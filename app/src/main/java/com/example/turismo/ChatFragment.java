package com.example.turismo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ChatFragment extends Fragment {

    private SharedViewModel sharedViewModel;
    private TextView textView;
    private FloatingActionButton fab;
    private Button exitGroupButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textView = view.findViewById(R.id.emptyGroupText);
        fab = view.findViewById(R.id.floatingActionButton);
        exitGroupButton = view.findViewById(R.id.exit_group_button);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedViewModel.triggerAddIcon();
            }
        });

        sharedViewModel.getIconCount().observe(getViewLifecycleOwner(), count -> {
            if (count != null && count > 0) {
                textView.setVisibility(View.GONE);
            } else {
                textView.setVisibility(View.VISIBLE);
            }
        });

        sharedViewModel.getSelectedGroup().observe(getViewLifecycleOwner(), groupId -> {
            if (groupId != null) {
                fab.setVisibility(View.GONE);
                textView.setText("Chat for group " + groupId);
                textView.setVisibility(View.VISIBLE);
                exitGroupButton.setVisibility(View.VISIBLE);
            } else {
                fab.setVisibility(View.VISIBLE);
                textView.setText("This is the Chat Fragment");
                exitGroupButton.setVisibility(View.GONE);
            }
        });

        exitGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedViewModel.clearSelectedGroup();
            }
        });
    }
}
