package com.example.shoppingcart;

public class Product {
    private String name;
    private double price;
    private int imageResId;
    private int imageNum;
    private int quantity;
    private String documentId;

    public Product(String name, double price, int imageResId, int imageNum, int quantity) {
        this.name = name;
        this.price = price;
        this.imageResId = imageResId;
        this.imageNum = imageNum;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getImageResId() {
        return imageResId;
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

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
}