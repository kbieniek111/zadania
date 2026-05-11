package com.umcsuser.carrent;

import com.umcsuser.carrent.repositories.RentalRepository;
import com.umcsuser.carrent.repositories.UserRepository;
import com.umcsuser.carrent.repositories.VehicleCategoryConfigRepository;
import com.umcsuser.carrent.repositories.VehicleRepository;
import com.umcsuser.carrent.repositories.impl.RentalJsonRepository;
import com.umcsuser.carrent.repositories.impl.UserJsonRepository;
import com.umcsuser.carrent.repositories.impl.VehicleCategoryConfigJsonRepository;
import com.umcsuser.carrent.repositories.impl.VehicleJsonRepository;
import com.umcsuser.carrent.repositories.impl.RentalJdbcRepository;
import com.umcsuser.carrent.repositories.impl.UserJdbcRepository;
import com.umcsuser.carrent.repositories.impl.VehicleJdbcRepository;
import com.umcsuser.carrent.services.AuthService;
import com.umcsuser.carrent.services.RentalService;
import com.umcsuser.carrent.services.UserService;
import com.umcsuser.carrent.services.VehicleCategoryConfigService;
import com.umcsuser.carrent.services.VehicleService;
import com.umcsuser.carrent.services.VehicleValidator;

public class Main {
    public static void main(String[] args) {
        String mode = args.length > 0 ? args[0] : "json";
        System.out.println("Tryb: " + mode.toUpperCase());

        VehicleRepository vehicleRepository;
        UserRepository userRepository;
        RentalRepository rentalRepository;
        VehicleCategoryConfigRepository categoryConfigRepository = new VehicleCategoryConfigJsonRepository();

        if (mode.equalsIgnoreCase("jdbc")) {
            vehicleRepository = new VehicleJdbcRepository();
            userRepository = new UserJdbcRepository();
            rentalRepository = new RentalJdbcRepository();
        } else {
            vehicleRepository = new VehicleJsonRepository();
            userRepository = new UserJsonRepository();
            rentalRepository = new RentalJsonRepository();
        }

        AuthService authService = new AuthService(userRepository);
        VehicleCategoryConfigService categoryConfigService = new VehicleCategoryConfigService(categoryConfigRepository);
        VehicleValidator vehicleValidator = new VehicleValidator(categoryConfigService);
        VehicleService vehicleService = new VehicleService(vehicleRepository, rentalRepository, vehicleValidator);
        RentalService rentalService = new RentalService(rentalRepository, vehicleRepository);
        UserService userService = new UserService(userRepository, rentalService);

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