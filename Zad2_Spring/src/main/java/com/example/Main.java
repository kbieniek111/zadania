package com.example;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Car f = new Car("100", "f", "f", 100, 100, true);
        Motorcycle f2 = new Motorcycle("100", "f", "f", 100, 100, true, MotorcycleCategory.A);
        Motorcycle f3 = new Motorcycle("101", "f", "f", 100, 100, true, MotorcycleCategory.A);

        System.out.println(f2.equals(f));
        System.out.println("f: " + f.hashCode() + " f2: " + f2.hashCode());
        System.out.println(f3.equals(f));
        System.out.println("f: " + f.hashCode() + " f3: " + f3.hashCode());

        IVehicleRepository vehicleRepo = new VehicleRepository();
        IUserRepository userRepo = new UserRepository();
        Authentication auth = new Authentication(userRepo);
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nJESTEŚ W EKRANIE STARTOWYM!");
            System.out.println("1. Zaloguj się");
            System.out.println("2. Zarejestruj się");
            System.out.print("Opcja: ");
            String startChoice = scanner.nextLine();




            if(startChoice.equals("2")) {
                System.out.println("\n REJESTRACJA");
                System.out.print("Podaj login: ");
                String newLogin = scanner.nextLine();

                if(userRepo.getUser(newLogin) != null) {
                    System.out.println("Błąd! Taki użytkownik już istnieje!");
                    continue;
                }
                System.out.print("Podaj hasło: ");
                String newPassword = scanner.nextLine();
                String hashedPassword = Authentication.hashPassword(newPassword);

                User newUser = new User(newLogin, hashedPassword, Role.USER, "NONE");
                if (userRepo.add(newUser)) {
                    System.out.println("Sukces! Możesz się teraz zalogować.");
                } else {
                    System.out.println("Błąd rejestracji.");
                }
                continue;
                } else if (!startChoice.equals("1")) {
                System.out.println("Nieprawidłowy wybór.");
                continue;
            }
            System.out.println("JESTEŚ W EKRANIE LOGOWANIA!");
            System.out.print("Login: ");
            String login = scanner.nextLine();

            System.out.print("Hasło: ");
            String password = scanner.nextLine();

            User currentUser = auth.authenticate(login, password);

            if (currentUser == null) {
                System.out.println("Błędny login lub hasło!");
                continue;
            }

            boolean loggedIn = true;
            while (loggedIn) {
                System.out.println("\nMENU (" + currentUser.getRole() + ")");

                if (currentUser.getRole() == Role.ADMIN) {
                    System.out.println("1. Dodaj pojazd");
                    System.out.println("2. Usuń pojazd");
                    System.out.println("3. Lista pojazdów");
                    System.out.println("4. Lista użytkowników");
                    System.out.println("5. Wyloguj");
                    System.out.println("6. Usuń użytkownika");
                    System.out.print("Wybierz opcję: ");
                    String choice = scanner.nextLine();

                    if (choice.equals("1")) {
                        System.out.print("Typ (1-Auto, 2-Motocykl): ");
                        String type = scanner.nextLine();
                        System.out.print("ID: "); String id = scanner.nextLine();
                        System.out.print("Marka: "); String brand = scanner.nextLine();
                        System.out.print("Model: "); String model = scanner.nextLine();
                        System.out.print("Rok: "); int year = Integer.parseInt(scanner.nextLine());
                        System.out.print("Cena: "); double price = Double.parseDouble(scanner.nextLine());

                        if (type.equals("1")) {
                            vehicleRepo.add(new Car(id, brand, model, year, price, false));
                        } else {
                            System.out.print("Kategoria: "); String category = scanner.nextLine();
                            vehicleRepo.add(new Motorcycle(id, brand, model, year, price, false, MotorcycleCategory.valueOf(category.toUpperCase())));
                        }
                        System.out.println("Dodano pojazd.");
                    } else if (choice.equals("2")) {
                        System.out.print("Podaj ID do usunięcia: ");
                        if (vehicleRepo.remove(scanner.nextLine())) System.out.println("Usunięto.");
                        else System.out.println("Nie znaleziono pojazdu.");
                    } else if (choice.equals("3")) {
                        vehicleRepo.getVehicles().forEach(System.out::println);
                    } else if (choice.equals("4")) {
                        userRepo.getUsers().forEach(System.out::println);
                    } else if (choice.equals("5")) {
                        loggedIn = false;
                    } else if (choice.equals("6")) {
                    System.out.print("Podaj nazwe użytkownika do usuniecia: ");
                    String userToRemove = scanner.nextLine();
                    if (userRepo.remove(userToRemove)) {
                        System.out.println("Użytkownik został usunięty.");
                    } else {
                        System.out.println("Błąd: Użytkownik nie istnieje lub ma wypożyczony pojazd!");
                    }
                }

                } else {
                    System.out.println("1. Wypożycz pojazd");
                    System.out.println("2. Zwróć pojazd");
                    System.out.println("3. Moje dane");
                    System.out.println("4. Wyloguj");
                    System.out.print("Wybierz opcję: ");
                    String choice = scanner.nextLine();

                    if (choice.equals("1")) {
                        System.out.print("Podaj ID do wypożyczenia: ");
                        String id = scanner.nextLine();
                        if (vehicleRepo.rentVehicle(id)) {
                            currentUser.setRentedVehicleId(id);
                            userRepo.update(currentUser);
                            System.out.println("Wypożyczono pojazd.");
                        } else {
                            System.out.println("Pojazd niedostępny lub nie istnieje.");
                        }
                    } else if (choice.equals("2")) {
                        String rentedId = currentUser.getRentedVehicleId();
                        if (!rentedId.equals("NONE") && vehicleRepo.returnVehicle(rentedId)) {
                            currentUser.setRentedVehicleId("NONE");
                            userRepo.update(currentUser);
                            System.out.println("Pojazd zwrócony.");
                        } else {
                            System.out.println("Nie masz wypożyczonego pojazdu.");
                        }
                    } else if (choice.equals("3")) {
                        System.out.println(currentUser);
                        if (!currentUser.getRentedVehicleId().equals("NONE")) {
                            Vehicle v = vehicleRepo.getVehicle(currentUser.getRentedVehicleId());
                            System.out.println("Szczegóły pojazdu: " + v);
                        }
                    } else if (choice.equals("4")) {
                        loggedIn = false;
                    }
                }
            }
        }
    }
}