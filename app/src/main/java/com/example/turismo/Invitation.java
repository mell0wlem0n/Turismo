package com.example.turismo;

public class Invitation {
    private String groupId;
    private String groupName;
    private String userId;

    public Invitation() {
        // Firestore requires a public no-arg constructor
    }

    public Invitation(String groupId, String groupName, String userId) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.userId = userId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
