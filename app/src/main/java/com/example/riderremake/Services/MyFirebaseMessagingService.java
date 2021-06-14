package com.example.riderremake.Services;

import androidx.annotation.NonNull;

import com.example.riderremake.Common;
import com.example.riderremake.EventBus.DeclineRequestFromDriver;
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

                }else
                Common.ShowNotfication(this, new Random().nextInt(),
                        dataReceive.get(Common.NOTI_TITLE),
                        dataReceive.get(Common.NOTI_CONTANT), null);
            }


        }
    }
}
