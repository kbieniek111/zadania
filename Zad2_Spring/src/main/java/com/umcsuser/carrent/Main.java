package com.umcsuser.carrent;

import com.umcsuser.carrent.repositories.RentalRepository;
import com.umcsuser.carrent.repositories.UserRepository;
import com.umcsuser.carrent.repositories.VehicleRepository;
import com.umcsuser.carrent.repositories.impl.RentalJsonRepository;
import com.umcsuser.carrent.repositories.impl.UserJsonRepository;
import com.umcsuser.carrent.repositories.impl.VehicleJsonRepository;
import com.umcsuser.carrent.services.AuthService;
import com.umcsuser.carrent.ui.UI;

public class Main {
    public static void main(String[] args) {
        UserRepository userRepository = new UserJsonRepository();
        VehicleRepository vehicleRepository = new VehicleJsonRepository();
        RentalRepository rentalRepository = new RentalJsonRepository();

        AuthService authService = new AuthService(userRepository);

        UI ui = new UI(vehicleRepository, userRepository, rentalRepository, authService);
        ui.start();
    }
}