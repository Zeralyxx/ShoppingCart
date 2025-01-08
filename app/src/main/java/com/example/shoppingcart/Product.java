package com.example.shoppingcart;

public class Product {
    private String name;
    private double price;
    private int imageResId;
    private int imageNum;
    private int quantity;
    private String documentId;

    public Product() {
        // This is the no-argument constructor
    }

    public Product(String name, double price, int imageResId, int imageNum, int quantity) {
        this.name = name;
        this.price = price;
        this.imageResId = imageResId;
        this.imageNum = imageNum;
        this.quantity = quantity;
    }

    // Getters and setters for all fields
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getImageResId() {
        return imageResId;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }

    public int getImageNum() {
        return imageNum;
    }

    public void setImageNum(int imageNum) {
        this.imageNum = imageNum;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
}