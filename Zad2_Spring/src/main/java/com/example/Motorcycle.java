package com.example;

public class Motorcycle extends Vehicle {
    private String category;

    public Motorcycle(String id, String brand, String model, int year, double price, boolean rented, String category) {
        super(id, brand, model, year, price, rented);
        this.category = category;
    }

    @Override
    public String toCSV() {
        return super.toCSV() + ";" + category;
    }

    @Override
    public Vehicle copy(){
        return new Motorcycle(getId(), getBrand(), getModel(), getYear(), getPrice(), isRented(), category);
    }

    @Override
    public String toString() {
        return super.toString() + ", Kategoria: " + category;
    }

}
