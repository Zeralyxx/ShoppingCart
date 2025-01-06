package com.example.shoppingcart;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private TextView validationTextView;
    private TextView createAccountTextView;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        sharedPreferences = getSharedPreferences("login_prefs", Context.MODE_PRIVATE);

        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        validationTextView = findViewById(R.id.validationTextView);
        createAccountTextView = findViewById(R.id.createAccountTextView);

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

        createAccountTextView.setOnClickListener(v -> {
            // Navigate to CreateAccountActivity
            startActivity(new android.content.Intent(this, SignupActivity.class));
        });
    }

    private void authenticateUser(String username, String password) {
        db.collection("users").document(username).get()
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            String storedPassword = document.getString("password");

                            if (password.equals(storedPassword)) {
                                Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
                                // Set the "logged in" state
                                // Inside your login success logic
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putBoolean("isLoggedIn", true);
                                editor.putString("username", username); // Assuming you also save the username
                                editor.apply(); // Use apply() instead of commit()
                                // Navigate to MainActivity
                                startActivity(new android.content.Intent(this, MainActivity.class));
                                finish();
                            } else {
                                Toast.makeText(this, "Incorrect password.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, "User not found.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}