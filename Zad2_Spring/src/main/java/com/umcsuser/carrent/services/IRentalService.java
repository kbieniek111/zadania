package com.umcsuser.carrent.services;

import com.umcsuser.carrent.models.Rental;

import java.util.List;
import java.util.Optional;

public interface IRentalService {
    void rentVehicle(String userId, String vehicleId);

    void returnVehicle(String userId);

    boolean vehicleHasActiveRental(String vehicleId);

    Optional<Rental> findActiveRentalByUserId(String userId);

    List<Rental> findUserRentals(String userId);
    List<Rental> findAllRentals();
}
