package com.example.turismo;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;

public class ConfirmMembershipActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_membership);

        Uri data = getIntent().getData();
        if (data != null) {
            String groupId = data.getQueryParameter("groupId");
            String userId = data.getQueryParameter("userId");

            FirebaseFirestore.getInstance().collection("groups").document(groupId)
                    .update("members", FieldValue.arrayUnion(userId))
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Membership confirmed", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error confirming membership", Toast.LENGTH_SHORT).show();
                    });
        }
    }
}
