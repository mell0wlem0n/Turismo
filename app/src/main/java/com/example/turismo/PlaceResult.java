package com.example.turismo;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.*;

import java.util.List;

public class PlaceResult {
    public String name;
    public String address;
    public LatLng location;
    public List<Place.Type> types;
    public double rating;
    public String phoneNumber;
    public String website;
    public OpeningHours openingHours;
    public String priceLevel;
    public List<PhotoMetadata> photoMetadatas;
    public String iconUrl; // Added field

    public PlaceResult(String name, String address, LatLng location, List<Place.Type> types, double rating,
                       String phoneNumber, String website, OpeningHours openingHours, String priceLevel,
                       List<PhotoMetadata> photoMetadatas, String iconUrl) {
        this.name = name;
        this.address = address;
        this.location = location;
        this.types = types;
        this.rating = rating;
        this.phoneNumber = phoneNumber;
        this.website = website;
        this.openingHours = openingHours;
        this.priceLevel = priceLevel;
        this.photoMetadatas = photoMetadatas;
        this.iconUrl = iconUrl; // Initialize field
    }

    public PlaceResult(String name, String address, LatLng location, List<Place.Type> types, double rating,
                       String phoneNumber, String website, OpeningHours openingHours, String priceLevel,
                       List<PhotoMetadata> photoMetadatas) {
        this.name = name;
        this.address = address;
        this.location = location;
        this.types = types;
        this.rating = rating;
        this.phoneNumber = phoneNumber;
        this.website = website;
        this.openingHours = openingHours;
        this.priceLevel = priceLevel;
        this.photoMetadatas = photoMetadatas;
    }
    public PlaceResult(LatLng latLng)
    {
        this.location = latLng;
    }

    public List<Place.Type> getTypes() {
        return types;
    }
}
