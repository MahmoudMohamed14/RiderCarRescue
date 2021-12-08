package com.example.riderremake.Model;

import com.example.riderremake.Model.DriverInfo;
import com.example.riderremake.Model.MechanicInfoModel;
import com.example.riderremake.Model.WinchInfoModel;
import com.firebase.geofire.GeoLocation;

public class DriverGeomodel {
    private String key;
    private GeoLocation geoLocation;
    private DriverInfo driverInfo;
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

    public DriverInfo getDriverInfo() {
        return driverInfo;
    }

    public void setDriverInfo(DriverInfo driverInfo) {
        this.driverInfo = driverInfo;
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
