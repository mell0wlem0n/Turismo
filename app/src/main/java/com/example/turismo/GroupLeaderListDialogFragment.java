package com.example.turismo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class GroupLeaderListDialogFragment extends DialogFragment {

    private RecyclerView groupRecyclerView;
    private GroupAdapter groupAdapter;
    private List<QueryDocumentSnapshot> groupList;
    private OnGroupSelectedListener listener;

    public interface OnGroupSelectedListener {
        void onGroupSelected(String groupId, String groupName);
    }

    public void setOnGroupSelectedListener(OnGroupSelectedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_leader_list_dialog, container, false);

        groupRecyclerView = view.findViewById(R.id.groupRecyclerView);
        groupRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        groupList = new ArrayList<>();
        groupAdapter = new GroupAdapter(groupList, (groupId, groupName) -> {
            if (listener != null) {
                listener.onGroupSelected(groupId, groupName);
            }
            dismiss();
        });

        groupRecyclerView.setAdapter(groupAdapter);

        loadLeaderGroups();

        return view;
    }

    private void loadLeaderGroups() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            db.collection("groups")
                    .whereEqualTo("leaderID", currentUser.getUid())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                groupList.add(document);
                            }
                            groupAdapter.notifyDataSetChanged();
                        }
                    });
        }
    }

    private class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder> {

        private final List<QueryDocumentSnapshot> groupList;
        private final OnGroupSelectedListener listener;

        GroupAdapter(List<QueryDocumentSnapshot> groupList, OnGroupSelectedListener listener) {
            this.groupList = groupList;
            this.listener = listener;
        }

        @NonNull
        @Override
        public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
            return new GroupViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
            QueryDocumentSnapshot document = groupList.get(position);
            String groupName = document.getString("groupName");
            holder.groupNameTextView.setText(groupName);
            holder.itemView.setOnClickListener(v -> listener.onGroupSelected(document.getId(), groupName));
        }

        @Override
        public int getItemCount() {
            return groupList.size();
        }

        class GroupViewHolder extends RecyclerView.ViewHolder {
            TextView groupNameTextView;

            GroupViewHolder(View itemView) {
                super(itemView);
                groupNameTextView = itemView.findViewById(android.R.id.text1);
            }
        }
    }
}
