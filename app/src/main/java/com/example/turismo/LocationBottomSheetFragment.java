package com.example.turismo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.OpeningHours;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class LocationBottomSheetFragment extends BottomSheetDialogFragment {
    private static final String ARG_LAT = "latitude";
    private static final String ARG_LNG = "longitude";
    private PlacesClient placesClient;
    private PlaceResult place;
    private OnLocationAddedListener listener;

    public interface OnLocationAddedListener {
        void onLocationAdded(String name, double lat, double lng);
    }

    public static LocationBottomSheetFragment newInstance(double lat, double lng, PlaceResult place, PlacesClient client) {
        Bundle args = new Bundle();
        args.putDouble(ARG_LAT, lat);
        args.putDouble(ARG_LNG, lng);
        LocationBottomSheetFragment fragment = new LocationBottomSheetFragment(place, client);
        fragment.setArguments(args);
        return fragment;
    }

    public LocationBottomSheetFragment(PlaceResult place, PlacesClient client) {
        this.place = place;
        this.placesClient = client;
    }

    public void setOnLocationAddedListener(OnLocationAddedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_bottom_sheet_dialog, container, false);
        TextView latitudeText = v.findViewById(R.id.latitudeText);
        TextView longitudeText = v.findViewById(R.id.longitudeText);
        Bundle args = getArguments();
        if (args != null) {
            double lat = args.getDouble(ARG_LAT);
            double lng = args.getDouble(ARG_LNG);
            latitudeText.setText("Latitude: " + lat);
            longitudeText.setText("Longitude: " + lng);
        }
        updateUI(v);

        Button startNavigationButton = v.findViewById(R.id.startNavigationButton);
        startNavigationButton.setOnClickListener(v1 -> startNavigation());

        Button addLocationToCalendarButton = v.findViewById(R.id.addLocationToCalendarButton);
        addLocationToCalendarButton.setOnClickListener(v1 -> {
            Log.d("LocationBottomSheet", "addLocationToCalendarButton clicked");
            showAddEventDialog(place.name, place.location.latitude, place.location.longitude);
            dismiss();
        });

        Button addEventToGroupButton = v.findViewById(R.id.addEventToGroupButton);
        addEventToGroupButton.setOnClickListener(v1 -> {
            Log.d("LocationBottomSheet", "addEventToGroupButton clicked");
            showGroupLeaderListDialog();
        });

        Button setLocationButton = v.findViewById(R.id.setLocationButton);
        setLocationButton.setOnClickListener(v1 -> showGroupListDialog());

        Button showWeatherButton = v.findViewById(R.id.showWeatherButton);
        showWeatherButton.setOnClickListener(v1 -> showWeatherFragment());

        return v;
    }

    private void setupImageSlider(ViewPager2 viewPager, List<PhotoMetadata> photoMetadataList) {
        PhotoAdapter adapter = new PhotoAdapter(photoMetadataList, placesClient);
        viewPager.setAdapter(adapter);
    }

    public static String formatOpeningHours(OpeningHours openingHours) {
        if (openingHours == null) {
            return "Opening hours not available";
        }
        StringBuilder sb = new StringBuilder();
        for (String day : openingHours.getWeekdayText()) {
            sb.append(day).append("\n");
        }
        return sb.toString().trim(); // Trim to remove the last newline character
    }

    private void updateUI(View view) {
        ((TextView) view.findViewById(R.id.nameText)).setText(place.name);
        ((TextView) view.findViewById(R.id.addressText)).setText(place.address);
        ((TextView) view.findViewById(R.id.phoneText)).setText(place.phoneNumber);
        ((TextView) view.findViewById(R.id.websiteText)).setText(place.websiteUri != null ? place.websiteUri.toString() : "N/A");
        ((TextView) view.findViewById(R.id.openingHoursText)).setText(formatOpeningHours(place.openingHours));
        ((TextView) view.findViewById(R.id.latitudeText)).setText(String.valueOf(place.location.latitude));
        ((TextView) view.findViewById(R.id.longitudeText)).setText(String.valueOf(place.location.longitude));
        if (place.photoMetadatas != null) {
            ViewPager2 imageSlider = view.findViewById(R.id.imageSlider);
            setupImageSlider(imageSlider, place.photoMetadatas);
        }
    }

    private void startNavigation() {
        LatLng destination = new LatLng(place.location.latitude, place.location.longitude);
        String uri = "google.navigation:q=" + destination.latitude + "," + destination.longitude;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setPackage("com.google.android.apps.maps");
        startActivity(intent);
    }

    private void showGroupListDialog() {
        GroupListDialogFragment dialog = new GroupListDialogFragment();
        dialog.setOnGroupSelectedListener(groupName -> {
            // Add the location to the selected group and update the map
            addTargetLocationToGroup(groupName, place.location.latitude, place.location.longitude);
        });
        dialog.show(getParentFragmentManager(), "GroupListDialogFragment");
    }

    private void addTargetLocationToGroup(String groupName, double latitude, double longitude) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String locationString = latitude + "," + longitude;
        db.collection("groups")
                .whereEqualTo("groupName", groupName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        String groupId = task.getResult().getDocuments().get(0).getId();
                        db.collection("groups").document(groupId)
                                .update("targetLocation", locationString)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(getContext(), "Target location set for group: " + groupName, Toast.LENGTH_SHORT).show();
                                });
                    }
                });
    }

    private void showWeatherFragment() {
        if (getArguments() != null) {
            double lat = getArguments().getDouble(ARG_LAT);
            double lng = getArguments().getDouble(ARG_LNG);
            WeatherFragment weatherFragment = WeatherFragment.newInstance(lat, lng);
            weatherFragment.show(getChildFragmentManager(), weatherFragment.getTag());
        }
    }

    private void showAddEventDialog(String name, double lat, double lng) {
        FragmentManager fragmentManager = getParentFragmentManager();
        AddEventDialogFragment dialog = new AddEventDialogFragment((summary, location, startDateTime, endDateTime, reason) -> {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                Event event = new Event(summary, location, startDateTime.getValue(), endDateTime.getValue(), reason, currentUser.getUid());
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("users").document(currentUser.getUid()).collection("events")
                        .add(event)
                        .addOnSuccessListener(documentReference -> {
                            //Toast.makeText(getContext(), "Event added successfully", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> Toast.makeText(getContext(), "Error adding event: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }, name, lat + "," + lng);
        dialog.show(fragmentManager, "AddEventDialogFragment");
    }

    private void showGroupLeaderListDialog() {
        GroupLeaderListDialogFragment dialog = new GroupLeaderListDialogFragment();
        dialog.setOnGroupSelectedListener((groupId, groupName) -> showAddGroupEventDialog(groupId, groupName, place.name, place.location.latitude, place.location.longitude));
        dialog.show(getParentFragmentManager(), "GroupLeaderListDialogFragment");
    }

    private void showAddGroupEventDialog(String groupId, String groupName, String name, double lat, double lng) {
        FragmentManager fragmentManager = getParentFragmentManager();
        AddEventDialogFragment dialog = new AddEventDialogFragment((summary, location, startDateTime, endDateTime, reason) -> {
            Event event = new Event(summary, location, startDateTime.getValue(), endDateTime.getValue(), reason, groupId);
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("groups").document(groupId).collection("events")
                    .add(event)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(getContext(), "Event added to group: " + groupName, Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Error adding event: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }, name, lat + "," + lng);
        dialog.show(fragmentManager, "AddEventDialogFragment");
    }
}
