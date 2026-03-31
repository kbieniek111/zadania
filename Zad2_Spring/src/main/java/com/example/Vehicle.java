package com.example;

public abstract  class Vehicle {
    private String id;
    private String brand;
    private String model;
    private int year;
    private double price;
    private boolean rented;


    public Vehicle(String id, String brand, String model, int year, double price, boolean rented) {
        this.id = id;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.price = price;
        this.rented = rented;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isRented() {
        return rented;
    }

    public void setRented(boolean rented) {
        this.rented = rented;
    }

    public abstract Vehicle copy();
    public String toCSV() {
        return id + ";" + brand + ";" + model + ";" + year + ";" + price + ";" + rented;
    }

    public String toString() {
        return "ID: " + id + ", Marka: " + brand + ", Model: " + model +
                ", Rok Produkcji: " + year + ", Cena: " + price + ", Czy wypożyczony: " + (rented ? "TAK" : "NIE");
    }
    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vehicle v)) return false;
        return id.equals(v.id);
    }

    @Override
    public final int hashCode() {
        return id.hashCode();
    }

}
