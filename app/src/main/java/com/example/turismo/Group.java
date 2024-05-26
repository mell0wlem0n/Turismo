package com.example.turismo;

import java.util.List;

public class Group {
    private String groupName;
    private List<String> members;
    private String leaderId;

    public Group() {
        // Firestore requires a public no-arg constructor
    }

    public Group(String groupName, List<String> members, String leaderId) {
        this.groupName = groupName;
        this.members = members;
        this.leaderId = leaderId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    public String getLeaderId() {
        return leaderId;
    }

    public void setLeaderId(String leaderId) {
        this.leaderId = leaderId;
    }
}
