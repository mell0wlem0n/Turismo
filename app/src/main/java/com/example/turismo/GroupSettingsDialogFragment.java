package com.example.turismo;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class GroupSettingsDialogFragment extends DialogFragment {

    private static final String ARG_GROUP_NAME = "group_name";
    private static final String ARG_MEMBERS = "members";
    private static final String ARG_GROUP_ID = "group_id";
    private static final String ARG_MEMBER_NAME = "name";
    private String groupName;
    private List<String> memberNames;
    private List<String> members;
    private EditText groupNameEditText;
    private TextView groupNameTextView;
    private ImageButton editButton;
    private Button leaveButton;
    private Button showMembersLocationButton;
    private Button showGroupEventsButton;
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
        void onShowMembersLocation(List<String> memberIds);
    }

    public static GroupSettingsDialogFragment newInstance(String groupName, List<String> members, String groupId, List<String> names) {
        GroupSettingsDialogFragment fragment = new GroupSettingsDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_GROUP_NAME, groupName);
        args.putStringArrayList(ARG_MEMBER_NAME, new ArrayList<>(names));
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
            memberNames = getArguments().getStringArrayList(ARG_MEMBER_NAME);
        }

        groupNameTextView = view.findViewById(R.id.groupNameTextView);
        groupNameEditText = view.findViewById(R.id.groupNameEditText);
        editButton = view.findViewById(R.id.editButton);
        leaveButton = view.findViewById(R.id.leaveButton);
        showMembersLocationButton = view.findViewById(R.id.showLocationsButton);
        showGroupEventsButton = view.findViewById(R.id.showGroupEventsButton);
        membersRecyclerView = view.findViewById(R.id.membersRecyclerView);
        addMemberEmailEditText = view.findViewById(R.id.addMemberEmailEditText);
        addMemberButton = view.findViewById(R.id.addMemberButton);

        groupNameTextView.setText(groupName);
        groupNameEditText.setText(groupName);

        if (members == null) {
            members = new ArrayList<>();
        }

        membersAdapter = new MembersAdapter(memberNames);
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

        showMembersLocationButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onShowMembersLocation(members);
            }
            MapFragment.GROUP_ID = groupId;
            Log.d("GroupSettingsDialog", "Fetching locations for members: " + members);

            db.collection("users").get().addOnSuccessListener(queryDocumentSnapshots -> {
                List<UserLocation> userLocations = new ArrayList<>();
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    String userId = document.getId();
                    if (members.contains(userId)) {
                        String username = document.getString("username");
                        String locationString = document.getString("location");
                        String profileImageUrl = document.getString("profileImageUrl"); // Get the profile image URL

                        Log.d("GroupSettingsDialog", "User: " + username + ", Location String: " + locationString);
                        if (locationString != null && !locationString.isEmpty()) {
                            try {
                                String[] locationParts = locationString.split(",");
                                double latitude = Double.parseDouble(locationParts[0].trim());
                                double longitude = Double.parseDouble(locationParts[1].trim());
                                userLocations.add(new UserLocation(username, latitude, longitude, profileImageUrl));
                            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                                Log.d("GroupSettingsDialog", "Failed to parse location for user: " + username);
                            }
                        } else {
                            Log.d("GroupSettingsDialog", "Location is null or empty for user: " + username);
                        }
                    }
                }

                LocationDataStore.getInstance().setUserLocations(userLocations);
                if (userLocations.isEmpty()) {
                    Log.d("GroupSettingsDialog", "No user locations found");
                } else {
                    Log.d("GroupSettingsDialog", "User locations found: " + userLocations);
                }

                Intent intent = new Intent(getActivity(), MapActivity.class);
                startActivity(intent);
            }).addOnFailureListener(e -> {
                Log.e("GroupSettingsDialog", "Error fetching user locations", e);
            });
        });

        showGroupEventsButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), GroupEventsActivity.class);
            intent.putExtra("group_id", groupId);
            startActivity(intent);
        });

        return view;
    }

    private void sendInvitation(String userId, String groupName, String groupId) {
        Invitation invitation = new Invitation(groupId, groupName, userId);
        db.collection("invitations")
                .add(invitation)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getContext(), "Invitation sent", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to send invitation", Toast.LENGTH_SHORT).show();
                });
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
                    sendInvitation(userId, groupName, groupId);
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

    private class MembersAdapter extends RecyclerView.Adapter<MembersAdapter.ViewHolder> {
        private final List<String> memberNames;

        MembersAdapter(List<String> memberNames) {
            this.memberNames = memberNames;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            String name = memberNames.get(position);
            holder.nameTextView.setText(name);
        }

        @Override
        public int getItemCount() {
            return memberNames.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView nameTextView;

            ViewHolder(View itemView) {
                super(itemView);
                nameTextView = itemView.findViewById(android.R.id.text1);
            }
        }
    }
}
