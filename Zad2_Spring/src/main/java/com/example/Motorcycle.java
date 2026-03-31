package com.example;

public class Motorcycle extends Vehicle {
    private MotorcycleCategory category;

    public Motorcycle(String id, String brand, String model, int year, double price, boolean rented, MotorcycleCategory category) {
        super(id, brand, model, year, price, rented);
        this.category = category;
    }

    @Override
    public String toCSV() {
        return super.toCSV() + ";" + category.name();
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
