package com.example.turismo;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventViewHolder> {

    private List<DocumentSnapshot> eventList;
    private OnDeleteEventListener onDeleteEventListener;

    public interface OnDeleteEventListener {
        void onDeleteEvent(DocumentSnapshot event);
    }

    public EventsAdapter(List<DocumentSnapshot> eventList, OnDeleteEventListener onDeleteEventListener) {
        this.eventList = eventList;
        this.onDeleteEventListener = onDeleteEventListener;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        DocumentSnapshot documentSnapshot = eventList.get(position);
        Event event = documentSnapshot.toObject(Event.class);

        if (event != null) {
            Log.d("TRYING TO LOAD", "TRYING TO LOAD");
            holder.summaryTextView.setText(event.getSummary());
            holder.locationTextView.setText(event.getLocation());
            holder.reasonTextView.setText(event.getReason());
            holder.startDateTimeTextView.setText(formatDateTime(event.getStartDateTime()));
            holder.endDateTimeTextView.setText(formatDateTime(event.getEndDateTime()));

            holder.itemView.setOnLongClickListener(v -> {
                onDeleteEventListener.onDeleteEvent(documentSnapshot);
                return true;
            });
        }
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    private String formatDateTime(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView summaryTextView;
        TextView locationTextView;
        TextView reasonTextView;
        TextView startDateTimeTextView;
        TextView endDateTimeTextView;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            summaryTextView = itemView.findViewById(R.id.summaryTextView);
            locationTextView = itemView.findViewById(R.id.locationTextView);
            reasonTextView = itemView.findViewById(R.id.reasonTextView);
            startDateTimeTextView = itemView.findViewById(R.id.startDateTimeTextView);
            endDateTimeTextView = itemView.findViewById(R.id.endDateTimeTextView);
        }
    }
}
