package com.example.turismo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder> {

    private List<PhotoMetadata> photoMetadataList;
    private PlacesClient placesClient;

    public PhotoAdapter(List<PhotoMetadata> photoMetadataList, PlacesClient placesClient) {
        this.photoMetadataList = photoMetadataList;
        this.placesClient = placesClient;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PhotoMetadata metadata = photoMetadataList.get(position);
        FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(metadata)
                .setMaxWidth(500) // Specify the max width of the image
                .setMaxHeight(300) // Specify the max height of the image
                .build();
        placesClient.fetchPhoto(photoRequest).addOnSuccessListener(fetchPhotoResponse -> {
            Glide.with(holder.imageView.getContext())
                    .load(fetchPhotoResponse.getBitmap())
                    .into(holder.imageView);
        }).addOnFailureListener(exception -> {
            holder.imageView.setImageResource(R.drawable.globe); // Default image on error
        });
    }

    @Override
    public int getItemCount() {
        return photoMetadataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
        }
    }
}

