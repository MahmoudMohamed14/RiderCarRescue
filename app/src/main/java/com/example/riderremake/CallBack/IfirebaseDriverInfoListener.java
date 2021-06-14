package com.example.riderremake.CallBack;

import com.example.riderremake.DriverGeomodel;

public interface IfirebaseDriverInfoListener {
    void onDriverInfoLoadSuccess(DriverGeomodel driverGeomodel);
    void onMechanicInfoLoadSuccess(DriverGeomodel driverGeomodel);
    void onWnchInfoLoadSuccess(DriverGeomodel driverGeomodel);
}
