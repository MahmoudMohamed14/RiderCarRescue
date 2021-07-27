package com.example.riderremake.Model;

public class DeclineRequestAndRemoveTripFromDriver {
    String driverKey;

    public DeclineRequestAndRemoveTripFromDriver(String driverKey) {
        this.driverKey = driverKey;
    }

    public String getDriverKey() {
        return driverKey;
    }

    public void setDriverKey(String driverKey) {
        this.driverKey = driverKey;
    }
}
