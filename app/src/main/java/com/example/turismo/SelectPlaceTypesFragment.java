package com.example.turismo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import java.util.ArrayList;
import java.util.List;

public class SelectPlaceTypesFragment extends DialogFragment {

    private ArrayList<String> selectedPlaceTypes;
    private PlaceTypesSelectedListener placeTypesSelectedListener;

    public interface PlaceTypesSelectedListener {
        void onPlaceTypesSelected(List<String> selectedTypes);
    }

    public void setPlaceTypesSelectedListener(PlaceTypesSelectedListener listener) {
        this.placeTypesSelectedListener = listener;
    }

    public static SelectPlaceTypesFragment newInstance(ArrayList<String> selectedPlaceTypes) {
        SelectPlaceTypesFragment fragment = new SelectPlaceTypesFragment();
        Bundle args = new Bundle();
        args.putStringArrayList("selectedPlaceTypes", selectedPlaceTypes);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_place_types, container, false);

        if (getArguments() != null) {
            selectedPlaceTypes = getArguments().getStringArrayList("selectedPlaceTypes");
        }

        // Initialize checkboxes and set them based on selectedPlaceTypes
        CheckBox restaurantCheckBox = view.findViewById(R.id.checkbox_restaurant);
        CheckBox atmCheckBox = view.findViewById(R.id.checkbox_atm);
        // Add more checkboxes as needed

        if (selectedPlaceTypes != null) {
            if (selectedPlaceTypes.contains("restaurant")) {
                restaurantCheckBox.setChecked(true);
            }
            if (selectedPlaceTypes.contains("atm")) {
                atmCheckBox.setChecked(true);
            }
            // Handle more checkboxes similarly
        }

        Button applyButton = view.findViewById(R.id.apply_button);
        applyButton.setOnClickListener(v -> {
            ArrayList<String> newSelectedTypes = new ArrayList<>();
            if (restaurantCheckBox.isChecked()) {
                newSelectedTypes.add("restaurant");
            }
            if (atmCheckBox.isChecked()) {
                newSelectedTypes.add("atm");
            }
            // Check more checkboxes similarly

            if (placeTypesSelectedListener != null) {
                placeTypesSelectedListener.onPlaceTypesSelected(newSelectedTypes);
            }
            dismiss();
        });

        return view;
    }
}
