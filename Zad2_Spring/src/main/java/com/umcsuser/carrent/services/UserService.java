package com.umcsuser.carrent.services;

import com.umcsuser.carrent.models.User;
import com.umcsuser.carrent.repositories.UserRepository;
import java.util.List;

public class UserService {
    private final UserRepository userRepository;
    private final RentalService rentalService;

    public UserService(UserRepository userRepository, RentalService rentalService) {
        this.userRepository = userRepository;
        this.rentalService = rentalService;
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public User findById(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono użytkownika."));
    }

    public void deleteUser(String targetUserId, String loggedUserId) {
        User targetUser = findById(targetUserId);
        if (targetUserId.equals(loggedUserId)) {
            throw new IllegalStateException("Nie możesz usunąć samego siebie.");
        }

        if (rentalService.findActiveRentalByUserId(targetUserId).isPresent()) {
            throw new IllegalStateException("Nie można usunąć użytkownika, ponieważ ma wciąż wypożyczony pojazd.");
        }

        userRepository.deleteById(targetUserId);
    }
}