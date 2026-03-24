package com.example;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class UserRepository implements IUserRepository {
    private List<User> users = new ArrayList<>();
    private final String fileName = "users.csv";

    public UserRepository() {
        load();
    }

    @Override
    public User getUser(String login) {
        for (User u : users) {
            if (u.getLogin().equals(login)) {
                return u.copy();
            }
        }
        return null;
    }

    @Override
    public List<User> getUsers() {
        List<User> copy = new ArrayList<>();
        for (User u : users) {
            copy.add(u.copy());
        }
        return copy;
    }

    @Override
    public boolean update(User updatedUser) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getLogin().equals(updatedUser.getLogin())) {
                users.set(i, updatedUser.copy());
                save();
                return true;
            }
        }
        return false;
    }

    @Override
    public void save() {
        try (PrintWriter out = new PrintWriter(new FileWriter(fileName))) {
            for (User u :users) {
                out.println(u.toCSV());
            }
        } catch (IOException e) {
            System.out.println("Błąd zapisu.");
        }
    }

    @Override
    public void load() {
        users.clear();
        try (Scanner sc = new Scanner(new File(fileName))) {
            while (sc.hasNextLine()) {
                String[] parts = sc.nextLine().split(";", -1);
                users.add(new User(parts[0], parts[1], Role.valueOf(parts[2]), parts[3]));
            }
        } catch (FileNotFoundException e) {
            System.out.println("Brak pliku " + fileName);
        }
    }
}