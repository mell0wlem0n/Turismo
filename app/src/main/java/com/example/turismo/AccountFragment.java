package com.example.turismo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class AccountFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;


    private EditText usernameEditText, emailEditText, passwordEditText, confirmPassword;
    private Button saveChangesButton, disconnectButton, deleteAccountButton;
    private ImageView profilePhotoImageView;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private FirebaseUser currentUser;
    private SharedPreferences sharedPreferences;
    private GoogleSignInClient mGoogleSignInClient;
    private Uri imageUri;
    public static String image_url;
    private StorageReference storageReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        // Initialize Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference("profile_images");

        // Initialize Google SignIn Client
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getContext(), gso);

        // Find views by ID
        usernameEditText = view.findViewById(R.id.nameField);
        emailEditText = view.findViewById(R.id.emailField);
        passwordEditText = view.findViewById(R.id.passwordField);
        saveChangesButton = view.findViewById(R.id.saveButton);
        disconnectButton = view.findViewById(R.id.disconnectButton);
        deleteAccountButton = view.findViewById(R.id.deleteButton);
        confirmPassword = view.findViewById(R.id.confirmPasswordField);
        profilePhotoImageView = view.findViewById(R.id.profilePhotoImageView);

        if (currentUser != null) {
            firestore.collection("users").document(currentUser.getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String username = documentSnapshot.getString("username");
                            String email = documentSnapshot.getString("email");
                            String profileImageUrl = documentSnapshot.getString("profileImageUrl");
                            image_url = profileImageUrl;
                            usernameEditText.setText(username);
                            emailEditText.setText(email);

                            if (profileImageUrl != null) {
                                Glide.with(this).load(profileImageUrl).into(profilePhotoImageView);

                            }
                        }
                    });
        }

        // Set click listeners for buttons
        saveChangesButton.setOnClickListener(v -> saveChanges());
        disconnectButton.setOnClickListener(v -> disconnect());
        deleteAccountButton.setOnClickListener(v -> deleteAccount());
        profilePhotoImageView.setOnClickListener(v -> openFileChooser());

        return view;
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                profilePhotoImageView.setImageBitmap(bitmap);
                uploadImageToFirebase();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImageToFirebase() {
        if (imageUri != null) {
            StorageReference fileReference = storageReference.child(currentUser.getUid() + ".jpg");

            fileReference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String downloadUrl = uri.toString();
                                    saveProfileImageUrl(downloadUrl);
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), "Failed to upload image", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void saveProfileImageUrl(String downloadUrl) {
        firestore.collection("users").document(currentUser.getUid())
                .update("profileImageUrl", downloadUrl)
                .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Profile image updated", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to update profile image", Toast.LENGTH_SHORT).show());
    }

    private void saveChanges() {
        String newUsername = usernameEditText.getText().toString().trim();
        String newEmail = emailEditText.getText().toString().trim();
        String newPassword = passwordEditText.getText().toString().trim();
        firestore.collection("users").document(currentUser.getUid()).get().addOnSuccessListener(documentSnapshot -> {
            String username = documentSnapshot.getString("username");
            String email = documentSnapshot.getString("email");
            if (!newPassword.isEmpty() && newPassword.equals(confirmPassword.getText().toString())) {
                currentUser.updatePassword(newPassword)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                passwordEditText.setText("");
                                confirmPassword.setText("");
                                Toast.makeText(getContext(), "Passwords changed successfully: ", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "Password change failed.", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else if (!newPassword.isEmpty()) {
                Toast.makeText(getContext(), "Passwords don't match", Toast.LENGTH_SHORT).show();
            }
            if (!username.equals(newUsername))
                firestore.collection("users").document(currentUser.getUid())
                        .update("username", newUsername)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(getContext(), "Username changed successfully", Toast.LENGTH_SHORT).show();

                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Error changing username: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            if (!email.equals(newEmail)) {
                if (Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
                    firestore.collection("users").document(currentUser.getUid())
                            .update("email", newEmail)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(getContext(), "Email changed successfully", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getContext(), "Error changing email: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                }
            }
        });
    }

    private void disconnect() {
        if (currentUser != null) {
            firebaseAuth.signOut();
            mGoogleSignInClient.signOut().addOnCompleteListener(getActivity(), task -> {
                mGoogleSignInClient.revokeAccess().addOnCompleteListener(getActivity(), revokeTask -> {
                    Intent intent = new Intent(getContext(), AuthentificationMenuActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    getContext().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
                            .edit()
                            .putString("email", "1")
                            .putString("password", "0")
                            .apply();
                    startActivity(intent);
                });
            });
        }
    }

    private void deleteAccount() {
        if (currentUser != null) {
            firestore.collection("users").document(currentUser.getUid()).delete();
            currentUser.delete()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Account deleted successfully
                            // Redirect to login screen
                            Intent intent = new Intent(getContext(), AuthentificationMenuActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            Toast.makeText(getContext(), "Account deleted successfully", Toast.LENGTH_SHORT).show();
                            startActivity(intent);
                        } else {
                            Toast.makeText(getContext(), "Error deleting account: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
