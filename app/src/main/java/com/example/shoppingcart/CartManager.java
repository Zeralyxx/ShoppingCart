package com.example.shoppingcart;

import java.util.ArrayList;
import java.util.List;

public class CartManager {
    private static CartManager instance;
    private List<Product> cartItems;

    private CartManager() {
        cartItems = new ArrayList<>();
    }

    public static synchronized CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    public void addToCart(Product product) {
        // Check if the product is already in the cart
        for (Product cartItem : cartItems) {
            if (cartItem.getName().equals(product.getName())) {
                // If the product is already in the cart, update the quantity
                cartItem.setQuantity(cartItem.getQuantity() + product.getQuantity());
                return; // Exit the method
            }
        }
        // If the product is not in the cart, add it
        cartItems.add(new Product(product.getName(), product.getPrice(), product.getImageResId(), product.getImageNum(), product.getQuantity()));
    }

    public List<Product> getCartItems() {
        return cartItems;
    }

    public void clearCart() {
        cartItems.clear();
    }
}