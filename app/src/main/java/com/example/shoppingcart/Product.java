package com.example.shoppingcart;

public class Product {
    private String name;
    private int imageResourceId;
    private double price;
    private int quantity;
    private int imageNum; // Add imageNum field

    public Product(String name, double price, int imageResourceId, int imageNum, int quantity) {
        this.name = name;
        this.price = price;
        this.imageResourceId = imageResourceId;
        this.imageNum = imageNum;
        this.quantity = quantity;
    }
    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImageResId() {
        return imageResourceId;
    }

    public void setImageResId(int imageResourceId) {
        this.imageResourceId = imageResourceId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getImageNum() {
        return imageNum;
    }

    public void setImageNum(int imageNum) {
        this.imageNum = imageNum;
    }
}