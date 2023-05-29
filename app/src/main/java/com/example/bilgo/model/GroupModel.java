package com.example.bilgo.model;

import java.util.List;

public class GroupModel {
    private List<String> memberIds;
    private String groupName;

    public GroupModel() {
        // Required empty constructor for Firestore
    }

    public GroupModel(List<String> memberIds) {
        this.memberIds = memberIds;
    }

    public List<String> getMemberIds() {
        return memberIds;
    }

    public void setMemberIds(List<String> memberIds) {
        this.memberIds = memberIds;
    }

    public String getGroupName(){ return groupName;}
    public void setGroupName(String groupName){this.groupName = groupName;}
}

