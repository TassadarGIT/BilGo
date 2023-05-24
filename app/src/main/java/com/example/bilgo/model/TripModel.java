package com.example.bilgo.model;

import com.google.firebase.firestore.GeoPoint;

import java.util.Date;

public class TripModel {

    private GeoPoint departurePoint;
    private GeoPoint destinationPoint;
    private Date date;
    private int seatsAvailable = 4;

    public TripModel(GeoPoint departurePoint, GeoPoint destinationPoint, Date date, int seatsAvailable) {
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getSeatsAvailable() {
        return seatsAvailable;
    }

    public void setSeatsAvailable(int seatsAvailable) {
        this.seatsAvailable = seatsAvailable;
    }
}
