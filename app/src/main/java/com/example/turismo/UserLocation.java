package com.example.turismo;

public class UserLocation {
    private String username;
    private double latitude;
    private double longitude;

    public UserLocation(String username, double latitude, double longitude) {
        this.username = username;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getUsername() {
        return username;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
