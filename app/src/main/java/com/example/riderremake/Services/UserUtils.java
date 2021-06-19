package com.example.riderremake.Services;

import android.content.Context;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.riderremake.Common;
import com.example.riderremake.DriverGeomodel;
import com.example.riderremake.EventBus.SelectPlaceEvent;
import com.example.riderremake.FCMResponse;
import com.example.riderremake.R;
import com.example.riderremake.RequestDriverActivity;
import com.example.riderremake.remote.FCMSendData;
import com.example.riderremake.remote.IFCMService;
import com.example.riderremake.remote.RetrofitFCMClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class UserUtils {
    public static void updateToken(final Context context, String token) {
        TokenModel tokenModel=new TokenModel(token);
        FirebaseDatabase.getInstance()
                .getReference(Common.TOKEN_REFRANCE)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .setValue(tokenModel)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });
    }

    public static void sendRequestToDriver(Context context, RelativeLayout main_layout, DriverGeomodel foundDriver, LatLng target) {
        CompositeDisposable compositeDisposable=new CompositeDisposable();
        IFCMService ifcmService= RetrofitFCMClient.getInstance().create(IFCMService.class);
        //get token
        SelectPlaceEvent selectPlaceEvent=new SelectPlaceEvent();
        FirebaseDatabase.getInstance().getReference(Common.TOKEN_REFRANCE).child(foundDriver.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    TokenModel tokenModel=snapshot.getValue( TokenModel.class);
                    Map<String,String> notification=new HashMap<>();
                    notification.put(Common.NOTI_TITLE,Common.REQUEST_DRIVER_TITLE);
                    notification.put(Common.NOTI_CONTANT,"This message respersent for request driver action ");
                    notification.put(Common.RiDER_KEY,FirebaseAuth.getInstance().getCurrentUser().getUid());
                    notification.put("TYPE_CAR",selectPlaceEvent.getCar());
                    notification.put(Common.RIDER_PICKUP_LOCATION,new StringBuilder("").append(target.latitude)
                    .append(",").append(target.longitude).toString());
                    FCMSendData fcmSendData=new FCMSendData(tokenModel.getToken(),notification);
                    compositeDisposable.add(ifcmService.sendNotification(fcmSendData)
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<FCMResponse>() {
                        @Override
                        public void accept(FCMResponse fcmResponse) throws Exception {
                            if(fcmResponse.getSuccess()==0){
                                compositeDisposable.clear();

                                Snackbar.make(main_layout,context.getString(R.string.request_driver_failed),Snackbar.LENGTH_LONG).show();


                            }

                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            compositeDisposable.clear();
                            Snackbar.make(main_layout,throwable.getMessage()+"here 88",Snackbar.LENGTH_LONG).show();

                        }
                    }));
                }
                else {
                    Snackbar.make(main_layout,context.getString(R.string.token_not_found),Snackbar.LENGTH_LONG).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Snackbar.make(main_layout,error.getMessage(),Snackbar.LENGTH_LONG).show();

            }
        });
    }
}
