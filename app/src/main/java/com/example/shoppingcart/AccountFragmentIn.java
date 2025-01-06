package com.example.shoppingcart;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class AccountFragmentIn extends Fragment {

    private static final String TAG = "AccountFragmentIn";
    private SharedPreferences sharedPreferences;
    private TextView usernameTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_account_in, container, false);
        sharedPreferences = requireContext().getSharedPreferences("login_prefs", Context.MODE_PRIVATE);
        usernameTextView = view.findViewById(R.id.usernameTextView);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated");
        updateUI();
        view.findViewById(R.id.logoutButton).setOnClickListener(v -> showLogoutConfirmationDialog());
    }

    private void updateUI() {
        String username = sharedPreferences.getString("username", "");
        Log.d(TAG, "Username: " + username);
        usernameTextView.setText(username);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView");
    }
}