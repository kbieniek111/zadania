package com.umcsuser.carrent;

import com.umcsuser.carrent.repositories.RentalRepository;
import com.umcsuser.carrent.repositories.UserRepository;
import com.umcsuser.carrent.repositories.VehicleCategoryConfigRepository;
import com.umcsuser.carrent.repositories.VehicleRepository;
import com.umcsuser.carrent.repositories.impl.*;
import com.umcsuser.carrent.services.*;

public class Main {
    public static void main(String[] args) {
        String mode = args.length > 0 ? args[0] : "json";
        System.out.println("Tryb: " + mode.toUpperCase());

        VehicleRepository vehicleRepository;
        UserRepository userRepository;
        RentalRepository rentalRepository;
        VehicleCategoryConfigRepository categoryConfigRepository = new VehicleCategoryConfigJsonRepository();

        if (mode.equalsIgnoreCase("hibernate")) {
            System.out.println("Ładowanie bazy danych hibernate");
            vehicleRepository = new VehicleHibernateRepository();
            userRepository = new UserHibernateRepository();
            rentalRepository = new RentalHibernateRepository();
        } else if (mode.equalsIgnoreCase("jdbc")) {
            System.out.println("Ładowanie bazy danych JDBC");
            vehicleRepository = new VehicleJdbcRepository();
            userRepository = new UserJdbcRepository();
            rentalRepository = new RentalJdbcRepository();
        } else {
            System.out.println("Ładowanie plików JSON");
            vehicleRepository = new VehicleJsonRepository();
            userRepository = new UserJsonRepository();
            rentalRepository = new RentalJsonRepository();
        }

        IAuthService authService = new AuthService(userRepository);
        IVehicleCategoryConfigService categoryConfigService = new VehicleCategoryConfigService(categoryConfigRepository);
        VehicleValidator vehicleValidator = new VehicleValidator(categoryConfigService);
        IVehicleService vehicleService = new VehicleService(vehicleRepository, rentalRepository, vehicleValidator);
        IRentalService rentalService = new RentalService(rentalRepository, vehicleRepository);
        IUserService userService = new UserService(userRepository, rentalService);

        UI ui = new UI(
                authService,
                vehicleService,
                rentalService,
                userService,
                categoryConfigService
        );
        ui.start();
    }
}