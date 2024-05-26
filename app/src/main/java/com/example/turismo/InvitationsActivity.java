package com.example.turismo;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class InvitationsActivity extends AppCompatActivity {

    private RecyclerView invitationsRecyclerView;
    private InvitationAdapter adapter;
    private List<Invitation> invitationList;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitations); // Ensure this layout file exists and is correct

        invitationsRecyclerView = findViewById(R.id.invitationsRecyclerView); // Ensure this ID matches the RecyclerView ID in activity_invitations.xml
        invitationsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        invitationList = new ArrayList<>();
        adapter = new InvitationAdapter(invitationList);
        invitationsRecyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        loadInvitations();
    }

    private void loadInvitations() {
        db.collection("invitations")
                .whereEqualTo("userId", currentUser.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        invitationList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Invitation invitation = document.toObject(Invitation.class);
                            invitationList.add(invitation);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }
}
