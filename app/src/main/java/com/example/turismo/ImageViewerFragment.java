package com.example.turismo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;

public class ImageViewerFragment extends DialogFragment {

    private static final String ARG_IMAGE_URL = "image_url";

    public static ImageViewerFragment newInstance(String imageUrl) {
        ImageViewerFragment fragment = new ImageViewerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_IMAGE_URL, imageUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image_viewer, container, false);

        ImageView imageView = view.findViewById(R.id.fullscreenImageView);
        String imageUrl = getArguments().getString(ARG_IMAGE_URL);

        Glide.with(this).load(imageUrl).into(imageView);

        view.setOnClickListener(v -> dismiss());

        return view;
    }
}
