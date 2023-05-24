package com.example.bilgo.model;

import com.google.firebase.Timestamp;

public class UserModel {
    private String phone;
    private String name;
    private String surname;
    private String gender;
    private String dateOfBirth;
    private Timestamp createdTimeStamp;
    private String profilePictureLink;
    private int points;
    private int rank;
    private String id;

    public UserModel() {

    }

    public UserModel(String phone, String name, String surname, String gender, String dateOfBirth, Timestamp createdTimeStamp, String profilePictureLink, int points, int rank) {
        this.phone = phone;
        this.name = name;
        this.surname = surname;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.createdTimeStamp = createdTimeStamp;
        this.profilePictureLink = profilePictureLink;
        this.points = 0;
        this.rank = -1;
    }

    public String getProfilePictureLink() {
        return profilePictureLink;
    }

    public void setProfilePictureLink(String profilePictureLink) {
        this.profilePictureLink = profilePictureLink;
    }

    public UserModel(String name, String surname, int points) {
        this.name = name;
        this.surname = surname;
        this.points = points;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
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

    public int getPoints() { return this.points; }
    public void setPoints(int points) { this.points = points; }

    public int getRank() {
        return this.rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
