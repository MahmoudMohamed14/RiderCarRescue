package com.example.riderremake.EventBus;

import com.google.android.gms.maps.model.LatLng;

public class SelectPlaceEvent {
    private LatLng origin,destination;

    private String car;
    public SelectPlaceEvent(LatLng origin, LatLng destination) {
        this.origin = origin;
        this.destination = destination;

    }

    public SelectPlaceEvent(LatLng origin, String car) {
        this.origin = origin;
        this.car = car;
    }

    public String getCar() {
        return car;
    }

    public void setCar(String car) {
        this.car = car;
    }

    public LatLng getOrigin() {
        return origin;
    }

    public LatLng getDestination() {
        return destination;
    }

    public String getOriginString() {
        return new StringBuilder()
                .append(destination.latitude)
                .append(",")
                .append(destination.longitude)
                .toString();
    }

    public void setOrigin(LatLng origin) {
        this.origin = origin;
    }

    public String getDestinationString() {
        return new StringBuilder()
                .append(origin.latitude)
                .append(",")
                .append(origin.longitude)
                .toString();

    }

    public void setDestination(LatLng destnation) {
        this.destination = destnation;
    }
}
