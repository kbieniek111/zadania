package com.umcsuser.carrent.repositories.impl;

import com.umcsuser.carrent.models.Vehicle;
import com.umcsuser.carrent.repositories.VehicleRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VehicleJdbcRepository implements VehicleRepository {
    private final String dbUrl = System.getenv("DB_URL");

    @Override
    public List<Vehicle> findAll() {
        List<Vehicle> vehicles = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM vehicle");
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) vehicles.add(map(rs));
        } catch (Exception e) { e.printStackTrace(); }
        return vehicles;
    }
    @Override
    public Optional<Vehicle> findById(String id) {
        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM vehicle WHERE id = ?")) {
            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return Optional.empty();
    }

    @Override
    public Vehicle save(Vehicle vehicle) {
        String sql = "INSERT INTO vehicle (id, brand, model, year) VALUES (?, ?, ?, ?) " +
                "ON CONFLICT (id) DO UPDATE SET brand = EXCLUDED.brand, model = EXCLUDED.model, year = EXCLUDED.year";
        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, vehicle.getId());
            pstmt.setString(2, vehicle.getBrand());
            pstmt.setString(3, vehicle.getModel());
            pstmt.setString(4, String.valueOf(vehicle.getYear())); // rocznik jako string
            pstmt.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
        return vehicle;
    }

    @Override
    public void deleteById(String id) {
        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM vehicle WHERE id = ?")) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private Vehicle map(ResultSet rs) throws Exception {
        Vehicle v = new Vehicle();
        v.setId(rs.getString("id"));
        v.setBrand(rs.getString("brand"));
        v.setModel(rs.getString("model"));
        v.setYear(Integer.parseInt(rs.getString("year")));
        return v;
    }
}