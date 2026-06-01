package com.umcsuser.carrent.services;

import com.umcsuser.carrent.models.Vehicle;
import com.umcsuser.carrent.repositories.RentalRepository;
import com.umcsuser.carrent.repositories.VehicleRepository;

import java.util.List;
@org.springframework.stereotype.Service
@org.springframework.transaction.annotation.Transactional
public class VehicleService implements IVehicleService {
    private final VehicleRepository vehicleRepository;
    private final RentalRepository rentalRepository;
    private final VehicleValidator vehicleValidator;

    public VehicleService(VehicleRepository vehicleRepository, RentalRepository rentalRepository, VehicleValidator vehicleValidator) {
        this.vehicleRepository = vehicleRepository;
        this.rentalRepository = rentalRepository;
        this.vehicleValidator = vehicleValidator;
    }

    @Override
    public Vehicle addVehicle(Vehicle vehicle) {
        vehicleValidator.validate(vehicle);
        return vehicleRepository.save(vehicle);
    }

    @Override
    public void removeVehicle(String vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono pojazdu."));

        if (isVehicleRented(vehicleId)) {
            throw new IllegalStateException("Nie można usunąć pojazdu, bo jest aktualnie wypożyczony.");
        }
        vehicleRepository.deleteById(vehicle.getId());
    }

    @Override
    public List<Vehicle> findAllVehicles() {
        return vehicleRepository.findAll();
    }

    @Override
    public List<Vehicle> findAvailableVehicles() {
        return vehicleRepository.findAll().stream()
                .filter(v -> !isVehicleRented(v.getId()))
                .toList();
    }

    @Override
    public Vehicle findById(String vehicleId) {
        return vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono pojazdu o ID: " + vehicleId));
    }

    @Override
    public boolean isVehicleRented(String vehicleId) {
        return rentalRepository.findByVehicleIdAndReturnDateIsNull(vehicleId).isPresent();
    }
}