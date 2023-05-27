package com.example.bilgo.model;

import com.google.firebase.firestore.FirebaseFirestore;
import java.util.List;

public class GroupModel {
    private String groupId;
    private String groupName;
    private List<String> memberIds;


    public GroupModel() {
        // Required empty constructor for Firestore
    }

    public GroupModel(String groupName, List<String> memberIds) {
        this.groupId = FirebaseFirestore.getInstance().collection("groups").document().getId();
        this.groupName = groupName;
        this.memberIds = memberIds;
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

    public List<String> getMemberIds() {
        return memberIds;
    }

    public void setMemberIds(List<String> memberIds) {
        this.memberIds = memberIds;
    }
}

