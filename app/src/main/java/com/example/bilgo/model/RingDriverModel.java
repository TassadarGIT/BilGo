package com.example.bilgo.model;

public class RingDriverModel {

    private String licensePlate;

    private String ringRoute;

    private double longitude;
    private double latitude;
    private long timestamp;


    public RingDriverModel(){

    }

    public RingDriverModel(String license, String route, double latitude, double longitude, long timestamp){
        this.licensePlate = license;
        this.ringRoute = route;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;

    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getRingRoute() {
        return ringRoute;
    }

    public void setRingRoute(String ringRoute) {
        this.ringRoute = ringRoute;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
