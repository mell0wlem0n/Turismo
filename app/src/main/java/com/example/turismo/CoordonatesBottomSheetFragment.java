package com.example.turismo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.viewpager2.widget.ViewPager2;

import com.google.android.libraries.places.api.model.OpeningHours;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;

public class CoordonatesBottomSheetFragment extends BottomSheetDialogFragment {
    private static final String ARG_LAT = "latitude";
    private static final String ARG_LNG = "longitude";
    private PlacesClient placesClient;
    private PlaceResult place;

    public static CoordonatesBottomSheetFragment newInstance(double lat, double lng, PlaceResult place, PlacesClient client) {
        Bundle args = new Bundle();
        args.putDouble(ARG_LAT, lat);
        args.putDouble(ARG_LNG, lng);
        CoordonatesBottomSheetFragment fragment = new CoordonatesBottomSheetFragment(place,client);
        fragment.setArguments(args);
        return fragment;
    }

    public CoordonatesBottomSheetFragment(PlaceResult place, PlacesClient client)
    {
        this.place = place;
        this.placesClient = client;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_coordonates_bottom_sheet, container, false);
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

        return v;
    }



    private void updateUI(View view) {
        ((TextView) view.findViewById(R.id.latitudeText)).setText(String.valueOf(place.location.latitude));
        ((TextView) view.findViewById(R.id.longitudeText)).setText(String.valueOf(place.location.longitude));
    }
}
