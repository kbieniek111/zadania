package com.umcsuser.carrent.web;

import com.umcsuser.carrent.models.Rental;
import com.umcsuser.carrent.services.IRentalService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rentals")
public class RentalController {

    private final IRentalService rentalService;

    public RentalController(IRentalService rentalService) {
        this.rentalService = rentalService;
    }

    @GetMapping
    public List<Rental> list() {
        return rentalService.findAllRentals();
    }

    @GetMapping("/users/{userId}")
    public List<Rental> userRentals(@PathVariable String userId) {
        return rentalService.findUserRentals(userId);
    }

    @PostMapping("/users/{userId}/rent/{vehicleId}")
    public Rental rent(@PathVariable String userId, @PathVariable String vehicleId) {

        rentalService.rentVehicle(userId, vehicleId);
        return rentalService.findUserRentals(userId).stream()
                .filter(r -> r.getVehicle() != null && r.getVehicle().getId().equals(vehicleId) && r.getReturnDate() == null)
                .findFirst()
                .orElse(null);
    }

    @PostMapping("/users/{userId}/return")
    public Rental returnVehicle(@PathVariable String userId) {
        Rental activeRental = rentalService.findUserRentals(userId).stream()
                .filter(r -> r.getReturnDate() == null)
                .findFirst()
                .orElse(null);

        rentalService.returnVehicle(userId);

        if (activeRental != null) {
            return rentalService.findAllRentals().stream()
                    .filter(r -> r.getId().equals(activeRental.getId()))
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }
}