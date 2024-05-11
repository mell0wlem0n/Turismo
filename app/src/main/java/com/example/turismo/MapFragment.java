package com.example.turismo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

public class MapFragment extends Fragment {

    private final int FINE_PERMISSION_CODE = 1;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private GoogleMap myMap;
    private SearchView mapSearchView;
    private Location currentLocation;
    private PlacesClient placesClient;

    private FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), getString(R.string.maps_api_key));
        }
        placesClient = Places.createClient(requireContext());
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        return inflater.inflate(R.layout.fragment_map, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    myMap = googleMap;
                    setupMap();
                    setupSearchView(view); // Add this line to set up the search view
                }
            });
        }

        FloatingActionMenu fabMenu = view.findViewById(R.id.fab_menu);
        FloatingActionButton fabRestaurant = view.findViewById(R.id.fab_restaurant);
        FloatingActionButton fabATM = view.findViewById(R.id.fab_atm);

        fabRestaurant.setOnClickListener(v -> {
            fetchNearbyPlaces( myMap, "restaurant");
            fabMenu.close(true);
        });

        fabATM.setOnClickListener(v -> {
            fetchNearbyPlaces( myMap, "atm");
            fabMenu.close(true);
        });

        // Close the menu when an item is clicked
        fabMenu.setOnMenuToggleListener(opened -> {
            if (!opened) fabMenu.close(true);
        });
    }

    private void showPlaceDetailsDialog(Place place) {
        Toast.makeText(requireContext(), "Place: " + place.getName() + "\nAddress: " + place.getAddress() + " " + place.getRating() + " " + place.getPhoneNumber(), Toast.LENGTH_LONG).show();

    }


    // Utility method to convert a View to Bitmap
    private Bitmap getBitmapFromView(View view) {
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.draw(canvas);
        return bitmap;
    }

    private void setupMap() {
        if (myMap != null) {
            pinpointCurrentLocation();

            myMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    PlaceResult placeResult = (PlaceResult) marker.getTag();
                    if (placeResult != null) {
                        LocationBottomSheetFragment bottomSheet = LocationBottomSheetFragment.newInstance(placeResult.location.latitude, placeResult.location.longitude, placeResult, placesClient);
                        bottomSheet.show(getChildFragmentManager(), bottomSheet.getTag());
                    }
                    return true; // Return true to indicate that we have consumed the event and no further processing is necessary
                }
            });
            myMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

                @Override
                public void onMapClick(LatLng latLng) {
                    PlaceResult placeResult = new PlaceResult(latLng);
                    LocationBottomSheetFragment bottomSheet = LocationBottomSheetFragment.newInstance(placeResult.location.latitude, placeResult.location.longitude, placeResult, placesClient);
                    bottomSheet.show(getChildFragmentManager(), bottomSheet.getTag());
                }
            });
        } else {
            Log.e("SetupMap", "Google Map is not initialized");
        }
    }


    private void pinpointCurrentLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        getLastLocation();
    }

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_PERMISSION_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
                    addMarkerToCurrentLocation();
                }
            }
        });
    }

    private void addMarkerToCurrentLocation() {
        if (currentLocation != null && myMap != null) {
            LatLng currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            myMap.addMarker(new MarkerOptions().position(currentLatLng).title("Current Location"));
            myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
        } else {
            Toast.makeText(requireContext(), "Unable to fetch current location", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == FINE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            } else {
                Toast.makeText(requireContext(), "Please allow permission for location", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void setupSearchView(View view) {
        // Initialize Places SDK
        /*if (!Places.isInitialized()) {
            Places.initialize(requireContext(), getString(R.string.maps_api_key));
        }*/

        // Get the user's current location
        LocationManager locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));

        // Set up a PlaceSelectionListener to handle the selected place
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        if (autocompleteFragment != null && location != null) {
            autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));

            // Bias the autocomplete results towards the user's current location
            autocompleteFragment.setLocationBias(RectangularBounds.newInstance(
                    new LatLng(location.getLatitude() - 0.1, location.getLongitude() - 0.1), // Southwest corner
                    new LatLng(location.getLatitude() + 0.1, location.getLongitude() + 0.1)  // Northeast corner
            ));

            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(@NonNull Place place) {
                    // Get detailed information about the selected place
                    String placeId = place.getId();
                    List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
                    FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);
                    placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
                        Place selectedPlace = response.getPlace();
                        LatLng latLng = selectedPlace.getLatLng();
                        if (latLng != null) {
                            Marker marker = myMap.addMarker(new MarkerOptions().position(latLng).title(selectedPlace.getName()));
                            getPlaceDetails(placeId, placeResult -> {
                                // Create a marker for each place and set a tag with the placeResult
                                Marker mrk = myMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(placeResult.location.latitude, placeResult.location.longitude))
                                        .title(placeResult.name)
                                        .snippet(placeResult.address));
                                marker.setTag(placeResult);

                                // Here you can add additional marker customization
                            });
                            myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14.0f));
                            Log.d("BBBB", "BBBB");
                            // Display information about the selected place
                            Toast.makeText(requireContext(), "Place: " + selectedPlace.getName() + "\nAddress: " + selectedPlace.getAddress(), Toast.LENGTH_LONG).show();
                        }
                    }).addOnFailureListener((exception) -> {
                        Log.e("Places", "Place not found: " + exception.getMessage());
                    });
                }

                @Override
                public void onError(@NonNull Status status) {
                    // Handle errors
                    Log.e("Places", "An error occurred: " + status);
                }
            });
        }
    }


    public void fetchNearbyPlaces(final GoogleMap googleMap, String placeType) {
        RequestQueue queue = Volley.newRequestQueue(requireContext());
        String apiKey = getString(R.string.maps_api_key);
        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json" +
                "?location=" + currentLocation.getLatitude() + "," + currentLocation.getLongitude() +
                "&radius=" + 1000 +
                "&type=" + placeType +
                "&key=" + apiKey;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray results = jsonObject.getJSONArray("results");
                        for (int i = 0; i < results.length(); i++) {
                            JSONObject result = results.getJSONObject(i);
                            String placeId = result.getString("place_id"); // Retrieve place ID
                            getPlaceDetails(placeId, placeResult -> {
                                // Create a marker for each place and set a tag with the placeResult
                                Marker marker = googleMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(placeResult.location.latitude, placeResult.location.longitude))
                                        .title(placeResult.name)
                                        .snippet(placeResult.address));
                                marker.setTag(placeResult);

                                // Here you can add additional marker customization
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> Toast.makeText(requireContext(), "Error fetching places: " + error.getMessage(), Toast.LENGTH_LONG).show());

        queue.add(stringRequest);

        // Set a marker click listener on the map

    }



    public void getPlaceDetails(String placeId, Consumer<PlaceResult> callback) {
        List<Place.Field> fields = Arrays.asList(
                Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG,
                Place.Field.TYPES, Place.Field.RATING, Place.Field.PHONE_NUMBER, Place.Field.WEBSITE_URI,
                Place.Field.OPENING_HOURS, Place.Field.PRICE_LEVEL, Place.Field.PHOTO_METADATAS
        );

        FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, fields);
        placesClient.fetchPlace(request).addOnSuccessListener(response -> {
            Place place = response.getPlace();
            PlaceResult result = new PlaceResult(
                    place.getName(),
                    place.getAddress(),
                    place.getLatLng(),
                    place.getTypes(),
                    place.getRating(),
                    place.getPhoneNumber(),
                    place.getWebsiteUri() != null ? place.getWebsiteUri().toString() : null,
                    place.getOpeningHours(),
                    String.valueOf(place.getPriceLevel()),
                    place.getPhotoMetadatas()
            );
            callback.accept(result);
        }).addOnFailureListener(e -> {
            Log.e("Places", "Failed to fetch place details: " + e.getMessage());
        });
    }


}

