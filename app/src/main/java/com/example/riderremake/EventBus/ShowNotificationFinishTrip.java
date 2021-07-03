package com.example.riderremake.EventBus;

public class ShowNotificationFinishTrip {
    String keytrip;
    public ShowNotificationFinishTrip(String keytrip) {
        this.keytrip = keytrip;
    }

    public String getKeytrip() {
        return keytrip;
    }

    public void setKeytrip(String keytrip) {
        this.keytrip = keytrip;
    }
}
