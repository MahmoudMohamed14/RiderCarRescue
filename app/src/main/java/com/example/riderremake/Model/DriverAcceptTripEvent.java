package com.example.riderremake.Model;

public class DriverAcceptTripEvent {
    String tripId;

    public DriverAcceptTripEvent(String tripId) {
        this.tripId = tripId;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripKey) {
        this.tripId = tripKey;
    }
}
