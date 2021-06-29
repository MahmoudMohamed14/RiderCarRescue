package com.example.riderremake.EventBus;

import com.google.android.gms.maps.model.LatLng;

public class SelectPlaceEvent {
    private LatLng origin,destination;
    private String car;
     private int pointer;

    public SelectPlaceEvent() {
    }


    public SelectPlaceEvent(LatLng origin, LatLng destination, int pointer) {
        this.origin = origin;
        this.destination = destination;
        this.pointer = pointer;
    }

    public SelectPlaceEvent(LatLng origin, String car, int pointer) {
        this.origin = origin;
        this.car = car;
        this.pointer = pointer;
    }

    public SelectPlaceEvent(LatLng origin, LatLng destination, String car, int pointer) {
        this.origin = origin;
        this.destination = destination;
        this.car = car;
        this.pointer = pointer;
    }

    public int getPointer() {
        return pointer;
    }

    public void setPointer(int pointer) {
        this.pointer = pointer;
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
