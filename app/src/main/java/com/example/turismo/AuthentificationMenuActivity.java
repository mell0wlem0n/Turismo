package com.example.turismo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AuthentificationMenuActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "GoogleSignIn";
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private SharedPreferences sharedPreferences;
    private Button loginButton;
    private Button googleSignInButton;
    private TextView register;
    private TextView forgotPassword;
    private EditText emailOrUsernameField;
    private EditText passwordField;
    private CheckBox rememberMeCheckbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authentification_menu);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);

        emailOrUsernameField = findViewById(R.id.emailOrUsernameField);
        passwordField = findViewById(R.id.passwordField);
        loginButton = findViewById(R.id.loginButton);
        googleSignInButton = findViewById(R.id.googleSignInButton);
        forgotPassword = findViewById(R.id.forgotPassword);
        register = findViewById(R.id.signup);
        rememberMeCheckbox = findViewById(R.id.rememberMe);

        loginButton.setOnClickListener(v -> loginUser(emailOrUsernameField.getText().toString().trim(), passwordField.getText().toString().trim()));

        forgotPassword.setOnClickListener(v -> startActivity(new Intent(AuthentificationMenuActivity.this, SendEmailForgotPassword.class)));

        register.setOnClickListener(v -> startActivity(new Intent(AuthentificationMenuActivity.this, RegisterActivity.class)));

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        googleSignInButton.setOnClickListener(v -> signInWithGoogle());

        // Check if user is already signed in with Google
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            // User is signed in, redirect to MapActivity
            startActivity(new Intent(AuthentificationMenuActivity.this, MapActivity.class));
            finish();
        }
    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Log.w(TAG, "Google sign in failed", e);
                Toast.makeText(this, "Google sign in failed: " + e.getStatusCode(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            if (user != null) {
                                addUserToFirestore(user);
                            }
                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(AuthentificationMenuActivity.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void addUserToFirestore(FirebaseUser user) {
        firestore.collection("users").document(user.getUid()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && !document.exists()) {
                    String username = user.getDisplayName();
                    String email = user.getEmail();
                    Map<String, Object> userData = new HashMap<>();
                    userData.put("username", username);
                    userData.put("email", email);

                    firestore.collection("users").document(user.getUid()).set(userData)
                            .addOnSuccessListener(aVoid -> {
                                Log.d(TAG, "User data saved successfully");
                                startActivity(new Intent(AuthentificationMenuActivity.this, MapActivity.class));
                                finish();
                            })
                            .addOnFailureListener(e -> Log.w(TAG, "Error saving user data", e));
                } else {
                    startActivity(new Intent(AuthentificationMenuActivity.this, MapActivity.class));
                    finish();
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });
    }

    private void loginUser(String emailOrUsername, String password) {
        if (emailOrUsername != null && !emailOrUsername.isEmpty() && password != null && !password.isEmpty()) {
            firebaseAuth.signInWithEmailAndPassword(emailOrUsername, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            handleLoginResult(task);
                            if (rememberMeCheckbox.isChecked()) {
                                getSharedPreferences("loginPrefs", MODE_PRIVATE)
                                        .edit()
                                        .putString("email", emailOrUsername)
                                        .putString("password", password)
                                        .apply();
                            }
                        }
                    });
        } else {
            Toast.makeText(getApplicationContext(), "Please fill both fields", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleLoginResult(Task<AuthResult> task) {
        if (task.isSuccessful()) {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null && user.isEmailVerified()) {
                Toast.makeText(AuthentificationMenuActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(AuthentificationMenuActivity.this, MapActivity.class));
                finish();
            } else {
                Toast.makeText(AuthentificationMenuActivity.this, "Please verify your email first.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(AuthentificationMenuActivity.this, "Login failed. Please check your credentials.", Toast.LENGTH_SHORT).show();
        }
    }
}
