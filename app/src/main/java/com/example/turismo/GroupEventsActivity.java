package com.example.turismo;

import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class GroupEventsActivity extends AppCompatActivity {

    private static final String EXTRA_GROUP_ID = "group_id";

    private CalendarView calendarView;
    private RecyclerView eventsRecyclerView;
    private EventsAdapter eventsAdapter;
    private List<DocumentSnapshot> eventList;

    private FirebaseFirestore db;
    private String groupId;

    private long selectedDate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_events);

        groupId = getIntent().getStringExtra(EXTRA_GROUP_ID);
        db = FirebaseFirestore.getInstance();
        selectedDate = getDateOnlyInMillis(Calendar.getInstance());

        calendarView = findViewById(R.id.calendarView);
        eventsRecyclerView = findViewById(R.id.eventsRecyclerView);
        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventList = new ArrayList<>();
        eventsAdapter = new EventsAdapter(eventList, this::deleteEvent);
        eventsRecyclerView.setAdapter(eventsAdapter);

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, dayOfMonth);
            selectedDate = getDateOnlyInMillis(calendar);
            fetchGroupEventsFromFirestore();
        });

        fetchGroupEventsFromFirestore();
    }

    private void fetchGroupEventsFromFirestore() {
        if (groupId != null) {
            db.collection("groups").document(groupId).collection("events")
                    .addSnapshotListener((queryDocumentSnapshots, e) -> {
                        if (e != null) {
                            Toast.makeText(GroupEventsActivity.this, "Error fetching events: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void deleteEvent(DocumentSnapshot event) {
        if (groupId != null) {
            db.collection("groups").document(groupId).collection("events").document(event.getId())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(GroupEventsActivity.this, "Event deleted", Toast.LENGTH_SHORT).show();
                        fetchGroupEventsFromFirestore();
                    })
                    .addOnFailureListener(e -> Toast.makeText(GroupEventsActivity.this, "Error deleting event: " + e.getMessage(), Toast.LENGTH_SHORT).show());
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
