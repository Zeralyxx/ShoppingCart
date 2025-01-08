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
import java.util.ArrayList;
import java.util.List;
import com.google.firebase.auth.FirebaseAuth;

public class ToShipActivity extends AppCompatActivity {

    private static final String TAG = "ToShipActivity";
    private ImageButton backButton;
    private RecyclerView toShipRecyclerView;
    private ToShipAdapter toShipAdapter;
    private List<Product> orderList;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private ListenerRegistration firestoreListener;
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
                intent.putExtra("fragment_to_load", "account");
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

    @Override
    protected void onStop() {
        super.onStop();
        if (firestoreListener != null) {
            firestoreListener.remove();
        }
    }

    private void loadOrderListFromFirestore() {
        orderList = new ArrayList<>();
        String userId = sharedPreferences.getString("userId", "");
        Log.d(TAG, "Retrieved userId from SharedPreferences: " + userId);

        if (firestoreListener != null) {
            firestoreListener.remove();
        }
        orderList.clear();

        if (!userId.isEmpty()) {
            firestoreListener = db.collection("toShip")
                    .whereEqualTo("username", userId)
                    .addSnapshotListener((querySnapshot, e) -> {
                        if (e != null) {
                            Toast.makeText(this, "Error loading orders from Firestore.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        orderList.clear();
                        if (querySnapshot != null) {
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                String productName = document.getString("productName");
                                double price = document.getDouble("price");
                                int quantity = document.getLong("quantity").intValue();
                                int imageNum = document.getLong("imageNum").intValue();
                                String documentId = document.getId(); // Get the document ID
                                int imageResourceId = getImageResource(imageNum);

                                Product product = new Product(productName, price, imageResourceId, imageNum, quantity);
                                product.setQuantity(quantity);
                                product.setDocumentId(documentId); // Store the document ID
                                orderList.add(product);
                            }
                        }
                        if (toShipAdapter == null) {
                            toShipAdapter = new ToShipAdapter(orderList, this); // Pass 'this' as the context
                            toShipRecyclerView.setAdapter(toShipAdapter);
                        } else {
                            toShipAdapter.notifyDataSetChanged();
                        }
                    });
        } else {
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
        return R.drawable.ic_launcher_foreground;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (firestoreListener != null) {
            firestoreListener.remove();
        }
    }
}