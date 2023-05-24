package com.example.bilgo.model;

import com.google.firebase.firestore.GeoPoint;

public class TripModel {

    private GeoPoint departurePoint;
    private GeoPoint destinationPoint;
    private String date;
    private int seatsAvailable = 4;

    public TripModel() {

    }

    public TripModel(GeoPoint departurePoint, GeoPoint destinationPoint, String date, int seatsAvailable) {
        this.departurePoint = departurePoint;
        this.destinationPoint = destinationPoint;
        this.date = date;
        this.seatsAvailable = seatsAvailable;
    }

    public GeoPoint getDeparturePoint() {
        return departurePoint;
    }

    public void setDeparturePoint(GeoPoint departurePoint) {
        this.departurePoint = departurePoint;
    }

    public GeoPoint getDestinationPoint() {
        return destinationPoint;
    }

    public void setDestinationPoint(GeoPoint destinationPoint) {
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
