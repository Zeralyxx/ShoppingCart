package com.example.shoppingcart;

public class HistoryProduct {
    private Product product;
    private String receivedDate;

    public HistoryProduct(Product product, String receivedDate) {
        this.product = product;
        this.receivedDate = receivedDate;
    }

    public Product getProduct() {
        return product;
    }
    public String getReceivedDate() {
        return receivedDate;
    }
}