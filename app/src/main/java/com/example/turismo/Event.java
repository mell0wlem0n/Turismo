package com.example.turismo;

import com.google.firebase.firestore.DocumentId;

public class Event {
    @DocumentId
    private String id;
    private String summary;
    private String location;
    private long date;
    private long startDateTime;
    private long endDateTime;
    private String reason;
    private String userId;

    public Event() {
        // Default constructor required for Firestore
    }

    public Event(String summary, String location, long startDateTime, long endDateTime, String reason, String userId) {
        this.summary = summary;
        this.location = location;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.reason = reason;
        date = startDateTime;
        this.userId = userId;
    }

    public Event(String summary, String location, long date, long startDateTime, long endDateTime, String reason, String userId) {
        this.summary = summary;
        this.location = location;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.date = date;
        this.reason = reason;
        this.userId = userId;
    }

    // Getters and setters...

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public long getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(long startDateTime) {
        this.startDateTime = startDateTime;
    }

    public long getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(long endDateTime) {
        this.endDateTime = endDateTime;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
