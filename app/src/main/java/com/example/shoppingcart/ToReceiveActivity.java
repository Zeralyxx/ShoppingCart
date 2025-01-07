package com.example.shoppingcart;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ToReceiveActivity extends AppCompatActivity {

    private ImageButton backButton;
    private RecyclerView toReceiveRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_receive);

        backButton = findViewById(R.id.backButton);
        toReceiveRecyclerView = findViewById(R.id.toReceiveRecyclerView);

        // Set up RecyclerView
        toReceiveRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // TODO: Set up adapter with order data
        // toReceiveRecyclerView.setAdapter(new OrderAdapter(orderList));

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Go back to the previous activity
            }
        });
    }
}