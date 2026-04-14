package com.umcsuser.carrent.ui;

import com.umcsuser.carrent.models.Rental;
import com.umcsuser.carrent.models.Role;
import com.umcsuser.carrent.models.User;
import com.umcsuser.carrent.models.Vehicle;
import com.umcsuser.carrent.repositories.RentalRepository;
import com.umcsuser.carrent.repositories.UserRepository;
import com.umcsuser.carrent.repositories.VehicleRepository;
import com.umcsuser.carrent.services.AuthService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class UI {
    private final VehicleRepository vehicleRepo;
    private final UserRepository userRepo;
    private final RentalRepository rentalRepo;
    private final AuthService authService;
    private final Scanner scanner;
    private User loggedUser;

    public UI(VehicleRepository vehicleRepo, UserRepository userRepo, RentalRepository rentalRepo, AuthService authService) {
        this.vehicleRepo = vehicleRepo;
        this.userRepo = userRepo;
        this.rentalRepo = rentalRepo;
        this.authService = authService;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        while (true) {
            if (loggedUser == null) {
                showLoginMenu();
            } else if (loggedUser.getRole() == Role.ADMIN) {
                showAdminMenu();
            } else {
                showUserMenu();
            }
        }
    }

    private void showLoginMenu() {
        System.out.println("\nSYSTEM WYPOŻYCZALNI");
        System.out.println("1. Zaloguj się");
        System.out.println("2. Zarejestruj się");
        System.out.println("0. Wyjście");
        System.out.print("Wybór: ");
        String choice = scanner.nextLine();

        if (choice.equals("1")) {
            System.out.print("Login: ");
            String login = scanner.nextLine();
            System.out.print("Hasło: ");
            String pass = scanner.nextLine();
            Optional<User> user = authService.login(login, pass);
            if (user.isPresent()) {
                loggedUser = user.get();
                System.out.println("Zalogowano pomyślnie!");
            } else {
                System.out.println("Błędny login lub hasło.");
            }
        } else if (choice.equals("2")) {
            System.out.print("Nowy login: ");
            String login = scanner.nextLine();
            System.out.print("Nowe hasło: ");
            String pass = scanner.nextLine();
            if (authService.register(login, pass)) {
                System.out.println("Zarejestrowano pomyślnie. Możesz się zalogować.");
            } else {
                System.out.println("Ten login jest już zajęty.");
            }
        } else if (choice.equals("0")) {
            System.exit(0);
        }
    }

    private void showAdminMenu() {
        System.out.println("\nMENU ADMINA");
        System.out.println("1. Pokaż wszystkie pojazdy");
        System.out.println("2. Usuń pojazd");
        System.out.println("0. Wyloguj");
        System.out.print("Wybór: ");
        String choice = scanner.nextLine();

        if (choice.equals("1")) {
            vehicleRepo.findAll().forEach(v -> {
                boolean isRented = rentalRepo.findByVehicleIdAndReturnDateIsNull(v.getId()).isPresent();
                System.out.println(v + (isRented ? " [WYPOŻYCZONY]" : " [DOSTĘPNY]"));
            });
        } else if (choice.equals("2")) {
            System.out.print("Podaj ID pojazdu do usunięcia: ");
            String id = scanner.nextLine();
            vehicleRepo.deleteById(id);
            System.out.println("Wykonano.");
        } else if (choice.equals("0")) {
            loggedUser = null;
        }
    }

    private void showUserMenu() {
        System.out.println("\nMENU UŻYTKOWNIKA");
        System.out.println("1. Pokaż dostępne pojazdy");
        System.out.println("2. Wypożycz pojazd");
        System.out.println("3. Zwróć pojazd");
        System.out.println("0. Wyloguj");
        System.out.print("Wybór: ");
        String choice = scanner.nextLine();

        if (choice.equals("1")) {
            List<Vehicle> all = vehicleRepo.findAll();
            for (Vehicle v : all) {
                if (rentalRepo.findByVehicleIdAndReturnDateIsNull(v.getId()).isEmpty()) {
                    System.out.println(v);
                }
            }
        } else if (choice.equals("2")) {
            System.out.print("Podaj ID pojazdu do wypożyczenia: ");
            String vId = scanner.nextLine();

            if (rentalRepo.findByVehicleIdAndReturnDateIsNull(vId).isPresent()) {
                System.out.println("Ten pojazd jest niedostępny!");
                return;
            }

            Rental newRental = Rental.builder()
                    .userId(loggedUser.getId())
                    .vehicleId(vId)
                    .rentDateTime(LocalDateTime.now().toString())
                    .build();
            rentalRepo.save(newRental);
            System.out.println("Wypożyczono pojazd!");

        } else if (choice.equals("3")) {
            System.out.print("Podaj ID pojazdu do zwrotu: ");
            String vId = scanner.nextLine();
            Optional<Rental> activeRental = rentalRepo.findByVehicleIdAndReturnDateIsNull(vId);

            if (activeRental.isPresent() && activeRental.get().getUserId().equals(loggedUser.getId())) {
                Rental rental = activeRental.get();
                rental.setReturnDateTime(LocalDateTime.now().toString());
                rentalRepo.save(rental);
                System.out.println("Pomyślnie zwrócono pojazd.");
            } else {
                System.out.println("Nie wypożyczyłeś takiego pojazdu.");
            }
        } else if (choice.equals("0")) {
            loggedUser = null;
        }
    }
}