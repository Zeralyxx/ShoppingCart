package com.example.shoppingcart;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.shoppingcart.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements FragmentNavigation {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set HomeFragment as the default
        replaceFragment(new HomeFragment(), R.id.frame_layout);
        binding.bottomNavigationView.setBackground(null);

        // Handle bottom navigation item selection
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            int itemId = item.getItemId();
            if (itemId == R.id.home) {
                replaceFragment(new HomeFragment(), R.id.frame_layout);
            } else if (itemId == R.id.cart) {
                replaceFragment(new CartFragment(), R.id.frame_layout);
            } else if (itemId == R.id.account) {
                replaceFragment(new AccountFragment(), R.id.frame_layout);
            }

            return true;

        });

        // Handle system bars insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    }

    // Function to replace the currently displayed fragment
    @Override
    public void replaceFragment(Fragment fragment, int containerId) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(containerId, fragment);
        fragmentTransaction.commit();
    }

    // Product class (as is, unchanged)
    public static class Product {
        private String name;
        private int imageResourceId;
        private double price;
        // Getters and Setters
    }
}