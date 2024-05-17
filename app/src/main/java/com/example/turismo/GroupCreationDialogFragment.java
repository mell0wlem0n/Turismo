package com.example.turismo;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class GroupCreationDialogFragment extends DialogFragment {

    private EditText groupNameEditText;
    private Button createGroupButton;
    private GroupCreationListener listener;

    public interface GroupCreationListener {
        void onGroupCreated(String groupName);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Fragment parentFragment = getParentFragment();
        if (parentFragment instanceof GroupCreationListener) {
            listener = (GroupCreationListener) parentFragment;
        } else {
            throw new RuntimeException(parentFragment.toString()
                    + " must implement GroupCreationListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_creation_dialog, container, false);

        groupNameEditText = view.findViewById(R.id.groupNameEditText);
        createGroupButton = view.findViewById(R.id.createGroupButton);

        createGroupButton.setOnClickListener(v -> {
            String groupName = groupNameEditText.getText().toString().trim();
            if (!groupName.isEmpty()) {
                listener.onGroupCreated(groupName);
                dismiss();
            } else {
                Toast.makeText(getContext(), "Group name cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
