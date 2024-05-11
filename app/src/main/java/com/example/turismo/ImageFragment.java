package com.example.turismo;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.net.PlacesClient;

public class ImageFragment extends Fragment {
    /*private static final String ARG_PHOTO_METADATA = "photo_metadata";
    private PhotoMetadata photoMetadata;
    private PlacesClient placesClient;

    // Factory method to create a new instance of this fragment using provided parameters
    public static ImageFragment newInstance(PhotoMetadata photoMetadata, PlacesClient placesClient) {
        ImageFragment fragment = new ImageFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PHOTO_METADATA, photoMetadata);
        fragment.setArguments(args);
        fragment.setPlacesClient(placesClient);
        return fragment;
    }

    public void setPlacesClient(PlacesClient placesClient) {
        this.placesClient = placesClient;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            photoMetadata = getArguments().getParcelable(ARG_PHOTO_METADATA);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image, container, false);
        ImageView imageView = view.findViewById(R.id.imageView);
        if (photoMetadata != null && placesClient != null) {
            // Using Glide to load the image
            Glide.with(this)
                    .load(placesClient.getPhoto(photoMetadata).getBitmap())
                    .into(imageView);
        }
        return view;
    }*/
}
