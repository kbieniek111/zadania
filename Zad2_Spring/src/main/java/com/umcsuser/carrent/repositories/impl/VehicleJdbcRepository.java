package com.umcsuser.carrent.repositories.impl;

import com.umcsuser.carrent.models.Vehicle;
import com.umcsuser.carrent.repositories.VehicleRepository;
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
public class VehicleJdbcRepository implements VehicleRepository {

    private final DataSource dataSource;

    public VehicleJdbcRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<Vehicle> findAll() {
        List<Vehicle> vehicles = new ArrayList<>();
        Connection conn = DataSourceUtils.getConnection(dataSource);

        try (PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM vehicle");
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                vehicles.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while reading vehicles", e);
        } finally {
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
        return vehicles;
    }

    @Override
    public Optional<Vehicle> findById(String id) {
        Connection conn = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM vehicle WHERE id = ?")) {
            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while reading vehicle by id", e);
        } finally {
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
        return Optional.empty();
    }

    @Override
    public Vehicle save(Vehicle vehicle) {
        String sql = "INSERT INTO vehicle (id, category, brand, model, year, plate) VALUES (?, ?, ?, ?, ?, ?) " +
                "ON CONFLICT (id) DO UPDATE SET category = EXCLUDED.category, brand = EXCLUDED.brand, " +
                "model = EXCLUDED.model, year = EXCLUDED.year, plate = EXCLUDED.plate";

        Connection conn = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, vehicle.getId());
            pstmt.setString(2, vehicle.getCategory());
            pstmt.setString(3, vehicle.getBrand());
            pstmt.setString(4, vehicle.getModel());
            pstmt.setInt(5, vehicle.getYear());
            pstmt.setString(6, vehicle.getPlate());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while saving vehicle", e);
        } finally {
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
        return vehicle;
    }

    @Override
    public void deleteById(String id) {
        Connection conn = DataSourceUtils.getConnection(dataSource);
        try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM vehicle WHERE id = ?")) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred while deleting vehicle", e);
        } finally {
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
    }

    private Vehicle map(ResultSet rs) throws SQLException {
        Vehicle v = new Vehicle();
        v.setId(rs.getString("id"));
        v.setBrand(rs.getString("brand"));
        v.setModel(rs.getString("model"));
        v.setYear(rs.getInt("year"));
        v.setCategory(rs.getString("category"));
        v.setPlate(rs.getString("plate"));
        return v;
    }
}