package com.example.shoppingcart;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ToShipActivity extends AppCompatActivity {

    private static final String TAG = "ToShipActivity";
    private ImageButton backButton;
    private RecyclerView toShipRecyclerView;
    private ToShipAdapter toShipAdapter;
    private List<Product> orderList;
    private static final String PREFS_NAME = "MyPrefs";
    private static final String ORDER_LIST_KEY = "orderList";
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private ListenerRegistration firestoreListener; // Add ListenerRegistration
    private String firestoreUsername;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_ship);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        sharedPreferences = getSharedPreferences("login_prefs", Context.MODE_PRIVATE);

        backButton = findViewById(R.id.backButton);
        toShipRecyclerView = findViewById(R.id.toShipRecyclerView);

        // Set up RecyclerView
        toShipRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ToShipActivity.this, MainActivity.class);
                intent.putExtra("fragment_to_load", "account"); // Add extra to indicate AccountFragment
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadOrderListFromFirestore();
    }

    private void loadOrderListFromFirestore() {
        orderList = new ArrayList<>();
        String userId = sharedPreferences.getString("userId", "");
        Log.d(TAG, "Retrieved userId from SharedPreferences: " + userId);

        if (firestoreListener != null) {
            firestoreListener.remove(); // Remove the old listener
        }
        orderList.clear(); // Clear the order list

        if (!userId.isEmpty()) {
            firestoreListener = db.collection("toShip")
                    .whereEqualTo("username", userId)
                    .addSnapshotListener((querySnapshot, e) -> {
                        if (e != null) {
                            // Handle error
                            Toast.makeText(this, "Error loading orders from Firestore.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        orderList.clear(); // Clear the list before adding new data
                        if (querySnapshot != null) {
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                String productName = document.getString("productName");
                                double price = document.getDouble("price");
                                int quantity = document.getLong("quantity").intValue();
                                int imageNum = document.getLong("imageNum").intValue();
                                int imageResourceId = getImageResource(imageNum);

                                Product product = new Product(productName, price, imageResourceId, imageNum, quantity);
                                product.setQuantity(quantity);
                                orderList.add(product);
                            }
                        }
                        if (toShipAdapter == null) {
                            toShipAdapter = new ToShipAdapter(orderList);
                            toShipRecyclerView.setAdapter(toShipAdapter);
                        } else {
                            toShipAdapter.notifyDataSetChanged(); // Notify adapter of data change
                        }
                    });
        } else {
            // User ID is not available, show an error
            Toast.makeText(this, "User ID not found. Please log in.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    private int getImageResource(int imageNum) {
        if (imageNum == 1) {
            return R.drawable.pastil;
        } else if (imageNum == 2) {
            return R.drawable.rice;
        } else if (imageNum == 3) {
            return R.drawable.hotdog;
        } else if (imageNum == 4) {
            return R.drawable.coke;
        } else if (imageNum == 5) {
            return R.drawable.sprite;
        } else if (imageNum == 6) {
            return R.drawable.styro;
        } else if (imageNum == 7) {
            return R.drawable.spoon;
        } else if (imageNum == 8) {
            return R.drawable.fork;
        }
        return R.drawable.ic_launcher_foreground; // Default image
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (firestoreListener != null) {
            firestoreListener.remove(); // Remove the listener when the activity is destroyed
        }
    }
}