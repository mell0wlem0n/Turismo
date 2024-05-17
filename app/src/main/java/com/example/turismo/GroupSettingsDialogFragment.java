package com.example.turismo;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class GroupSettingsDialogFragment extends DialogFragment {

    private static final String ARG_GROUP_NAME = "group_name";
    private static final String ARG_MEMBERS = "members";
    private static final String ARG_GROUP_ID = "group_id";
    private String groupName;
    private List<String> members;
    private EditText groupNameEditText;
    private TextView groupNameTextView;
    private ImageButton editButton;
    private Button leaveButton;
    private RecyclerView membersRecyclerView;
    private MembersAdapter membersAdapter;
    private GroupSettingsListener listener;
    private EditText addMemberEmailEditText;
    private Button addMemberButton;
    private FirebaseFirestore db;
    private String groupId;

    public interface GroupSettingsListener {
        void onGroupNameChanged(String newName);
        void onLeaveGroup();
    }

    public static GroupSettingsDialogFragment newInstance(String groupName, List<String> members, String groupId) {
        GroupSettingsDialogFragment fragment = new GroupSettingsDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_GROUP_NAME, groupName);
        args.putStringArrayList(ARG_MEMBERS, new ArrayList<>(members));
        args.putString(ARG_GROUP_ID, groupId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof GroupSettingsListener) {
            listener = (GroupSettingsListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement GroupSettingsListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_settings_dialog, container, false);

        if (getArguments() != null) {
            groupName = getArguments().getString(ARG_GROUP_NAME);
            members = getArguments().getStringArrayList(ARG_MEMBERS);
            groupId = getArguments().getString(ARG_GROUP_ID);
        }

        groupNameTextView = view.findViewById(R.id.groupNameTextView);
        groupNameEditText = view.findViewById(R.id.groupNameEditText);
        editButton = view.findViewById(R.id.editButton);
        leaveButton = view.findViewById(R.id.leaveButton);
        membersRecyclerView = view.findViewById(R.id.membersRecyclerView);
        addMemberEmailEditText = view.findViewById(R.id.addMemberEmailEditText);
        addMemberButton = view.findViewById(R.id.addMemberButton);

        groupNameTextView.setText(groupName);
        groupNameEditText.setText(groupName);

        if (members == null) {
            members = new ArrayList<>();
        }

        membersAdapter = new MembersAdapter(members);
        membersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        membersRecyclerView.setAdapter(membersAdapter);

        db = FirebaseFirestore.getInstance();

        editButton.setOnClickListener(v -> {
            groupNameTextView.setVisibility(View.GONE);
            groupNameEditText.setVisibility(View.VISIBLE);
            groupNameEditText.requestFocus();
        });

        groupNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String newName = s.toString().trim();
                if (!newName.isEmpty()) {
                    listener.onGroupNameChanged(newName);
                    groupNameTextView.setText(newName);
                }
            }
        });

        leaveButton.setOnClickListener(v -> listener.onLeaveGroup());

        addMemberButton.setOnClickListener(v -> {
            String email = addMemberEmailEditText.getText().toString().trim();
            if (!email.isEmpty()) {
                addUserToGroupByEmail(email);
            } else {
                Toast.makeText(getContext(), "Email cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void addUserToGroupByEmail(String email) {
        if (groupId == null) {
            Toast.makeText(getContext(), "Group ID is null", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("users").whereEqualTo("email", email).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                String userId = task.getResult().getDocuments().get(0).getId();
                if (!members.contains(userId)) {
                    members.add(userId);
                    db.collection("groups").document(groupId)
                            .update("members", members)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(getContext(), "User added to group", Toast.LENGTH_SHORT).show();
                                membersAdapter.notifyDataSetChanged();
                            })
                            .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to add user", Toast.LENGTH_SHORT).show());
                } else {
                    Toast.makeText(getContext(), "User already in group", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "User not found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        }
    }
}
