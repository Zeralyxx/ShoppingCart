package com.example.shoppingcart;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

public class AccountFragmentIn extends Fragment {

    private static final String TAG = "AccountFragmentIn";
    private SharedPreferences sharedPreferences;
    private TextView usernameTextView;
    private ImageButton editProfileButton;
    private String userId;
    private ActivityResultLauncher<Intent> editProfileLauncher;
    private FirebaseFirestore db;
    private ListenerRegistration userListenerRegistration;
    private MaterialButton btnToReceive;
    private MaterialButton btnHistory;
    private MaterialButton btnCancelled;
    private MaterialButton btnToShip;
    private Button helpCenterButton;
    private Button aboutUsButton; // Add this

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_account_in, container, false);
        sharedPreferences = requireContext().getSharedPreferences("login_prefs", Context.MODE_PRIVATE);
        usernameTextView = view.findViewById(R.id.usernameTextView);
        editProfileButton = view.findViewById(R.id.editProfileButton);
        db = FirebaseFirestore.getInstance();

        // Initialize My Orders buttons
        btnToReceive = view.findViewById(R.id.btnToReceive);
        btnHistory = view.findViewById(R.id.btnHistory);
        btnCancelled = view.findViewById(R.id.btnCancelled);
        btnToShip = view.findViewById(R.id.btnToShip);
        helpCenterButton = view.findViewById(R.id.helpCenterButton);
        aboutUsButton = view.findViewById(R.id.aboutUsButton); // Initialize the aboutUsButton

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated");
        view.findViewById(R.id.logoutButton).setOnClickListener(v -> showLogoutConfirmationDialog());
        editProfileButton.setOnClickListener(v -> navigateToEditProfile());

        // Set click listeners for My Orders buttons
        btnToReceive.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ToReceiveActivity.class);
            startActivity(intent);
        });
        btnToShip.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ToShipActivity.class);
            startActivity(intent);
        });
        btnHistory.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), HistoryActivity.class);
            startActivity(intent);
        });
        btnCancelled.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CancelledActivity.class);
            startActivity(intent);
        });

        // Set click listener for Help Center button
        helpCenterButton.setOnClickListener(v -> openHelpCenterLink());

        // Set click listener for About Us button
        aboutUsButton.setOnClickListener(v -> navigateToAboutUs());

        // Initialize ActivityResultLauncher for EditProfileActivity
        editProfileLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // Refresh UI if profile was updated
                            loadUsername();
                        }
                    }
                }
        );
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        loadUsername(); // Load username on resume
        startUserListener(); // Start listening for changes
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        stopUserListener(); // Stop listening for changes
    }

    private void loadUsername() {
        userId = sharedPreferences.getString("userId", "");
        if (userId != null && !userId.isEmpty()) {
            db.collection("users").document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String username = documentSnapshot.getString("username");
                            Log.d(TAG, "Username from Firestore: " + username);
                            usernameTextView.setText(username);
                        } else {
                            Log.e(TAG, "User document not found in Firestore");
                            Toast.makeText(requireContext(), "User data not found.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error loading username from Firestore", e);
                        Toast.makeText(requireContext(), "Failed to load user data.", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Log.e(TAG, "User ID is null or empty.");
            Toast.makeText(requireContext(), "User ID not found.", Toast.LENGTH_SHORT).show();
        }
    }

    private void startUserListener() {
        userId = sharedPreferences.getString("userId", "");
        if (userId != null && !userId.isEmpty()) {
            userListenerRegistration = db.collection("users").document(userId)
                    .addSnapshotListener((documentSnapshot, e) -> {
                        if (e != null) {
                            Log.e(TAG, "Listen failed.", e);
                            return;
                        }
                        if (documentSnapshot != null && documentSnapshot.exists()) {
                            String username = documentSnapshot.getString("username");
                            Log.d(TAG, "Username changed (real-time): " + username);
                            usernameTextView.setText(username);
                        }
                    });
        }
    }

    private void stopUserListener() {
        if (userListenerRegistration != null) {
            userListenerRegistration.remove();
            userListenerRegistration = null;
        }
    }

    private void showLogoutConfirmationDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> logoutUser())
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void logoutUser() {
        Log.d(TAG, "logoutUser: shared preferences cleared");
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        replaceFragment(new AccountFragmentOut());
    }

    private void replaceFragment(Fragment fragment) {
        Log.d(TAG, "replaceFragment");
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.account_fragment_container, fragment);
        fragmentTransaction.commit();
    }

    private void navigateToEditProfile() {
        Intent intent = new Intent(requireContext(), EditProfileActivity.class);
        intent.putExtra("userId", userId);
        editProfileLauncher.launch(intent);
    }

    private void openHelpCenterLink() {
        String url = "https://www.facebook.com/messages/e2ee/t/7608263649229986";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        try {
            startActivity(intent);
        } catch (android.content.ActivityNotFoundException e) {
            Toast.makeText(requireContext(), "No app found to open the link.", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "No app found to open the link", e);
        }
    }

    private void navigateToAboutUs() {
        Intent intent = new Intent(requireContext(), AboutUsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView");
        stopUserListener();
    }
}