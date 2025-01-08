package com.example.shoppingcart;

public class CancelledProduct {
    private Product product;
    private String cancellationDate;

    public CancelledProduct(Product product, String cancellationDate) {
        this.product = product;
        this.cancellationDate = cancellationDate;
    }

    public Product getProduct() {
        return product;
    }
    public String getCancellationDate() {
        return cancellationDate;
    }
}