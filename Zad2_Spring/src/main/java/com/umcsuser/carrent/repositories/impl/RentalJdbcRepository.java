package com.umcsuser.carrent.repositories.impl;

import com.umcsuser.carrent.models.Rental;
import com.umcsuser.carrent.repositories.RentalRepository;
import java.sql.*;
import java.util.*;

public class RentalJdbcRepository implements RentalRepository {
    private final String dbUrl = System.getenv("DB_URL");

    @Override
    public List<Rental> findAll() {
        List<Rental> rentals = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM rental");
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) rentals.add(map(rs));
        } catch (Exception e) { e.printStackTrace(); }
        return rentals;
    }

    @Override
    public Optional<Rental> findById(String id) {
        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM rental WHERE id = ?")) {
            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return Optional.empty();
    }

    @Override
    public Rental save(Rental rental) {
        if (rental.getId() == null || rental.getId().isEmpty()) {
            rental.setId(java.util.UUID.randomUUID().toString());
        }
        String sql = "INSERT INTO rental (id, vehicle_id, user_id, rent_date, return_date) VALUES (?, ?, ?, ?, ?) " +
                "ON CONFLICT (id) DO UPDATE SET return_date = EXCLUDED.return_date";
        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, rental.getId());
            pstmt.setString(2, rental.getVehicleId());
            pstmt.setString(3, rental.getUserId());
            pstmt.setString(4, rental.getRentDateTime());
            pstmt.setString(5, rental.getReturnDateTime());
            pstmt.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
        return rental;
    }

    @Override
    public void deleteById(String id) {
        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM rental WHERE id = ?")) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }

    @Override
    public Optional<Rental> findByVehicleIdAndReturnDateIsNull(String vehicleId) {
        String sql = "SELECT * FROM rental WHERE vehicle_id = ? AND return_date IS NULL";
        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, vehicleId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(map(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    private Rental map(ResultSet rs) throws Exception {
        Rental r = new Rental();
        r.setId(rs.getString("id"));
        r.setVehicleId(rs.getString("vehicle_id"));
        r.setUserId(rs.getString("user_id"));
        r.setRentDateTime(rs.getString("rent_date"));
        r.setReturnDateTime(rs.getString("return_date"));
        return r;
    }
}