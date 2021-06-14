package com.example.riderremake;

import com.firebase.geofire.GeoLocation;

public class DriverGeomodel {
    private String key;
    private GeoLocation geoLocation;
    private DriverInfoModel driverInfoModel;
    private MechanicInfoModel mechanicInfoModel;
    private WinchInfoModel winchInfoModel;
    private boolean isDecline;


    public DriverGeomodel() {

    }

    public DriverGeomodel(String key, GeoLocation geoLocation) {
        this.key = key;
        this.geoLocation = geoLocation;
    }

    public boolean isDecline() {
        return isDecline;
    }

    public void setDecline(boolean decline) {
        isDecline = decline;
    }

    public WinchInfoModel getWinchInfoModel() {
        return winchInfoModel;
    }

    public void setWinchInfoModel(WinchInfoModel winchInfoModel) {
        this.winchInfoModel = winchInfoModel;
    }

    public MechanicInfoModel getMechanicInfoModel() {
        return mechanicInfoModel;
    }

    public void setMechanicInfoModel(MechanicInfoModel mechanicInfoModel) {
        this.mechanicInfoModel = mechanicInfoModel;
    }

    public DriverInfoModel getDriverInfoModel() {
        return driverInfoModel;
    }

    public void setDriverInfoModel(DriverInfoModel driverInfoModel) {
        this.driverInfoModel = driverInfoModel;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public GeoLocation getGeoLocation() {
        return geoLocation;
    }

    public void setGeoLocation(GeoLocation geoLocation) {
        this.geoLocation = geoLocation;
    }
}
