package com.example.shoppingcart;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private Button createAccountButton;
    private TextView validationTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        db = FirebaseFirestore.getInstance();

        usernameEditText = findViewById(R.id.createUsernameEditText);
        passwordEditText = findViewById(R.id.createPasswordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        createAccountButton = findViewById(R.id.createAccountButton);
        validationTextView = findViewById(R.id.validationTextView);

        createAccountButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String confirmPassword = confirmPasswordEditText.getText().toString().trim();
            //String address = addressEditText.getText().toString().trim(); // Get address

            if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
                validationTextView.setVisibility(View.VISIBLE);
                validationTextView.setText("Please fill out all fields.");
            } else if (!password.equals(confirmPassword)) {
                validationTextView.setVisibility(View.VISIBLE);
                validationTextView.setText("Passwords do not match.");
            } else {
                validationTextView.setVisibility(View.GONE);
                createUser(username, password); // Pass address to createUser
            }
        });
    }

    private void createUser(String username, String password) {
        db.collection("users")
                .whereEqualTo("username", username)
                .get()
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            Toast.makeText(this, "Username already exists.", Toast.LENGTH_SHORT).show();
                        } else {
                            List<String> shoppingCart = new ArrayList<>();
                            Map<String, Object> user = new HashMap<>();
                            user.put("username", username);
                            user.put("password", password);
                            user.put("name", "");
                            user.put("gender", "");
                            user.put("birthday", "");
                            user.put("phone", "");
                            user.put("email", "");
                            user.put("profileImageUri", "");
                            user.put("shoppingCart", shoppingCart);
                            user.put("address", ""); // Add blank address to user data

                            db.collection("users")
                                    .add(user)
                                    .addOnSuccessListener(documentReference -> {
                                        Toast.makeText(this, "Account created successfully.", Toast.LENGTH_SHORT).show();
                                        clearFields();
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(this, "Error creating account.", Toast.LENGTH_SHORT).show();
                                    });
                        }
                    } else {
                        Toast.makeText(this, "Error checking username.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void clearFields() {
        usernameEditText.setText("");
        passwordEditText.setText("");
        confirmPasswordEditText.setText("");
    }
}