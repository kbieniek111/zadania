package com.umcsuser.carrent.repositories.impl;

import com.umcsuser.carrent.models.Rental;
import com.umcsuser.carrent.models.User;
import com.umcsuser.carrent.models.Vehicle;
import com.umcsuser.carrent.repositories.RentalRepository;
import com.umcsuser.carrent.repositories.UserRepository;
import com.umcsuser.carrent.repositories.VehicleRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@Profile("jdbc")
public class RentalJdbcRepository implements RentalRepository {

    private final DataSource dataSource;
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;

    // Poprawiony konstruktor:
    public RentalJdbcRepository(
            DataSource dataSource,
            @Qualifier("vehicleJdbcRepository") VehicleRepository vehicleRepository,
            @Qualifier("userJdbcRepository") UserRepository userRepository) {
        this.dataSource = dataSource;
        this.vehicleRepository = vehicleRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Rental> findAll() {
        List<Rental> rentals = new ArrayList<>();
        Connection conn = DataSourceUtils.getConnection(dataSource);

        try (PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM rental");
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                rentals.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while reading rentals", e);
        } finally {
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
        return rentals;
    }

    @Override
    public Optional<Rental> findById(String id) {
        Connection conn = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM rental WHERE id = ?")) {
            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while reading rental by id", e);
        } finally {
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
        return Optional.empty();
    }

    @Override
    public Rental save(Rental rental) {
        if (rental.getId() == null || rental.getId().isEmpty()) {
            rental.setId(java.util.UUID.randomUUID().toString());
        }
        String sql = "INSERT INTO rental (id, vehicle_id, user_id, rent_date, return_date) VALUES (?, ?, ?, ?, ?) " +
                "ON CONFLICT (id) DO UPDATE SET return_date = EXCLUDED.return_date";

        Connection conn = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, rental.getId());
            pstmt.setString(2, rental.getVehicle().getId());
            pstmt.setString(3, rental.getUser().getId());
            pstmt.setString(4, rental.getRentDate());
            pstmt.setString(5, rental.getReturnDate());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while saving rental", e);
        } finally {
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
        return rental;
    }

    @Override
    public void deleteById(String id) {
        Connection conn = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM rental WHERE id = ?")) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while deleting rental", e);
        } finally {
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
    }

    @Override
    public Optional<Rental> findByVehicleIdAndReturnDateIsNull(String vehicleId) {
        String sql = "SELECT * FROM rental WHERE vehicle_id = ? AND return_date IS NULL";
        Connection conn = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, vehicleId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(map(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while finding active rental", e);
        } finally {
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
        return Optional.empty();
    }

    private Rental map(ResultSet rs) throws SQLException {
        Rental r = new Rental();
        r.setId(rs.getString("id"));

        String vehicleId = rs.getString("vehicle_id");
        Vehicle vehicle = vehicleRepository.findById(vehicleId).orElse(new Vehicle());
        r.setVehicle(vehicle);

        String userId = rs.getString("user_id");
        User user = userRepository.findById(userId).orElse(new User());
        r.setUser(user);

        r.setRentDate(rs.getString("rent_date"));
        r.setReturnDate(rs.getString("return_date"));
        return r;
    }
}