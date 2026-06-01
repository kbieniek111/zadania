package com.umcsuser.carrent.services;

import com.umcsuser.carrent.models.Rental;
import com.umcsuser.carrent.models.User;
import com.umcsuser.carrent.models.Vehicle;
import com.umcsuser.carrent.repositories.RentalRepository;
import com.umcsuser.carrent.repositories.VehicleRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
@org.springframework.stereotype.Service
@org.springframework.transaction.annotation.Transactional
public class RentalService implements IRentalService {
    private final RentalRepository rentalRepository;
    private final VehicleRepository vehicleRepository;

    public RentalService(RentalRepository rentalRepository, VehicleRepository vehicleRepository) {
        this.rentalRepository = rentalRepository;
        this.vehicleRepository = vehicleRepository;
    }

    @Override
    public void rentVehicle(String userId, String vehicleId) {
        if (vehicleHasActiveRental(vehicleId)) {
            throw new IllegalStateException("Pojazd jest już wypożyczony.");
        }

        User tempUser = new User();
        tempUser.setId(userId);

        Vehicle tempVehicle = new Vehicle();
        tempVehicle.setId(vehicleId);

        Rental rental = Rental.builder()
                .user(tempUser)
                .vehicle(tempVehicle)
                .rentDate(LocalDateTime.now().toString())
                .build();
        rentalRepository.save(rental);
    }

    @Override
    public void returnVehicle(String userId) {
        Rental rental = findActiveRentalByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("Nie masz aktualnie wypożyczonego pojazdu."));
        rental.setReturnDate(LocalDateTime.now().toString());
        rentalRepository.save(rental);
    }

    @Override
    public boolean vehicleHasActiveRental(String vehicleId) {
        return rentalRepository.findByVehicleIdAndReturnDateIsNull(vehicleId).isPresent();
    }

    @Override
    public Optional<Rental> findActiveRentalByUserId(String userId) {
        return rentalRepository.findAll().stream()
                .filter(r -> r.getUser() != null && r.getUser().getId().equals(userId) && r.isActive())
                .findFirst();
    }

    @Override
    public List<Rental> findUserRentals(String userId) {
        return rentalRepository.findAll().stream()
                .filter(r -> r.getUser() != null && r.getUser().getId().equals(userId))
                .collect(Collectors.toList());
    }

    public List<Rental> findAllRentals() {
        return rentalRepository.findAll();
    }
}