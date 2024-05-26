package com.example.turismo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.List;

public class InvitationAdapter extends RecyclerView.Adapter<InvitationAdapter.InvitationViewHolder> {

    private List<Invitation> invitationList;

    public InvitationAdapter(List<Invitation> invitationList) {
        this.invitationList = invitationList;
    }

    @NonNull
    @Override
    public InvitationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_invitation, parent, false);
        return new InvitationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InvitationViewHolder holder, int position) {
        Invitation invitation = invitationList.get(position);
        holder.groupNameTextView.setText(invitation.getGroupName());

        holder.acceptButton.setOnClickListener(v -> {
            acceptInvitation(invitation);
            removeInvitation(position);
        });

        holder.declineButton.setOnClickListener(v -> {
            declineInvitation(invitation);
            removeInvitation(position);
        });
    }

    @Override
    public int getItemCount() {
        return invitationList.size();
    }

    private void removeInvitation(int position) {
        invitationList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, invitationList.size());
    }

    private void acceptInvitation(Invitation invitation) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("groups").document(invitation.getGroupId())
                .update("members", FieldValue.arrayUnion(invitation.getUserId()))
                .addOnSuccessListener(aVoid -> {
                    db.collection("invitations")
                            .whereEqualTo("userId", invitation.getUserId())
                            .whereEqualTo("groupId", invitation.getGroupId())
                            .get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                    document.getReference().delete();
                                }
                            });
                });
    }

    private void declineInvitation(Invitation invitation) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("invitations")
                .whereEqualTo("userId", invitation.getUserId())
                .whereEqualTo("groupId", invitation.getGroupId())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        document.getReference().delete();
                    }
                });
    }

    public static class InvitationViewHolder extends RecyclerView.ViewHolder {
        TextView groupNameTextView;
        Button acceptButton;
        Button declineButton;

        public InvitationViewHolder(@NonNull View itemView) {
            super(itemView);
            groupNameTextView = itemView.findViewById(R.id.groupNameTextView);
            acceptButton = itemView.findViewById(R.id.acceptButton);
            declineButton = itemView.findViewById(R.id.declineButton);
        }
    }
}
