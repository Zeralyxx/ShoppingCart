package com.example.shoppingcart;

import android.os.Bundle;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

public class AboutUsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_us);

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish()); // Close the activity when back button is clicked
    }
}