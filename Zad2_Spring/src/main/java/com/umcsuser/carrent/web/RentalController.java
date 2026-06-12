package com.umcsuser.carrent.web;

import com.umcsuser.carrent.dto.RentalRequest;
import com.umcsuser.carrent.models.Rental;
import com.umcsuser.carrent.models.User;
import com.umcsuser.carrent.repositories.UserRepository;
import com.umcsuser.carrent.services.IRentalService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rentals")
public class RentalController {

    private final IRentalService rentalService;
    private final UserRepository userRepository;

    public RentalController(
            IRentalService rentalService,
            @Qualifier("userJdbcRepository") UserRepository userRepository) {

        this.rentalService = rentalService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<Rental> list() {
        return rentalService.findAllRentals();
    }

    @GetMapping("/users/{userId}")
    public List<Rental> userRentals(@PathVariable String userId) {
        return rentalService.findUserRentals(userId);
    }

    @PostMapping("/rent")
    public ResponseEntity<Rental> rent(
            @RequestBody RentalRequest rentalRequest,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByLogin(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String userId = user.getId();
        String vehicleId = rentalRequest.vehicleId();

        rentalService.rentVehicle(userId, vehicleId);
        Rental rental = rentalService.findUserRentals(userId).stream()
                .filter(r -> r.getVehicle() != null && r.getVehicle().getId().equals(vehicleId) && r.getReturnDate() == null)
                .findFirst()
                .orElse(null);

        return ResponseEntity.status(HttpStatus.CREATED).body(rental);
    }

    @PostMapping("/return")
    public ResponseEntity<Rental> returnVehicle(
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByLogin(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String userId = user.getId();

        Rental activeRental = rentalService.findUserRentals(userId).stream()
                .filter(r -> r.getReturnDate() == null)
                .findFirst()
                .orElse(null);

        rentalService.returnVehicle(userId);

        if (activeRental != null) {
            Rental updatedRental = rentalService.findAllRentals().stream()
                    .filter(r -> r.getId().equals(activeRental.getId()))
                    .findFirst()
                    .orElse(null);
            return ResponseEntity.ok(updatedRental);
        }

        return ResponseEntity.ok().build();
    }
}