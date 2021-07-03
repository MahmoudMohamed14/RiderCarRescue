package com.example.riderremake.EventBus;

import com.google.android.gms.maps.model.LatLng;

public class SelectPlaceEvent {
    private LatLng origin,destination;
    private String car,adress;
     private int pointer;

    public SelectPlaceEvent(LatLng origin, LatLng destination, String address, String type_car, int pointer) {
        this.origin = origin;
        this.destination = destination;
        this.adress=address;
        this.car = car;
        this.pointer = pointer;
    }


    public SelectPlaceEvent(LatLng origin, LatLng destination,String address, int pointer) {
        this.origin = origin;
        this.destination = destination;
        this.adress=address;
        this.pointer = pointer;
    }

    public SelectPlaceEvent(LatLng origin, String car, int pointer) {
        this.origin = origin;
        this.car = car;
        this.pointer = pointer;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
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

    public String getDestinationString() {
        return new StringBuilder()
                .append(destination.latitude)
                .append(",")
                .append(destination.longitude)
                .toString();
    }

    public void setOrigin(LatLng origin) {
        this.origin = origin;
    }

    public String getOriginString() {
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
