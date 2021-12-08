package com.example.riderremake.remote;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMService {
    @Headers({
            "Content-Type:application/json",
          "Authorization:key=AAAAO4PV65Q:APA91bEyY_9lZS8UDGKrAx9ckSa0TEciGtKRdpDJBjsonTUvzDgeYs9JH3VBLPW271VnocY5Tx78EnzvFi6ASFywstYTXFrc1CXxokLu-9ygs9qcnALqv8iAz4bHzGOvpKJBnyh9yUyg"

    })
    @POST("fcm/send")
    Observable<FCMResponse>sendNotification(@Body FCMSendData body);
}
