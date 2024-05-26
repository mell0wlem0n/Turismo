package com.example.turismo;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class GroupFragment extends Fragment implements GroupCreationDialogFragment.GroupCreationListener {

    private RecyclerView groupsRecyclerView;
    private TextView emptyGroupText;
    private FirebaseFirestore db;
    private GroupAdapter adapter;
    private List<String> groupList;
    private FirebaseUser currentUser;

    public GroupFragment() {
        // Required empty public constructor
    }

    public static GroupFragment newInstance() {
        return new GroupFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group, container, false);

        groupsRecyclerView = view.findViewById(R.id.groupsRecyclerView);
        emptyGroupText = view.findViewById(R.id.emptyGroupText);
        FloatingActionButton createGroupButton = view.findViewById(R.id.floatingActionButton);
        FloatingActionButton viewInvitationsButton = view.findViewById(R.id.viewInvitationsButton);

        groupList = new ArrayList<>();
        adapter = new GroupAdapter(groupList, groupName -> {
            Intent intent = new Intent(getActivity(), ChatActivity.class);
            intent.putExtra("GROUP_NAME", groupName);
            startActivity(intent);
        });

        groupsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        groupsRecyclerView.setAdapter(adapter);

        createGroupButton.setOnClickListener(v -> {
            GroupCreationDialogFragment dialog = new GroupCreationDialogFragment();
            dialog.show(getChildFragmentManager(), "GroupCreationDialogFragment");
        });

        viewInvitationsButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), InvitationsActivity.class);
            startActivity(intent);
        });

        loadGroups();

        return view;
    }

    private void loadGroups() {
        db.collection("groups")
                .whereArrayContains("members", currentUser.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        groupList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Group group = document.toObject(Group.class);
                            groupList.add(group.getGroupName());
                        }
                        adapter.notifyDataSetChanged();
                        if (groupList.isEmpty()) {
                            emptyGroupText.setVisibility(View.VISIBLE);
                            groupsRecyclerView.setVisibility(View.GONE);
                        } else {
                            emptyGroupText.setVisibility(View.GONE);
                            groupsRecyclerView.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    @Override
    public void onGroupCreated(String groupName) {
        // Create a new group with the specified name and set the current user as the leader
        List<String> members = new ArrayList<>();
        members.add(currentUser.getUid());
        Group newGroup = new Group(groupName, members, currentUser.getUid());

        db.collection("groups")
                .add(newGroup)
                .addOnSuccessListener(documentReference -> {
                    loadGroups();
                });
    }
}
