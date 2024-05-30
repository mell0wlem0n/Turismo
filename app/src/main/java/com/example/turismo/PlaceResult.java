package com.example.turismo;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.*;

import java.util.List;

public class PlaceResult {
    String name;
    String address;
    LatLng location;
    List<Place.Type> types;
    Double rating;
    String phoneNumber;
    String websiteUri;
    List<PhotoMetadata> photoMetadatas;
    OpeningHours openingHours;
    String priceLevel;



    public PlaceResult(String name, String address, LatLng location, List<Place.Type> types, Double rating, String phoneNumber, String websiteUri, OpeningHours openingHours, String priceLevel, List<PhotoMetadata> photoMetadatas) {
        this.name = name;
        this.address = address;
        this.location = location;
        this.rating = rating;
        this.types = types;
        this.phoneNumber = phoneNumber;
        this.websiteUri = websiteUri;
        this.photoMetadatas = photoMetadatas;
        this.openingHours = openingHours;
        this.priceLevel = priceLevel;
    }

    public PlaceResult(LatLng latLng)
    {
        this.location = latLng;
    }

    public List<Place.Type> getTypes() {
        return types;
    }
}
