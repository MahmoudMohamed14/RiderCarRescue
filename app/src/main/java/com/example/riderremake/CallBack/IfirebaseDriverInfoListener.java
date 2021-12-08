package com.example.riderremake.CallBack;

import com.example.riderremake.Model.DriverGeomodel;

public interface IfirebaseDriverInfoListener {
    void onDriverInfoLoadSuccess(DriverGeomodel driverGeomodel);
    void onMechanicInfoLoadSuccess(DriverGeomodel driverGeomodel);
    void onWnchInfoLoadSuccess(DriverGeomodel driverGeomodel);
}
