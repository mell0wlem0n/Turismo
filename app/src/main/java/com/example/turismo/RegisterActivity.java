package com.example.turismo;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
public class RegisterActivity extends AppCompatActivity {

    private EditText usernameEditText, emailEditText, passwordEditText, confirmPasswordEditText;
    private Button registerButton;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Find views by ID
        usernameEditText = findViewById(R.id.registerNameField);
        emailEditText = findViewById(R.id.registerEmailField);
        passwordEditText = findViewById(R.id.registerPasswordField);
        confirmPasswordEditText = findViewById(R.id.registerConfirmPasswordField);
        registerButton = findViewById(R.id.registerButton);

        // Set click listener for the register button
        registerButton.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String username = usernameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        // Validate input fields
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }


        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {

                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        String userId = user.getUid();


                        Map<String, Object> userData = new HashMap<>();
                        userData.put("username", username);
                        userData.put("email", email);

                        firestore.collection("users").document(userId)
                                .set(userData)
                                .addOnSuccessListener(Void -> {
                                    Toast.makeText(RegisterActivity.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {

                                    Toast.makeText(RegisterActivity.this, "Error registering user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                        user.sendEmailVerification()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {

                                            startActivity(new Intent(RegisterActivity.this, AuthentificationMenuActivity.class));
                                            finish();
                                            Toast.makeText(RegisterActivity.this, "Registration successful. Please check your email for verification.", Toast.LENGTH_SHORT).show();
                                        } else {

                                            Toast.makeText(RegisterActivity.this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    } else {

                        Toast.makeText(RegisterActivity.this, "Error registering user: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


}

