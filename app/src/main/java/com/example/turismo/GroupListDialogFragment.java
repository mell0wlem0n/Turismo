package com.example.turismo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class GroupListDialogFragment extends DialogFragment {

    private RecyclerView groupsRecyclerView;
    private GroupAdapter adapter;
    private List<String> groupList;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private OnGroupSelectedListener listener;

    public interface OnGroupSelectedListener {
        void onGroupSelected(String groupName);
    }

    public void setOnGroupSelectedListener(OnGroupSelectedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_list_dialog, container, false);

        groupsRecyclerView = view.findViewById(R.id.groupsRecyclerView);
        groupsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        groupList = new ArrayList<>();
        adapter = new GroupAdapter(groupList, groupName -> {
            if (listener != null) {
                listener.onGroupSelected(groupName);
            }
            dismiss();
        });
        groupsRecyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        loadGroups();

        return view;
    }

    private void loadGroups() {
        db.collection("groups")
                .whereArrayContains("members", auth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        groupList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Group group = document.toObject(Group.class);
                            groupList.add(group.getGroupName());
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }
}
