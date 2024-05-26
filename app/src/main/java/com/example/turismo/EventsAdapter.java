package com.example.turismo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.api.services.calendar.model.Event;

import java.util.List;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventViewHolder> {

    private final List<Event> events;
    private final EventDeleteCallback deleteCallback;

    public interface EventDeleteCallback {
        void onDeleteEvent(Event event);
    }

    public EventsAdapter(List<Event> events, EventDeleteCallback deleteCallback) {
        this.events = events;
        this.deleteCallback = deleteCallback;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = events.get(position);
        holder.bind(event);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    class EventViewHolder extends RecyclerView.ViewHolder {

        private final TextView eventSummary;
        private final TextView eventLocation;
        private final TextView eventStartDate;
        private final TextView eventEndDate;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventSummary = itemView.findViewById(R.id.eventSummary);
            eventLocation = itemView.findViewById(R.id.eventLocation);
            eventStartDate = itemView.findViewById(R.id.eventStartDate);
            eventEndDate = itemView.findViewById(R.id.eventEndDate);

            itemView.findViewById(R.id.deleteButton).setOnClickListener(v -> {
                Event event = events.get(getAdapterPosition());
                deleteCallback.onDeleteEvent(event);
            });
        }

        public void bind(Event event) {
            eventSummary.setText(event.getSummary());
            eventLocation.setText(event.getLocation());
            eventStartDate.setText(event.getStart().getDateTime().toString());
            eventEndDate.setText(event.getEnd().getDateTime().toString());
        }
    }
}
