package com.example.shoppingcart;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class CreateAccountFragment extends Fragment {

    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_account, container, false);

        db = FirebaseFirestore.getInstance();

        EditText usernameEditText = view.findViewById(R.id.createUsernameEditText);
        EditText passwordEditText = view.findViewById(R.id.createPasswordEditText);
        EditText confirmPasswordEditText = view.findViewById(R.id.confirmPasswordEditText);

        Button createAccountButton = view.findViewById(R.id.createAccountButton);
        TextView validationTextView = view.findViewById(R.id.validationTextView);

        createAccountButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String confirmPassword = confirmPasswordEditText.getText().toString().trim();

            if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
                validationTextView.setVisibility(View.VISIBLE);
                validationTextView.setText("Please fill out all fields.");
            } else if (!password.equals(confirmPassword)) {
                validationTextView.setVisibility(View.VISIBLE);
                validationTextView.setText("Passwords do not match.");
            } else {
                validationTextView.setVisibility(View.GONE);
                createUser(username, password);
            }
        });

        return view;
    }

    private void createUser(String username, String password) {
        db.collection("users").document(username).get()
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().exists()) {
                            Toast.makeText(getActivity(), "Username already exists.", Toast.LENGTH_SHORT).show();
                        } else {
                            List<String> shoppingCart = new ArrayList<>();
                            User user = new User(username, password, shoppingCart);
                            db.collection("users").document(username).set(user)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(getActivity(), "Account created successfully.", Toast.LENGTH_SHORT).show();
                                        clearFields();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(getActivity(), "Error creating account.", Toast.LENGTH_SHORT).show();
                                    });
                        }
                    } else {
                        Toast.makeText(getActivity(), "Error checking username.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void clearFields() {
        EditText usernameEditText = requireView().findViewById(R.id.createUsernameEditText);
        EditText passwordEditText = requireView().findViewById(R.id.createPasswordEditText);
        EditText confirmPasswordEditText = requireView().findViewById(R.id.confirmPasswordEditText);

        usernameEditText.setText("");
        passwordEditText.setText("");
        confirmPasswordEditText.setText("");
    }

    public static class User {
        public String username;
        public String password;
        public List<String> shoppingCart;

        public User() {
        }

        public User(String username, String password, List<String> shoppingCart) {
            this.username = username;
            this.password = password;
            this.shoppingCart = shoppingCart;
        }
    }
}
