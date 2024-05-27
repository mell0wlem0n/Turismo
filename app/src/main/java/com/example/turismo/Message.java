package com.example.turismo;

public class Message {
    private String text;
    private String senderId;
    private String senderEmail;
    private String senderName;
    private long timestamp;
    private String profileImageUrl; // Add this field

    public Message() {
        // Firestore requires a public no-arg constructor
    }

    public Message(String text, String senderId, String senderEmail, String senderName, String profileImageUrl, long timestamp) {
        this.text = text;
        this.senderId = senderId;
        this.senderEmail = senderEmail;
        this.senderName = senderName;
        this.profileImageUrl = profileImageUrl; // Initialize this field
        this.timestamp = timestamp;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}
