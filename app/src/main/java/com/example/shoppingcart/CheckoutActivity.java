package com.example.shoppingcart;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CheckoutActivity extends AppCompatActivity {

    private RecyclerView checkoutRecyclerView;
    private CheckoutAdapter checkoutAdapter;
    private CartManager cartManager;
    private ImageButton backButton;
    private TextView checkoutTotalPriceTextView;
    private RadioGroup paymentRadioGroup;
    private Button placeOrderButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        // Initialize RecyclerView
        checkoutRecyclerView = findViewById(R.id.checkoutRecyclerView);
        checkoutRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Get CartManager instance
        cartManager = CartManager.getInstance();

        // Initialize checkout adapter
        List<Product> cartItems = cartManager.getCartItems();
        checkoutAdapter = new CheckoutAdapter(cartItems);
        checkoutRecyclerView.setAdapter(checkoutAdapter);

        // Initialize back button
        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        // Initialize total price text view
        checkoutTotalPriceTextView = findViewById(R.id.checkoutTotalPriceTextView);
        updateTotalPrice();

        // Initialize payment radio group and place order button
        paymentRadioGroup = findViewById(R.id.paymentRadioGroup);
        placeOrderButton = findViewById(R.id.placeOrderButton);

        // Set initial state of the place order button
        updatePlaceOrderButtonState();

        // Set up radio group listener
        paymentRadioGroup.setOnCheckedChangeListener((group, checkedId) -> updatePlaceOrderButtonState());

        // Set up place order button listener
        placeOrderButton.setOnClickListener(v -> placeOrder());
    }

    private void updateTotalPrice() {
        List<Product> cartItems = cartManager.getCartItems();
        double totalPrice = 0;
        for (Product product : cartItems) {
            totalPrice += product.getPrice() * product.getQuantity();
        }
        checkoutTotalPriceTextView.setText("₱" + String.format("%.2f", totalPrice));
    }

    private void updatePlaceOrderButtonState() {
        int selectedRadioButtonId = paymentRadioGroup.getCheckedRadioButtonId();
        if (selectedRadioButtonId == -1) {
            placeOrderButton.setEnabled(false);
            placeOrderButton.setAlpha(0.5f); // Optional: make it look disabled
        } else {
            placeOrderButton.setEnabled(true);
            placeOrderButton.setAlpha(1.0f); // Optional: make it look enabled
        }
    }

    private void placeOrder() {
        int selectedRadioButtonId = paymentRadioGroup.getCheckedRadioButtonId();
        if (selectedRadioButtonId != -1) {
            // Payment method is selected, proceed with order placement
            Toast.makeText(this, "Order placed!", Toast.LENGTH_SHORT).show();
            // You can add your order processing logic here
        } else {
            // No payment method selected, show a message
            Toast.makeText(this, "Please select a payment method", Toast.LENGTH_SHORT).show();
        }
    }
}