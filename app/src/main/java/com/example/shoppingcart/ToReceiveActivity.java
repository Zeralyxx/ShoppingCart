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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.auth.FirebaseAuth;
import java.util.ArrayList;
import java.util.List;

public class ToReceiveActivity extends AppCompatActivity {

    private static final String TAG = "ToReceiveActivity";
    private ImageButton backButton;
    private RecyclerView toReceiveRecyclerView;
    private ToReceiveAdapter toReceiveAdapter;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private ListenerRegistration firestoreListener;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_receive);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        sharedPreferences = getSharedPreferences("login_prefs", Context.MODE_PRIVATE);

        backButton = findViewById(R.id.backButton);
        toReceiveRecyclerView = findViewById(R.id.toReceiveRecyclerView);

        // Set up RecyclerView
        toReceiveRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the adapter with an empty list
        toReceiveAdapter = new ToReceiveAdapter(this, new ArrayList<>());
        toReceiveRecyclerView.setAdapter(toReceiveAdapter);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ToReceiveActivity.this, MainActivity.class);
                intent.putExtra("fragment_to_load", "account");
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadOrderListFromFirestore();
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (firestoreListener != null) {
            firestoreListener.remove();
            firestoreListener = null;
        }
    }

    private void loadOrderListFromFirestore() {
        String userId = sharedPreferences.getString("userId", "");
        Log.d(TAG, "Retrieved userId from SharedPreferences: " + userId);

        if (userId != null && !userId.isEmpty()) {
            if (firestoreListener != null) {
                firestoreListener.remove();
            }

            firestoreListener = db.collection("toReceive")
                    .whereEqualTo("username", userId)
                    .addSnapshotListener((querySnapshot, e) -> {
                        if (e != null) {
                            Log.e(TAG, "Listen failed.", e);
                            Toast.makeText(this, "Error loading orders from Firestore.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (querySnapshot != null) {
                            List<DocumentSnapshot> documentSnapshots = querySnapshot.getDocuments();
                            toReceiveAdapter = new ToReceiveAdapter(this, documentSnapshots);
                            toReceiveRecyclerView.setAdapter(toReceiveAdapter);
                            toReceiveAdapter.notifyDataSetChanged();
                            Log.d(TAG, "ToReceive data updated (real-time): " + documentSnapshots.size() + " items");
                        }
                    });
        } else {
            Toast.makeText(this, "User ID not found. Please log in.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }
}