package com.example.turismo;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CalendarFragment extends Fragment {

    private static final String ARG_NAME = "name";
    private static final String ARG_LAT = "latitude";
    private static final String ARG_LNG = "longitude";
    private static final int REQUEST_ACCOUNT_PICKER = 1000;
    private static final int REQUEST_AUTHORIZATION = 1001;

    private String name;
    private double latitude;
    private double longitude;

    private GoogleAccountCredential credential;
    private Calendar calendarService;
    private RecyclerView eventsRecyclerView;
    private EventsAdapter eventsAdapter;
    private List<Event> eventList;

    private String pendingSummary;
    private String pendingLocation;
    private DateTime pendingStartDateTime;
    private DateTime pendingEndDateTime;

    public CalendarFragment() {
        // Required empty public constructor
    }

    public static CalendarFragment newInstance(String name, double lat, double lng) {
        CalendarFragment fragment = new CalendarFragment();
        Bundle args = new Bundle();
        args.putString(ARG_NAME, name);
        args.putDouble(ARG_LAT, lat);
        args.putDouble(ARG_LNG, lng);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            name = getArguments().getString(ARG_NAME);
            latitude = getArguments().getDouble(ARG_LAT);
            longitude = getArguments().getDouble(ARG_LNG);
        }

        credential = GoogleAccountCredential.usingOAuth2(
                getContext(), Collections.singleton(CalendarScopes.CALENDAR));
        startActivityForResult(credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        eventsRecyclerView = view.findViewById(R.id.eventsRecyclerView);
        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        eventList = new ArrayList<>();
        eventsAdapter = new EventsAdapter(eventList, this::deleteEvent);
        eventsRecyclerView.setAdapter(eventsAdapter);

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(v -> openAddEventDialog());

        // Check if there are any pre-filled location details
        if (name != null && latitude != 0 && longitude != 0) {
            openAddEventDialogWithLocation(name, latitude + "," + longitude);
        }

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == Activity.RESULT_OK && data != null && data.getExtras() != null) {
                    String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        credential.setSelectedAccountName(accountName);
                        calendarService = new Calendar.Builder(
                                new NetHttpTransport(),
                                JacksonFactory.getDefaultInstance(),
                                credential)
                                .setApplicationName("Turismo")
                                .build();
                        fetchEventsFromCalendar();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == Activity.RESULT_OK) {
                    if (pendingSummary != null && pendingLocation != null && pendingStartDateTime != null && pendingEndDateTime != null) {
                        addEventToCalendar(pendingSummary, pendingLocation, pendingStartDateTime, pendingEndDateTime);
                    }
                }
                break;
        }
    }

    private void openAddEventDialog() {
        AddEventDialogFragment dialog = new AddEventDialogFragment(this::prepareToAddEvent);
        dialog.show(getParentFragmentManager(), "AddEventDialogFragment");
    }

    private void openAddEventDialogWithLocation(String name, String location) {
        AddEventDialogFragment dialog = new AddEventDialogFragment(this::prepareToAddEvent, name, location);
        dialog.show(getParentFragmentManager(), "AddEventDialogFragment");
    }

    private void fetchEventsFromCalendar() {
        new Thread(() -> {
            try {
                List<Event> items = calendarService.events().list("primary")
                        .setMaxResults(10)
                        .setTimeMin(new DateTime(System.currentTimeMillis()))
                        .setOrderBy("startTime")
                        .setSingleEvents(true)
                        .execute()
                        .getItems();

                getActivity().runOnUiThread(() -> {
                    eventList.clear();
                    eventList.addAll(items);
                    eventsAdapter.notifyDataSetChanged();
                });
            } catch (IOException e) {
                getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Error fetching events: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void prepareToAddEvent(String summary, String location, DateTime startDateTime, DateTime endDateTime) {
        this.pendingSummary = summary;
        this.pendingLocation = location;
        this.pendingStartDateTime = startDateTime;
        this.pendingEndDateTime = endDateTime;
        addEventToCalendar(summary, location, startDateTime, endDateTime);
    }

    public void addLocation(String name, double lat, double lng) {
        String location = lat + "," + lng;
        openAddEventDialogWithLocation(name, location);
    }

    private void addEventToCalendar(String summary, String location, DateTime startDateTime, DateTime endDateTime) {
        if (calendarService == null) {
            Toast.makeText(getContext(), "Google Calendar API not initialized", Toast.LENGTH_SHORT).show();
            return;
        }

        Event event = new Event()
                .setSummary(summary)
                .setLocation(location)
                .setDescription("Event at " + location);

        EventDateTime start = new EventDateTime()
                .setDateTime(startDateTime)
                .setTimeZone("America/Los_Angeles");
        event.setStart(start);

        EventDateTime end = new EventDateTime()
                .setDateTime(endDateTime)
                .setTimeZone("America/Los_Angeles");
        event.setEnd(end);

        new Thread(() -> {
            try {
                Event createdEvent = calendarService.events().insert("primary", event).execute();
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Event created: " + createdEvent.getHtmlLink(), Toast.LENGTH_SHORT).show();
                    fetchEventsFromCalendar();  // Refresh the events list
                });
            } catch (UserRecoverableAuthIOException e) {
                startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
            } catch (GoogleJsonResponseException e) {
                getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Error: " + e.getDetails().getMessage(), Toast.LENGTH_SHORT).show());
            } catch (IOException e) {
                getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "IO Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void deleteEvent(Event event) {
        new Thread(() -> {
            try {
                calendarService.events().delete("primary", event.getId()).execute();
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Event deleted", Toast.LENGTH_SHORT).show();
                    fetchEventsFromCalendar();  // Refresh the events list
                });
            } catch (IOException e) {
                getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Error deleting event: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}
