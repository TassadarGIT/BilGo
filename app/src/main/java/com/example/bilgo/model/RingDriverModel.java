package com.example.bilgo.model;

public class RingDriverModel {

    private String licensePlate;

    private String ringRoute;

    public RingDriverModel(){

    }

    public RingDriverModel(String license, String route){
        this.licensePlate = license;
        this.ringRoute = route;

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
}
