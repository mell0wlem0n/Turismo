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
        CheckBox bankCheckBox = view.findViewById(R.id.checkbox_bank);
        CheckBox hospitalCheckBox = view.findViewById(R.id.checkbox_hospital);
        CheckBox pharmacyCheckBox = view.findViewById(R.id.checkbox_pharmacy);
        CheckBox policeCheckBox = view.findViewById(R.id.checkbox_police);
        CheckBox cafeCheckBox = view.findViewById(R.id.checkbox_cafe);
        CheckBox barCheckBox = view.findViewById(R.id.checkbox_bar);
        CheckBox bakeryCheckBox = view.findViewById(R.id.checkbox_bakery);
        CheckBox movieTheaterCheckBox = view.findViewById(R.id.checkbox_movie_theater);
        CheckBox amusementParkCheckBox = view.findViewById(R.id.checkbox_amusement_park);
        CheckBox nightClubCheckBox = view.findViewById(R.id.checkbox_night_club);
        CheckBox zooCheckBox = view.findViewById(R.id.checkbox_zoo);
        CheckBox clothingStoreCheckBox = view.findViewById(R.id.checkbox_clothing_store);
        CheckBox shoppingMallCheckBox = view.findViewById(R.id.checkbox_shopping_mall);
        CheckBox bookStoreCheckBox = view.findViewById(R.id.checkbox_book_store);
        CheckBox groceryStoreCheckBox = view.findViewById(R.id.checkbox_grocery_or_supermarket);
        CheckBox trainStationCheckBox = view.findViewById(R.id.checkbox_train_station);
        CheckBox busStationCheckBox = view.findViewById(R.id.checkbox_bus_station);
        CheckBox airportCheckBox = view.findViewById(R.id.checkbox_airport);
        CheckBox subwayStationCheckBox = view.findViewById(R.id.checkbox_subway_station);
        // Add more checkboxes as needed

        if (selectedPlaceTypes != null) {
            if (selectedPlaceTypes.contains("restaurant")) {
                restaurantCheckBox.setChecked(true);
            }
            if (selectedPlaceTypes.contains("atm")) {
                atmCheckBox.setChecked(true);
            }
            if (selectedPlaceTypes.contains("bank")) {
                bankCheckBox.setChecked(true);
            }
            if (selectedPlaceTypes.contains("hospital")) {
                hospitalCheckBox.setChecked(true);
            }
            if (selectedPlaceTypes.contains("pharmacy")) {
                pharmacyCheckBox.setChecked(true);
            }
            if (selectedPlaceTypes.contains("police")) {
                policeCheckBox.setChecked(true);
            }
            if (selectedPlaceTypes.contains("cafe")) {
                cafeCheckBox.setChecked(true);
            }
            if (selectedPlaceTypes.contains("bar")) {
                barCheckBox.setChecked(true);
            }
            if (selectedPlaceTypes.contains("bakery")) {
                bakeryCheckBox.setChecked(true);
            }
            if (selectedPlaceTypes.contains("movie_theater")) {
                movieTheaterCheckBox.setChecked(true);
            }
            if (selectedPlaceTypes.contains("amusement_park")) {
                amusementParkCheckBox.setChecked(true);
            }
            if (selectedPlaceTypes.contains("night_club")) {
                nightClubCheckBox.setChecked(true);
            }
            if (selectedPlaceTypes.contains("zoo")) {
                zooCheckBox.setChecked(true);
            }
            if (selectedPlaceTypes.contains("clothing_store")) {
                clothingStoreCheckBox.setChecked(true);
            }
            if (selectedPlaceTypes.contains("shopping_mall")) {
                shoppingMallCheckBox.setChecked(true);
            }
            if (selectedPlaceTypes.contains("book_store")) {
                bookStoreCheckBox.setChecked(true);
            }
            if (selectedPlaceTypes.contains("grocery_or_supermarket")) {
                groceryStoreCheckBox.setChecked(true);
            }
            if (selectedPlaceTypes.contains("train_station")) {
                trainStationCheckBox.setChecked(true);
            }
            if (selectedPlaceTypes.contains("bus_station")) {
                busStationCheckBox.setChecked(true);
            }
            if (selectedPlaceTypes.contains("airport")) {
                airportCheckBox.setChecked(true);
            }
            if (selectedPlaceTypes.contains("subway_station")) {
                subwayStationCheckBox.setChecked(true);
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
            if (bankCheckBox.isChecked()) {
                newSelectedTypes.add("bank");
            }
            if (hospitalCheckBox.isChecked()) {
                newSelectedTypes.add("hospital");
            }
            if (pharmacyCheckBox.isChecked()) {
                newSelectedTypes.add("pharmacy");
            }
            if (policeCheckBox.isChecked()) {
                newSelectedTypes.add("police");
            }
            if (cafeCheckBox.isChecked()) {
                newSelectedTypes.add("cafe");
            }
            if (barCheckBox.isChecked()) {
                newSelectedTypes.add("bar");
            }
            if (bakeryCheckBox.isChecked()) {
                newSelectedTypes.add("bakery");
            }
            if (movieTheaterCheckBox.isChecked()) {
                newSelectedTypes.add("movie_theater");
            }
            if (amusementParkCheckBox.isChecked()) {
                newSelectedTypes.add("amusement_park");
            }
            if (nightClubCheckBox.isChecked()) {
                newSelectedTypes.add("night_club");
            }
            if (zooCheckBox.isChecked()) {
                newSelectedTypes.add("zoo");
            }
            if (clothingStoreCheckBox.isChecked()) {
                newSelectedTypes.add("clothing_store");
            }
            if (shoppingMallCheckBox.isChecked()) {
                newSelectedTypes.add("shopping_mall");
            }
            if (bookStoreCheckBox.isChecked()) {
                newSelectedTypes.add("book_store");
            }
            if (groceryStoreCheckBox.isChecked()) {
                newSelectedTypes.add("grocery_or_supermarket");
            }
            if (trainStationCheckBox.isChecked()) {
                newSelectedTypes.add("train_station");
            }
            if (busStationCheckBox.isChecked()) {
                newSelectedTypes.add("bus_station");
            }
            if (airportCheckBox.isChecked()) {
                newSelectedTypes.add("airport");
            }
            if (subwayStationCheckBox.isChecked()) {
                newSelectedTypes.add("subway_station");
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
