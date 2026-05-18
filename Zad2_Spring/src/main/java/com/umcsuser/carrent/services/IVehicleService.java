package com.umcsuser.carrent.services;

import com.umcsuser.carrent.models.Vehicle;

import java.util.List;

public interface IVehicleService {
    Vehicle addVehicle(Vehicle vehicle);

    void removeVehicle(String vehicleId);

    List<Vehicle> findAllVehicles();

    List<Vehicle> findAvailableVehicles();

    Vehicle findById(String vehicleId);

    boolean isVehicleRented(String vehicleId);
}
