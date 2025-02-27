package com.example.shoppingcart;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import java.util.List;
import com.google.firebase.auth.FirebaseAuth;
import java.util.HashMap;
import java.util.Map;
import android.content.Intent;

public class CheckoutActivity extends AppCompatActivity {

    private static final String TAG = "CheckoutActivity";
    private RecyclerView checkoutRecyclerView;
    private CheckoutAdapter checkoutAdapter;
    private CartManager cartManager;
    private ImageButton backButton;
    private TextView checkoutTotalPriceTextView;
    private RadioGroup paymentRadioGroup;
    private Button placeOrderButton;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private SharedPreferences sharedPreferences;
    private TextView addressTextView; // Added address TextView
    private ListenerRegistration addressListener; // Listener for address changes
    private boolean isAddressSet = false; // Flag to track if address is set
    private ImageButton editProfileButton; // Added edit profile button

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        sharedPreferences = getSharedPreferences("login_prefs", Context.MODE_PRIVATE);

        Log.d(TAG, "onCreate: SharedPreferences initialized");

        // Initialize RecyclerView
        checkoutRecyclerView = findViewById(R.id.checkoutRecyclerView);
        checkoutRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Get CartManager instance
        cartManager = CartManager.getInstance();

        // Initialize checkout adapter
        List<Product> cartItems = cartManager.getCartItems();
        checkoutAdapter = new CheckoutAdapter(this, cartItems);
        checkoutRecyclerView.setAdapter(checkoutAdapter);

        // Initialize back button
        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        // Initialize total price text view
        checkoutTotalPriceTextView = findViewById(R.id.checkoutTotalPriceTextView);
        updateTotalPrice(); // Update total price when the activity is created

        // Initialize payment radio group and place order button
        paymentRadioGroup = findViewById(R.id.paymentRadioGroup);
        placeOrderButton = findViewById(R.id.placeOrderButton);

        // Initialize address text view
        addressTextView = findViewById(R.id.addressTextView); // Initialize address TextView

        // Initialize edit profile button
        editProfileButton = findViewById(R.id.editProfileButton); // Initialize edit profile button
        editProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditProfileActivity.class);
            startActivity(intent);
        });

        // Set initial state of the place order button
        updatePlaceOrderButtonState();

        // Set up radio group listener
        paymentRadioGroup.setOnCheckedChangeListener((group, checkedId) -> updatePlaceOrderButtonState());

        // Set up place order button listener
        placeOrderButton = findViewById(R.id.placeOrderButton);
        placeOrderButton.setOnClickListener(v -> placeOrder());
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateTotalPrice();
        loadAddress();
        setupAddressListener(); // Start listening for address changes
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (addressListener != null) {
            addressListener.remove(); // Stop listening for address changes
        }
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
        if (selectedRadioButtonId == -1 || !isAddressSet) {
            placeOrderButton.setEnabled(false);
            placeOrderButton.setAlpha(0.5f); // Optional: make it look disabled
        } else {
            placeOrderButton.setEnabled(true);
            placeOrderButton.setAlpha(1.0f); // Optional: make it look enabled
        }
    }

    private void loadAddress() {
        String userId = sharedPreferences.getString("userId", null);
        if (userId != null) {
            db.collection("users").document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String address = documentSnapshot.getString("address");
                            if (address != null && !address.isEmpty()) {
                                addressTextView.setText("Address: " + address);
                                addressTextView.setTextColor(Color.BLACK); // Set color to black
                                isAddressSet = true; // Set flag to true
                            } else {
                                addressTextView.setText("Address: Not set");
                                addressTextView.setTextColor(Color.RED); // Set color to red
                                isAddressSet = false; // Set flag to false
                            }
                        } else {
                            addressTextView.setText("Address: Not set");
                            addressTextView.setTextColor(Color.RED); // Set color to red
                            isAddressSet = false; // Set flag to false
                        }
                        updatePlaceOrderButtonState(); // Update button state after loading address
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error loading address", e);
                        addressTextView.setText("Address: Not set");
                        addressTextView.setTextColor(Color.RED); // Set color to red
                        isAddressSet = false; // Set flag to false
                        updatePlaceOrderButtonState(); // Update button state on failure
                    });
        } else {
            Log.e(TAG, "User ID is null.");
            addressTextView.setText("Address: Not set");
            addressTextView.setTextColor(Color.RED); // Set color to red
            isAddressSet = false; // Set flag to false
            updatePlaceOrderButtonState(); // Update button state if user ID is null
        }
    }

    private void setupAddressListener() {
        String userId = sharedPreferences.getString("userId", null);
        if (userId != null) {
            addressListener = db.collection("users").document(userId)
                    .addSnapshotListener((documentSnapshot, e) -> {
                        if (e != null) {
                            Log.e(TAG, "Listen failed.", e);
                            return;
                        }
                        if (documentSnapshot != null && documentSnapshot.exists()) {
                            String address = documentSnapshot.getString("address");
                            if (address != null && !address.isEmpty()) {
                                addressTextView.setText("Address: " + address);
                                addressTextView.setTextColor(Color.BLACK); // Set color to black
                                isAddressSet = true; // Set flag to true
                            } else {
                                addressTextView.setText("Address: Not set");
                                addressTextView.setTextColor(Color.RED); // Set color to red
                                isAddressSet = false; // Set flag to false
                            }
                        } else {
                            addressTextView.setText("Address: Not set");
                            addressTextView.setTextColor(Color.RED); // Set color to red
                            isAddressSet = false; // Set flag to false
                        }
                        updatePlaceOrderButtonState(); // Update button state after address change
                    });
        }
    }

    private void placeOrder() {
        Log.d(TAG, "placeOrder: Attempting to place order");
        int selectedRadioButtonId = paymentRadioGroup.getCheckedRadioButtonId();
        if (selectedRadioButtonId != -1) {
            // Payment method is selected, proceed with order placement
            Toast.makeText(this, "Order placed!", Toast.LENGTH_SHORT).show();

            // Get the cart items
            List<Product> cartItems = cartManager.getCartItems();

            // Get the current user's username from SharedPreferences
            String userId = sharedPreferences.getString("userId", "");
            Log.d(TAG, "placeOrder: Retrieved userId from SharedPreferences: "+ userId);

            // Get the user's name from SharedPreferences
            String name = sharedPreferences.getString("username", "");
            Log.d(TAG, "placeOrder: Retrieved username from SharedPreferences: " + name);

            // Iterate through the cart items and add them to Firestore
            for (Product product : cartItems) {
                Map<String, Object> order = new HashMap<>();
                order.put("username", userId);
                order.put("name", name); // Add the name field
                order.put("imageNum", product.getImageNum()); // Use the correct imageNum
                order.put("productName", product.getName());
                order.put("quantity", product.getQuantity());
                order.put("price", product.getPrice()); // Add the individual price to the order

                db.collection("toShip")
                        .add(order)
                        .addOnSuccessListener(documentReference -> {
                            // Order added successfully
                            Log.d(TAG, "placeOrder: Order added to Firestore successfully");
                        })
                        .addOnFailureListener(e -> {
                            // Handle error
                            Log.e(TAG, "placeOrder: Error adding order to Firestore.", e);
                            Toast.makeText(this, "Error adding order to Firestore.", Toast.LENGTH_SHORT).show();
                        });
            }

            // Clear the cart
            cartManager.clearCart();

            // Navigate to AccountFragmentIn
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("navigateToAccount", true);
            intent.putExtra("userId", userId);
            intent.putExtra("username", name);
            startActivity(intent);

            // Finish the checkout activity
            finish();
        } else {
            // No payment method selected, show a message
            Toast.makeText(this, "Please select a payment method", Toast.LENGTH_SHORT).show();
        }
    }
}