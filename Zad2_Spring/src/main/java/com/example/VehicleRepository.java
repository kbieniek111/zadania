package com.example;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class VehicleRepository implements IVehicleRepository {
    private List<Vehicle> vehicles = new ArrayList<>();
    private final String fileName = "vehicles.csv";

    public VehicleRepository() {
        load();
    }

    @Override
    public List<Vehicle> getVehicles() {
        List<Vehicle> copy = new ArrayList<>();
        for (Vehicle v : vehicles) {
            copy.add(v.copy());
        }
        return copy;
    }

    @Override
    public Vehicle getVehicle(String id) {
        for (Vehicle v : vehicles) {
            if (v.getId().equals(id)) {
                return v.copy();
            }
        }
        return null;
    }

    @Override
    public boolean add(Vehicle vehicle) {
        for (Vehicle v : vehicles) {
            if (v.getId().equals(vehicle.getId())) {
                return false;
            }
        }
        vehicles.add(vehicle.copy());
        save();
        return true;
    }

    @Override
    public boolean remove(String id) {
        boolean removed = vehicles.removeIf(v -> v.getId().equals(id));
        if (removed) {
            save();
        }
        return removed;
    }

    @Override
    public boolean rentVehicle(String id) {
        for (Vehicle v : vehicles) {
            if (v.getId().equals(id) && !v.isRented()) {
                v.setRented(true);
                save();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean returnVehicle(String id) {
        for (Vehicle v : vehicles) {
            if (v.getId().equals(id) && v.isRented()) {
                v.setRented(false);
                save();
                return true;
            }
        }
        return false;
    }

    public void save() {
        try (PrintWriter out = new PrintWriter(new FileWriter(fileName))) {
            for (Vehicle v : vehicles) {
                out.println((v instanceof Car ? "CAR;" : "MOTO;") + v.toCSV());
            }
        } catch (IOException e) {
            System.err.println("Błąd zapisu: " + e.getMessage());
        }
    }

    public void load() {
        vehicles.clear();
        File file = new File(fileName);
        if (!file.exists()) return;

        try (Scanner sc = new Scanner(file)) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(";", -1);
                String type = parts[0];

                if (type.equals("CAR")) {
                    vehicles.add(new Car(parts[1], parts[2], parts[3],
                            Integer.parseInt(parts[4]), Double.parseDouble(parts[5]), Boolean.parseBoolean(parts[6])));
                } else {
                    MotorcycleCategory cat = MotorcycleCategory.valueOf(parts[7]);
                    vehicles.add(new Motorcycle(parts[1], parts[2], parts[3],
                            Integer.parseInt(parts[4]), Double.parseDouble(parts[5]), Boolean.parseBoolean(parts[6]), cat));
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Brak pliku, zaczynamy z pustą listą.");
        }
    }
}