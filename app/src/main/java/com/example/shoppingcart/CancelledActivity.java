package com.example.shoppingcart;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CancelledActivity extends AppCompatActivity {

    private ImageButton backButton;
    private RecyclerView cancelledRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancelled);

        backButton = findViewById(R.id.backButton);
        cancelledRecyclerView = findViewById(R.id.cancelledRecyclerView);

        // Set up RecyclerView
        cancelledRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // TODO: Set up adapter with order data
        // cancelledRecyclerView.setAdapter(new OrderAdapter(orderList));

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Go back to the previous activity
            }
        });
    }
}