package com.example.shoppingcart;

// Product.java
public class Product {
    private String name;
    private double price;
    private int imageResId;
    private int quantity;

    // Constructor
    public Product(String name, double price, int imageResId) {
        this.name = name;
        this.price = price;
        this.imageResId = imageResId;
        this.quantity = 1; // Default quantity
    }
    public Product(String name, double price, int imageResId, int quantity) {
        this.name = name;
        this.price = price;
        this.imageResId = imageResId;
        this.quantity = quantity;
    }

    // Getters
    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getImageResId() { return imageResId; }
    public int getQuantity() { return quantity; }

    // Setters
    public void setQuantity(int quantity) { this.quantity = quantity; }
}