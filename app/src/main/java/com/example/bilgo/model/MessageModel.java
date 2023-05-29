package com.example.bilgo.model;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MessageModel {
    private String messageId;
    private String groupId;
    private String senderId;
    private String senderName;
    private String content;
    private Timestamp timestamp;
    private String formattedTimestamp;

    public MessageModel() {
        // Required empty constructor for Firestore
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
    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public void setTimestamp(Timestamp now){
        timestamp = now;
    }
    public String getFormattedTimestamp() {
        // Convert the Timestamp to a Date object
        Date date = timestamp.toDate();

        // Define the desired date and time format
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        // Format the date and time
        formattedTimestamp = sdf.format(date);
        return formattedTimestamp;
    }

}

