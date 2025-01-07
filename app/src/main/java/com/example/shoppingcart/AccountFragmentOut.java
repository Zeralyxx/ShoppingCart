package com.example.shoppingcart;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.google.android.material.button.MaterialButton;
import androidx.core.content.ContextCompat;

public class AccountFragmentOut extends Fragment {

    private MaterialButton btnToReceive;
    private MaterialButton btnToShip;
    private MaterialButton btnHistory;
    private MaterialButton btnCancelled;
    private Button aboutUsButton;
    private Button helpCenterButton;
    private Button settingsButton;
    private MaterialButton btnLogin;
    private MaterialButton btnSignup;
    private View headerLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account_out, container, false);
        headerLayout = view.findViewById(R.id.headerLayout);
        headerLayout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.green));

        // Initialize views
        btnToReceive = view.findViewById(R.id.btnToReceive);
        btnToShip = view.findViewById(R.id.btnToShip);
        btnHistory = view.findViewById(R.id.btnHistory);
        btnCancelled = view.findViewById(R.id.btnCancelled);
        aboutUsButton = view.findViewById(R.id.aboutUsButton);
        helpCenterButton = view.findViewById(R.id.helpCenterButton);
        settingsButton = view.findViewById(R.id.settingsButton);
        btnLogin = view.findViewById(R.id.btnLogin);
        btnSignup = view.findViewById(R.id.btnSignup);

        // Set click listeners
        btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        });
        btnSignup.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SignupActivity.class);
            startActivity(intent);
        });

        btnToReceive.setOnClickListener(v -> showToast("To Receive Button Clicked"));
        btnToShip.setOnClickListener(v -> showToast("To Ship Button Clicked"));
        btnHistory.setOnClickListener(v -> showToast("History Button Clicked"));
        btnCancelled.setOnClickListener(v -> showToast("Cancelled Button Clicked"));
        aboutUsButton.setOnClickListener(v -> showToast("About Us Button Clicked"));
        helpCenterButton.setOnClickListener(v -> showToast("Help Center Button Clicked"));
        settingsButton.setOnClickListener(v -> showToast("Settings Button Clicked"));

        return view;
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}