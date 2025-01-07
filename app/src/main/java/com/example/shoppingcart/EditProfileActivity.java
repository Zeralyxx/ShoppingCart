package com.example.shoppingcart;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.util.UUID;

public class EditProfileActivity extends AppCompatActivity {

    private static final String TAG = "EditProfileActivity";
    private EditText usernameEditText, birthdayEditText, phoneEditText, emailEditText;
    private Spinner genderSpinner;
    private ImageView profileImageView;
    private ImageButton saveButton, backButton;
    private TextView editProfileTextView, changeProfilePictureTextView;
    private Uri imageUri;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private FirebaseAuth auth;
    private String userId;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        auth = FirebaseAuth.getInstance();
        sharedPreferences = getSharedPreferences("login_prefs", MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", null);

        usernameEditText = findViewById(R.id.usernameEditText);
        birthdayEditText = findViewById(R.id.birthdayEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        emailEditText = findViewById(R.id.emailEditText);
        genderSpinner = findViewById(R.id.genderSpinner);
        profileImageView = findViewById(R.id.editProfileImageView);
        saveButton = findViewById(R.id.saveButton);
        backButton = findViewById(R.id.backButton);
        editProfileTextView = findViewById(R.id.editProfileTextView);
        changeProfilePictureTextView = findViewById(R.id.changeProfilePictureTextView);

        // Initialize ActivityResultLauncher for image picking
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        imageUri = data.getData();
                        Glide.with(this).load(imageUri).into(profileImageView);
                    }
                }
        );

        // Set up gender spinner
        setupGenderSpinner();

        // Load existing profile data
        loadProfileData();

        changeProfilePictureTextView.setOnClickListener(v -> pickImage());

        saveButton.setOnClickListener(v -> saveProfile());

        backButton.setOnClickListener(v -> finish());
    }

    private void setupGenderSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.gender_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(adapter);
    }

    private void loadProfileData() {
        if (userId != null) {
            db.collection("users").document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String username = documentSnapshot.getString("username");
                            String imageUrl = documentSnapshot.getString("profileImageUrl");
                            String gender = documentSnapshot.getString("gender");
                            String birthday = documentSnapshot.getString("birthday");
                            String phone = documentSnapshot.getString("phone");
                            String email = documentSnapshot.getString("email");

                            usernameEditText.setText(username);
                            birthdayEditText.setText(birthday);
                            phoneEditText.setText(phone);
                            emailEditText.setText(email);

                            if (gender != null) {
                                ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) genderSpinner.getAdapter();
                                int position = adapter.getPosition(gender);
                                genderSpinner.setSelection(position);
                            }

                            if (imageUrl != null && !imageUrl.isEmpty()) {
                                Glide.with(this).load(imageUrl).into(profileImageView);
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error loading profile data", e);
                        Toast.makeText(this, "Failed to load profile data.", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Log.e(TAG, "User ID is null.");
            Toast.makeText(this, "User ID not found.", Toast.LENGTH_SHORT).show();
        }
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private void saveProfile() {
        String username = usernameEditText.getText().toString().trim();
        String gender = genderSpinner.getSelectedItem().toString();
        String birthday = birthdayEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();

        if (userId == null) {
            Toast.makeText(this, "User ID not found.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imageUri != null) {
            uploadImage(username, gender, birthday, phone, email);
        } else {
            updateProfile(username, gender, birthday, phone, email, null);
        }
    }

    private void uploadImage(String username, String gender, String birthday, String phone, String email) {
        if (userId == null) {
            Toast.makeText(this, "User ID not found.", Toast.LENGTH_SHORT).show();
            return;
        }

        String imageName = UUID.randomUUID().toString();
        StorageReference imageRef = storageRef.child("profile_images/" + userId + "/" + imageName);

        UploadTask uploadTask = imageRef.putFile(imageUri);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String imageUrl = uri.toString();
                updateProfile(username, gender, birthday, phone, email, imageUrl);
            }).addOnFailureListener(e -> {
                Log.e(TAG, "Failed to get download URL", e);
                Toast.makeText(this, "Failed to get image URL.", Toast.LENGTH_SHORT).show();
            });
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Failed to upload image", e);
            Toast.makeText(this, "Failed to upload image.", Toast.LENGTH_SHORT).show();
        });
    }

    private void updateProfile(String username, String gender, String birthday, String phone, String email, String imageUrl) {
        if (userId == null) {
            Toast.makeText(this, "User ID not found.", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("users").document(userId)
                .update(
                        "username", username,
                        "gender", gender,
                        "birthday", birthday,
                        "phone", phone,
                        "email", email,
                        "profileImageUrl", imageUrl
                )
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                    // Set result code and finish
                    Intent resultIntent = new Intent();
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to update profile", e);
                    Toast.makeText(this, "Failed to update profile.", Toast.LENGTH_SHORT).show();
                });
    }
}