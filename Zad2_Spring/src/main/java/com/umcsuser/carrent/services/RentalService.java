package com.umcsuser.carrent.services;

import com.umcsuser.carrent.models.Rental;
import com.umcsuser.carrent.repositories.RentalRepository;
import com.umcsuser.carrent.repositories.VehicleRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RentalService {
    private final RentalRepository rentalRepository;
    private final VehicleRepository vehicleRepository;

    public RentalService(RentalRepository rentalRepository, VehicleRepository vehicleRepository) {
        this.rentalRepository = rentalRepository;
        this.vehicleRepository = vehicleRepository;
    }

    public void rentVehicle(String userId, String vehicleId) {
        if (vehicleHasActiveRental(vehicleId)) {
            throw new IllegalStateException("Pojazd jest już wypożyczony.");
        }
        Rental rental = Rental.builder()
                .userId(userId)
                .vehicleId(vehicleId)
                .rentDateTime(LocalDateTime.now().toString())
                .build();
        rentalRepository.save(rental);
    }

    public void returnVehicle(String userId) {
        Rental rental = findActiveRentalByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("Nie masz aktualnie wypożyczonego pojazdu."));
        rental.setReturnDateTime(LocalDateTime.now().toString());
        rentalRepository.save(rental);
    }

    public boolean vehicleHasActiveRental(String vehicleId) {
        return rentalRepository.findByVehicleIdAndReturnDateIsNull(vehicleId).isPresent();
    }

    public Optional<Rental> findActiveRentalByUserId(String userId) {
        return rentalRepository.findAll().stream()
                .filter(r -> r.getUserId().equals(userId) && r.isActive())
                .findFirst();
    }

    public List<Rental> findUserRentals(String userId) {
        return rentalRepository.findAll().stream()
                .filter(r -> r.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    public List<Rental> findAllRentals() {
        return rentalRepository.findAll();
    }
}