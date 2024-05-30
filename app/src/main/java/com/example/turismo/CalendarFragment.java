package com.example.turismo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.api.client.util.DateTime;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CalendarFragment extends Fragment {

    private CalendarView calendarView;
    private RecyclerView eventsRecyclerView;
    private EventsAdapter eventsAdapter;
    private List<DocumentSnapshot> eventList;

    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    private long selectedDate;
    private Set<Long> eventDates;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        selectedDate = getDateOnlyInMillis(Calendar.getInstance());
        eventDates = new HashSet<>();
        fetchAllEventDates();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        calendarView = view.findViewById(R.id.calendarView);
        eventsRecyclerView = view.findViewById(R.id.eventsRecyclerView);
        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        eventList = new ArrayList<>();
        eventsAdapter = new EventsAdapter(eventList, this::deleteEvent);
        eventsRecyclerView.setAdapter(eventsAdapter);

        calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, dayOfMonth);
            selectedDate = getDateOnlyInMillis(calendar);
            fetchEventsFromFirestore();
        });

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(v -> openAddEventDialog());

        fetchEventsFromFirestore();

        return view;
    }

    private void openAddEventDialog() {
        AddEventDialogFragment dialog = new AddEventDialogFragment(this::addEventToFirestore);
        dialog.show(getParentFragmentManager(), "AddEventDialogFragment");
    }

    private void fetchEventsFromFirestore() {
        if (currentUser != null) {
            db.collection("users").document(currentUser.getUid()).collection("events")
                    .addSnapshotListener((queryDocumentSnapshots, e) -> {
                        if (e != null) {
                            Toast.makeText(getContext(), "Error fetching events: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        eventList.clear();
                        if (queryDocumentSnapshots != null) {
                            for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                                Event event = document.toObject(Event.class);
                                if (event != null && isSameDay(event.getDate(), selectedDate)) {
                                    eventList.add(document);
                                }
                            }
                        }
                        eventsAdapter.notifyDataSetChanged();
                    });
        }
    }

    private boolean isSameDay(long eventDateInMillis, long selectedDateInMillis) {
        Calendar eventCalendar = Calendar.getInstance();
        eventCalendar.setTimeInMillis(eventDateInMillis);

        Calendar selectedCalendar = Calendar.getInstance();
        selectedCalendar.setTimeInMillis(selectedDateInMillis);

        return eventCalendar.get(Calendar.YEAR) == selectedCalendar.get(Calendar.YEAR) &&
                eventCalendar.get(Calendar.MONTH) == selectedCalendar.get(Calendar.MONTH) &&
                eventCalendar.get(Calendar.DAY_OF_MONTH) == selectedCalendar.get(Calendar.DAY_OF_MONTH);
    }

    public void addEventToFirestore(String summary, String location, DateTime startDateTime, DateTime endDateTime, String reason) {
        if (currentUser == null) {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        long eventDate = getDateOnlyInMillis(startDateTime.getValue());
        Event event = new Event(summary, location, eventDate, startDateTime.getValue(), endDateTime.getValue(), reason, currentUser.getUid());
        db.collection("users").document(currentUser.getUid()).collection("events")
                .add(event)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getContext(), "Event added successfully", Toast.LENGTH_SHORT).show();
                    fetchEventsFromFirestore();
                    fetchAllEventDates();
                    highlightEventDays();
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error adding event: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void deleteEvent(DocumentSnapshot event) {
        if (currentUser != null) {
            db.collection("users").document(currentUser.getUid()).collection("events").document(event.getId())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Event deleted", Toast.LENGTH_SHORT).show();
                        fetchEventsFromFirestore();
                        fetchAllEventDates();
                        highlightEventDays();
                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Error deleting event: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    private void fetchAllEventDates() {
        if (currentUser != null) {
            db.collection("users").document(currentUser.getUid()).collection("events")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            eventDates.clear();
                            for (DocumentSnapshot document : task.getResult()) {
                                Event event = document.toObject(Event.class);
                                if (event != null) {
                                    eventDates.add(getDateOnlyInMillis(event.getDate()));
                                }
                            }
                            highlightEventDays();
                        }
                    });
        }
    }

    private void highlightEventDays() {
        calendarView.setDate(calendarView.getDate()); // refresh the calendar view
        // Here we will loop through the dates and highlight them.
        long currentDate = getDateOnlyInMillis(Calendar.getInstance());
        for (long date : eventDates) {
            if (date != currentDate) {
                // Custom logic to highlight the date
                highlightDate(calendarView, date);
            }
        }
    }

    private void highlightDate(CalendarView calendarView, long date) {
        // Custom logic to highlight the date
        // You can use background color or drawable resources to highlight the date
        View dateView = calendarView.getChildAt((int) date);
        if (dateView != null) {
            dateView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.red)); // Example color
        }
    }

    private long getDateOnlyInMillis(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    private long getDateOnlyInMillis(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        return getDateOnlyInMillis(calendar);
    }
}
