package com.example.bilgo.model;

import com.google.firebase.firestore.GeoPoint;

public class TripModel {

    private String departurePoint;
    private String destinationPoint;
    private String date;
    private int seatsAvailable = 4;

    public TripModel() {

    }

    public TripModel(String departurePoint, String destinationPoint, String date, int seatsAvailable) {
        this.departurePoint = departurePoint;
        this.destinationPoint = destinationPoint;
        this.date = date;
        this.seatsAvailable = seatsAvailable;
    }

    public String getDeparturePoint() {
        return departurePoint;
    }

    public void setDeparturePoint(String departurePoint) {
        this.departurePoint = departurePoint;
    }

    public String getDestinationPoint() {
        return destinationPoint;
    }

    public void setDestinationPoint(String destinationPoint) {
        this.destinationPoint = destinationPoint;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getSeatsAvailable() {
        return seatsAvailable;
    }

    public void setSeatsAvailable(int seatsAvailable) {
        this.seatsAvailable = seatsAvailable;
    }
}
