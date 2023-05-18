package com.example.bilgo.model;

import android.media.Image;

import com.google.firebase.Timestamp;

public class UserModel {
    private String phone;
    private String username;
    private String gender;
    private String dateOfBirth;
    private Timestamp createdTimeStamp;

    public UserModel() {
    }

    public UserModel(String phone, String username, String gender, String dateOfBirth,  Timestamp createdTimeStamp) {
        this.phone = phone;
        this.username = username;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.createdTimeStamp = createdTimeStamp;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Timestamp getCreatedTimeStamp() {
        return createdTimeStamp;
    }

    public void setCreatedTimeStamp(Timestamp createdTimeStamp) {
        this.createdTimeStamp = createdTimeStamp;
    }
}
