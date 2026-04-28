package com.umcsuser.carrent.services;

import com.umcsuser.carrent.models.Vehicle;
import com.umcsuser.carrent.repositories.RentalRepository;
import com.umcsuser.carrent.repositories.VehicleRepository;

import java.util.List;

public class VehicleService {
    private final VehicleRepository vehicleRepository;
    private final RentalRepository rentalRepository;
    private final VehicleValidator vehicleValidator;

    public VehicleService(VehicleRepository vehicleRepository, RentalRepository rentalRepository, VehicleValidator vehicleValidator) {
        this.vehicleRepository = vehicleRepository;
        this.rentalRepository = rentalRepository;
        this.vehicleValidator = vehicleValidator;
    }

    public Vehicle addVehicle(Vehicle vehicle) {
        vehicleValidator.validate(vehicle);
        return vehicleRepository.save(vehicle);
    }

    public void removeVehicle(String vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono pojazdu."));

        if (isVehicleRented(vehicleId)) {
            throw new IllegalStateException("Nie można usunąć pojazdu, bo jest aktualnie wypożyczony.");
        }
        vehicleRepository.deleteById(vehicle.getId());
    }

    public List<Vehicle> findAllVehicles() {
        return vehicleRepository.findAll();
    }

    public List<Vehicle> findAvailableVehicles() {
        return vehicleRepository.findAll().stream()
                .filter(v -> !isVehicleRented(v.getId()))
                .toList();
    }

    public Vehicle findById(String vehicleId) {
        return vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono pojazdu o ID: " + vehicleId));
    }

    public boolean isVehicleRented(String vehicleId) {
        return rentalRepository.findByVehicleIdAndReturnDateIsNull(vehicleId).isPresent();
    }
}