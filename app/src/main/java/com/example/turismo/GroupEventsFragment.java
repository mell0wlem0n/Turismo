package com.example.turismo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class GroupEventsFragment extends Fragment {

    private static final String ARG_GROUP_ID = "group_id";

    private CalendarView calendarView;
    private RecyclerView eventsRecyclerView;
    private EventsAdapter eventsAdapter;
    private List<DocumentSnapshot> eventList;

    private FirebaseFirestore db;
    private String groupId;

    private long selectedDate;

    public static GroupEventsFragment newInstance(String groupId) {
        GroupEventsFragment fragment = new GroupEventsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_GROUP_ID, groupId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            groupId = getArguments().getString(ARG_GROUP_ID);
        }
        db = FirebaseFirestore.getInstance();
        selectedDate = getDateOnlyInMillis(Calendar.getInstance());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_events, container, false);

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
            fetchGroupEventsFromFirestore();
        });

        fetchGroupEventsFromFirestore();

        return view;
    }

    private void fetchGroupEventsFromFirestore() {
        if (groupId != null) {
            db.collection("groups").document(groupId).collection("events")
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

    private void deleteEvent(DocumentSnapshot event) {
        if (groupId != null) {
            db.collection("groups").document(groupId).collection("events").document(event.getId())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Event deleted", Toast.LENGTH_SHORT).show();
                        fetchGroupEventsFromFirestore();
                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Error deleting event: " + e.getMessage(), Toast.LENGTH_SHORT).show());
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
