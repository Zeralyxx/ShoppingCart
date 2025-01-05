package com.example.shoppingcart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

public class AccountFragment extends Fragment {

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        EditText usernameEditText = view.findViewById(R.id.usernameEditText);
        EditText passwordEditText = view.findViewById(R.id.passwordEditText);
        Button loginButton = view.findViewById(R.id.loginButton);
        TextView validationTextView = view.findViewById(R.id.validationTextView);

        loginButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                validationTextView.setVisibility(View.VISIBLE);
            } else {
                validationTextView.setVisibility(View.GONE);
                authenticateUser(username, password);
            }
        });

        TextView createAccountTextView = view.findViewById(R.id.createAccountTextView);
        createAccountTextView.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, new CreateAccountFragment())
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    private void authenticateUser(String username, String password) {
        db.collection("users").document(username).get()
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            String storedPassword = document.getString("password");

                            if (password.equals(storedPassword)) {
                                Toast.makeText(getActivity(), "Login successful!", Toast.LENGTH_SHORT).show();
                                // Navigate to another fragment or perform other actions
                            } else {
                                Toast.makeText(getActivity(), "Incorrect password.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getActivity(), "User not found.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
