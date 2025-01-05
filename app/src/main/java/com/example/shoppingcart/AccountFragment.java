package com.example.shoppingcart;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AccountFragment extends Fragment {

    private MaterialButton btnLogin;
    private MaterialButton btnSignup;
    private ConstraintLayout profileLayout;
    private ImageView profileImageView;
    private TextView usernameTextView;
    private Button editProfileButton;
    private MaterialButton btnToReceive;
    private MaterialButton btnHistory;
    private MaterialButton btnCancelled;
    private MaterialButton btnReturns;
    private Button aboutUsButton;
    private Button helpCenterButton;
    private Button settingsButton;
    private Button logoutButton;
    private FirebaseAuth mAuth;
    private ConstraintLayout headerLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize views
        headerLayout = view.findViewById(R.id.headerLayout);
        btnLogin = view.findViewById(R.id.btnLogin);
        btnSignup = view.findViewById(R.id.btnSignup);
        profileLayout = view.findViewById(R.id.profileLayout);
        profileImageView = view.findViewById(R.id.profileImageView);
        usernameTextView = view.findViewById(R.id.usernameTextView);
        editProfileButton = view.findViewById(R.id.editProfileButton);
        btnToReceive = view.findViewById(R.id.btnToReceive);
        btnHistory = view.findViewById(R.id.btnHistory);
        btnCancelled = view.findViewById(R.id.btnCancelled);
        btnReturns = view.findViewById(R.id.btnReturns);
        aboutUsButton = view.findViewById(R.id.aboutUsButton);
        helpCenterButton = view.findViewById(R.id.helpCenterButton);
        settingsButton = view.findViewById(R.id.settingsButton);
        logoutButton = view.findViewById(R.id.logoutButton);

        // Set click listeners
        btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        });
        btnSignup.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SignupActivity.class);
            startActivity(intent);
        });
        editProfileButton.setOnClickListener(v -> showToast("Edit Profile Button Clicked"));
        btnToReceive.setOnClickListener(v -> showToast("To Receive Button Clicked"));
        btnHistory.setOnClickListener(v -> showToast("History Button Clicked"));
        btnCancelled.setOnClickListener(v -> showToast("Cancelled Button Clicked"));
        btnReturns.setOnClickListener(v -> showToast("Returns Button Clicked"));
        aboutUsButton.setOnClickListener(v -> showToast("About Us Button Clicked"));
        helpCenterButton.setOnClickListener(v -> showToast("Help Center Button Clicked"));
        settingsButton.setOnClickListener(v -> showToast("Settings Button Clicked"));
        logoutButton.setOnClickListener(v -> logoutUser());

        // Update UI based on login status
        updateUI();

        return view;
    }

    private void updateUI() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // User is logged in
            btnLogin.setVisibility(View.GONE);
            btnSignup.setVisibility(View.GONE);
            profileLayout.setVisibility(View.VISIBLE);
            usernameTextView.setText(currentUser.getDisplayName() != null ? currentUser.getDisplayName() : "User");
            // Load profile image using Glide
            if (currentUser.getPhotoUrl() != null) {
                Glide.with(this)
                        .load(currentUser.getPhotoUrl())
                        .placeholder(R.drawable.ic_launcher_background)
                        .error(R.drawable.ic_launcher_background)
                        .into(profileImageView);
            } else {
                profileImageView.setImageResource(R.drawable.ic_launcher_background);
            }
        } else {
            // User is not logged in
            btnLogin.setVisibility(View.VISIBLE);
            btnSignup.setVisibility(View.VISIBLE);
            profileLayout.setVisibility(View.GONE);
        }
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void logoutUser() {
        mAuth.signOut();
        updateUI();
        showToast("Logged out successfully");
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }
}