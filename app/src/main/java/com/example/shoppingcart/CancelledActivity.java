package com.example.shoppingcart;

import android.content.Context;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class CancelledActivity extends AppCompatActivity {

    private static final String TAG = "CancelledActivity";
    private ImageButton backButton;
    private RecyclerView cancelledRecyclerView;
    private FirebaseFirestore db;
    private CancelledAdapter cancelledAdapter;
    private List<CancelledProduct> cancelledOrderList;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancelled);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        sharedPreferences = getSharedPreferences("login_prefs", Context.MODE_PRIVATE);

        backButton = findViewById(R.id.backButton);
        cancelledRecyclerView = findViewById(R.id.cancelledRecyclerView);

        // Set up RecyclerView
        cancelledRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Go back to the previous activity
            }
        });

        loadCancelledOrdersFromFirestore();
    }

    private void loadCancelledOrdersFromFirestore() {
        cancelledOrderList = new ArrayList<>();
        String userId = sharedPreferences.getString("userId", "");
        Log.d(TAG, "Retrieved userId from SharedPreferences: " + userId);

        if (!userId.isEmpty()) {
            db.collection("cancelled")
                    .whereEqualTo("username", userId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String productName = document.getString("productName");
                                double price = document.getDouble("price");
                                int quantity = document.getLong("quantity").intValue();
                                int imageNum = document.getLong("imageNum").intValue();
                                String cancellationDate = document.getString("cancellationDate");
                                int imageResourceId = getImageResource(imageNum);

                                Product product = new Product(productName, price, imageResourceId, imageNum, quantity);
                                CancelledProduct cancelledProduct = new CancelledProduct(product, cancellationDate);
                                cancelledOrderList.add(cancelledProduct);
                            }
                            cancelledAdapter = new CancelledAdapter(cancelledOrderList);
                            cancelledRecyclerView.setAdapter(cancelledAdapter);
                        } else {
                            Toast.makeText(this, "Error loading cancelled orders.", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // User ID is not available, show an error
            Toast.makeText(this, "User ID not found. Please log in.", Toast.LENGTH_SHORT).show();
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
            return R.drawable.sprite1;
        } else if (imageNum == 6) {
            return R.drawable.styro;
        } else if (imageNum == 7) {
            return R.drawable.spoon1;
        } else if (imageNum == 8) {
            return R.drawable.fork;
        } else if (imageNum == 9) {
            return R.drawable.chicken;
        } else if (imageNum == 10) {
            return R.drawable.kwek;
        }
        return R.drawable.ic_launcher_foreground; // Default image
    }
}