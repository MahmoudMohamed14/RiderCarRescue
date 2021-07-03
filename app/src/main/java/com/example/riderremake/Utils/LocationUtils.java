package com.example.riderremake.Utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.text.TextUtils;

import java.util.List;
import java.util.Locale;

public class LocationUtils{
    public static String getAddressFromLocation(Context context, Location location){
        StringBuilder result= new StringBuilder();
        Geocoder geocoder=new Geocoder(context, Locale.getDefault());
        List<Address> addressList;
        try{
            addressList=geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            if(addressList!=null&&addressList.size()>0){
                if(addressList.get(0).getLocality()!=null&&!TextUtils.isEmpty(addressList.get(0).getLocality())){
                    result.append(addressList.get(0).getLocality());
                }else if(addressList.get(0).getSubAdminArea()!=null&&!TextUtils.isEmpty(addressList.get(0).getSubAdminArea())){

                    result.append(addressList.get(0).getSubAdminArea());
                }else if(addressList.get(0).getAdminArea()!=null&&!TextUtils.isEmpty(addressList.get(0).getAdminArea())){

                    result.append(addressList.get(0).getAdminArea());
                }else {
                    result.append(addressList.get(0).getCountryName());
                }
                result.append("_").append(addressList.get(0).getCountryCode());

            }
            return result.toString();

        } catch (Exception e) {
            return result.toString();
        }

    }
}
