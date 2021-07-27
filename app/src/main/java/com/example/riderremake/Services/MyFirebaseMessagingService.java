package com.example.riderremake.Services;

import androidx.annotation.NonNull;

import com.example.riderremake.Common;
import com.example.riderremake.EventBus.DeclineRequestFromDriver;
import com.example.riderremake.Model.DeclineRequestAndRemoveTripFromDriver;
import com.example.riderremake.Model.DriverAcceptTripEvent;
import com.example.riderremake.Model.DriverCompleteTripEvent;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;
import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            UserUtils.updateToken(this,s);
        }
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Map<String,String> dataReceive=remoteMessage.getData();
        if(dataReceive!=null){
            if(dataReceive.get(Common.NOTI_TITLE)!=null) {

                if(dataReceive.get(Common.NOTI_TITLE).equals(Common.REQUEST_DRIVER_DECLINE)){
                    EventBus.getDefault().postSticky(new DeclineRequestFromDriver());

                }

                else if(dataReceive.get(Common.NOTI_TITLE).equals(Common.REQUEST_DRIVER_ACCEPT)){
                    String tripkey=dataReceive.get(Common.TRIP_KEY);
                    EventBus.getDefault().postSticky(new DriverAcceptTripEvent(tripkey));

                }
                else if(dataReceive.get(Common.NOTI_TITLE).equals(Common.REQUEST_DRIVER_DECLINE_AND_REMOVE_TRIP)){
                    String driverkey=dataReceive.get(Common.DRIVER_KEY);
                    EventBus.getDefault().postSticky(new DeclineRequestAndRemoveTripFromDriver(driverkey));

                }
                else if(dataReceive.get(Common.NOTI_TITLE).equals(Common.RIDER_COMPLETE_TRIP)){
                    String tripkey=dataReceive.get(Common.TRIP_KEY);
                    EventBus.getDefault().postSticky(new DriverCompleteTripEvent(tripkey));

                }

                else
                Common.ShowNotfication(this, new Random().nextInt(),
                        dataReceive.get(Common.NOTI_TITLE),
                        dataReceive.get(Common.NOTI_CONTANT), null);
            }


        }
    }
}
