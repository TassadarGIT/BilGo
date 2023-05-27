package com.example.bilgo.model;

import com.google.firebase.Timestamp;

public class MessageModel {
    private String messageId;
    private String groupId;
    private String senderId;
    private String content;
    private Timestamp timestamp;

    public MessageModel() {
        // Required empty constructor for Firestore
    }

    public MessageModel(String messageId, String groupId, String senderId, String content, Timestamp timestamp) {
        this.messageId = messageId;
        this.groupId = groupId;
        this.senderId = senderId;
        this.content = content;
        this.timestamp = timestamp;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
