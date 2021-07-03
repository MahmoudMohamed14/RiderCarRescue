package com.example.riderremake;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.renderscript.RenderScript;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.riderremake.EventBus.DeclineRequestFromDriver;
import com.example.riderremake.EventBus.SelectPlaceEvent;
import com.example.riderremake.EventBus.ShowNotificationFinishTrip;
import com.example.riderremake.Model.DeclineRequestAndRemoveTripFromDriver;
import com.example.riderremake.Model.DriverAcceptTripEvent;
import com.example.riderremake.Model.DriverCompleteTripEvent;
import com.example.riderremake.Model.TripPlanModel;
import com.example.riderremake.Services.UserUtils;
import com.example.riderremake.remote.IgoogleApi;
import com.example.riderremake.remote.RetrofitClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.JsonObject;
import com.google.maps.android.ui.IconGenerator;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CancellationException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.example.riderremake.R2.attr.drawPath;
import static com.example.riderremake.R2.attr.floatingActionButtonStyle;
import static com.example.riderremake.R2.attr.icon;
import static com.example.riderremake.R2.attr.ttcIndex;

public class RequestDriverActivity extends FragmentActivity implements OnMapReadyCallback {
    //effect
    private Circle lastusercircle;
    private long duration=1000;
    private ValueAnimator lastplusAnimation;
    //slowly camera spinning
    private  ValueAnimator animator;
    private final  static int DESIRED_NUM_OF_SPINS=5;
    private final  static int DESIRED_SECOND_PER_ONE_FULL_360_SPIN=40;
    @BindView(R.id.main_layout)
    RelativeLayout main_layout;
    @BindView(R.id.confirm_uber_layout)
    CardView confirm_uber_layout;
    @BindView(R.id.btn_confirm_uber)
    Button btn_confirm_uber;
    @BindView(R.id.confirm_pickup_layout)
    CardView confirm_pickup_layout;
    @BindView(R.id.finding_your_ride_layout)
    CardView finding_your_ride_layout;
    @BindView(R.id.btn_confirm_pickup)
    Button btn_confirm_pickup;
    @BindView(R.id.txt_address_pickup)
   TextView txt_address_pickup;
    @BindView(R.id.layout_driver_info)
    CardView layout_driver_info;
    @BindView(R.id.txt_driver_name)
    TextView txt_driver_name;
    @BindView(R.id.img_driver)
    ImageView img_driver;
    @BindView(R.id.fill_maps)
    View fill_map;
    private Object Context;
    private DriverGeomodel lastDriverCall;
    private String driverOldPosition="";
    private Handler handler;
    private float v;
    private double lat,lng;
    private int next,index;
    private LatLng start,end;


    @OnClick(R.id.btn_confirm_uber)
    void onConfirmUber(){
        confirm_pickup_layout.setVisibility(View.VISIBLE);//show layout pickup
        confirm_uber_layout.setVisibility(View.GONE);//hide uber layout
        setDatapickup();
    }
    @OnClick(R.id.btn_confirm_pickup)
    void onConfirmPickup(){
        if(mMap==null)return;
        if(selectPlaceEvent==null)return;
        mMap.clear();
        CameraPosition cameraPosition= new CameraPosition.Builder()
                .target(selectPlaceEvent
                        .getOrigin()).tilt(45f).zoom(16).build();
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        //start animation
        addmarkerwihthplusanimation();


    }

    private void addmarkerwihthplusanimation() {
        confirm_uber_layout.setVisibility(View.GONE);
        fill_map.setVisibility(View.VISIBLE);
        confirm_pickup_layout.setVisibility(View.GONE);
        finding_your_ride_layout.setVisibility(View.VISIBLE);
        originMarker=mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker()).position(selectPlaceEvent.getOrigin()));
        addplussatingEffect(selectPlaceEvent);
    }

    private void addplussatingEffect(SelectPlaceEvent selectPlaceEvent) {
        if(lastplusAnimation!=null)lastplusAnimation.cancel();
        if(lastusercircle!=null)lastusercircle.setCenter(selectPlaceEvent.getOrigin());
        lastplusAnimation= Common.valueAnimate(duration, animation->{
            if(lastusercircle!=null)lastusercircle.setRadius((float)animation.getAnimatedValue());
            else{
                lastusercircle=mMap.addCircle(new CircleOptions().center(selectPlaceEvent.getOrigin()).radius((float)animation.getAnimatedValue())
                .strokeColor(Color.WHITE).fillColor(Color.parseColor("#33333333")));
            }

        });
        startMapCameraSpinningAinmation(selectPlaceEvent);
    }

    private void startMapCameraSpinningAinmation(SelectPlaceEvent selectPlaceEvent) {
        if(animator!=null)animator.cancel();
        animator=ValueAnimator.ofFloat(0,DESIRED_NUM_OF_SPINS*360);
        animator.setDuration(DESIRED_SECOND_PER_ONE_FULL_360_SPIN*DESIRED_NUM_OF_SPINS*1000);
        animator.setInterpolator(new LinearInterpolator());
        animator.setStartDelay(100);
        animator.addUpdateListener(ValueAnimator->{
            float newBearingValue=(float)ValueAnimator.getAnimatedValue();
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(selectPlaceEvent.getOrigin())
            .tilt(45f)
            .zoom(16f)
            .bearing(newBearingValue)
            .build()));

        });
        animator.start();
        //after start animation find driver
        findNearbyDriver(selectPlaceEvent);
    }

    private void  findNearbyDriver(SelectPlaceEvent selectPlaceEvent) {
        if(Common.driversFound.size()>0){
            float min_distance=0;//default min distance=0;
            DriverGeomodel foundDriver= null;
            Location currentRiderLocation=new Location("");
            currentRiderLocation.setLatitude(selectPlaceEvent.getOrigin().latitude);
            currentRiderLocation.setLongitude(selectPlaceEvent.getOrigin().longitude);
            for(String key:Common.driversFound.keySet()){
                Location driverLocation=new Location("");
                driverLocation.setLatitude(Common.driversFound.get(key).getGeoLocation().latitude);
                driverLocation.setLongitude(Common.driversFound.get(key).getGeoLocation().longitude);
                //compare 2 location
                if(min_distance==0){
                    min_distance=driverLocation.distanceTo(currentRiderLocation);//first default min_distance
                   if(!Common.driversFound.get(key).isDecline()){//if not decline before
                       foundDriver= Common.driversFound.get(key);
                       break;
                   }else
                       continue;
                }
                else if (driverLocation.distanceTo(currentRiderLocation)<min_distance){
                    //if have any driver smaller min_distance,just get it
                    min_distance=driverLocation.distanceTo(currentRiderLocation);//first default min_distance
                    if(!Common.driversFound.get(key).isDecline()){//if not decline before
                        foundDriver= Common.driversFound.get(key);
                        break;
                    }
                    else
                        continue;
                }


            }

            if(foundDriver!=null){
                UserUtils.sendRequestToDriver(this,main_layout,foundDriver,selectPlaceEvent);

                lastDriverCall=foundDriver;
            }
            else {
                Snackbar.make(main_layout,getString(R.string.no_driver_accept_request),Snackbar.LENGTH_SHORT).show();
                lastDriverCall=null;
                finish();
            }

        }

        else {
            //not found
            Snackbar.make(main_layout,getString(R.string.drivers_not_found),Snackbar.LENGTH_SHORT).show();
            lastDriverCall=null;
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        if(animator!=null)animator.end();
        super.onDestroy();
    }

    TextView tex_origin;
    private GoogleMap mMap;
    private SelectPlaceEvent selectPlaceEvent;
    //routes
    private CompositeDisposable compositeDisposable=new CompositeDisposable();
    private IgoogleApi igoogleApi;
    private Polyline balckpolyline,greypolyline;
    private PolylineOptions polylineOptions,blaclpolylineoption;
    private List<LatLng>polylinelist;

    private Marker originMarker ,destinationMarker;
    private void setDatapickup() {
        if(selectPlaceEvent.getPointer()==1||selectPlaceEvent.getPointer()==2) {
            txt_address_pickup.setText(tex_origin != null ? tex_origin.getText() : "None");
        }else if(selectPlaceEvent.getPointer()==3){
            txt_address_pickup.setText(selectPlaceEvent.getCar()!=null?selectPlaceEvent.getCar():"None");
        }
        mMap.clear();//clear on all map
        addpickermarker();

    }

    private void addpickermarker() {
        View view=getLayoutInflater().inflate(R.layout.pickup_info_windows,null);
        IconGenerator generator=new IconGenerator(this);
        generator.setContentView(view);
        generator.setBackground(new ColorDrawable(Color.TRANSPARENT));
        Bitmap icon=generator.makeIcon();
        originMarker=mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(icon)).position(selectPlaceEvent.getOrigin()));

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(! EventBus.getDefault().isRegistered(this))
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        compositeDisposable.clear();
        super.onStop();

        if(EventBus.getDefault().hasSubscriberForEvent(SelectPlaceEvent.class))
            EventBus.getDefault().removeStickyEvent(SelectPlaceEvent.class);
        if(EventBus.getDefault().hasSubscriberForEvent(DriverCompleteTripEvent.class))
            EventBus.getDefault().removeStickyEvent(DriverCompleteTripEvent.class);
        if(EventBus.getDefault().hasSubscriberForEvent(DeclineRequestFromDriver.class))
            EventBus.getDefault().removeStickyEvent(DeclineRequestFromDriver.class);
        if(EventBus.getDefault().hasSubscriberForEvent(DeclineRequestAndRemoveTripFromDriver.class))
            EventBus.getDefault().removeStickyEvent(DeclineRequestAndRemoveTripFromDriver.class);
        if(EventBus.getDefault().hasSubscriberForEvent(DriverAcceptTripEvent.class))
            EventBus.getDefault().removeStickyEvent(DriverAcceptTripEvent.class);

        EventBus.getDefault().unregister(this);


    }
    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public  void onDriverCompleteTripEvent(DriverCompleteTripEvent event){


        Common.ShowNotfication(this, new Random().nextInt(),
                "Complete Trip",
                "you trip"+event.getKeytrip()+"has been complete", null);
        finish();
    }
    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public  void onDriverAcceptEvent(DriverAcceptTripEvent event)
    {
        FirebaseDatabase.getInstance().getReference(Common.Trips).child(event.getTripId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    TripPlanModel tripPlanModel=snapshot.getValue(TripPlanModel.class);
                    mMap.clear();
                    fill_map.setVisibility(View.GONE);
                    if(animator!=null)animator.end();
                    CameraPosition cameraPosition=new CameraPosition.Builder()
                            .target(mMap.getCameraPosition().target)
                            .tilt(0f)
                            .zoom(mMap.getCameraPosition().zoom)
                            .build();
                    mMap.moveCamera( CameraUpdateFactory.newCameraPosition(cameraPosition));
                    //get route
                    String driverLocation= new StringBuilder()
                            .append(tripPlanModel.getCurrentLat())
                            .append(",")
                            .append(tripPlanModel.getCurrentLog())
                            .toString();
                    // Request Api
                    compositeDisposable.add(igoogleApi.getDirections("driving","less_driving"
                            ,tripPlanModel.getOrigin()
                            ,driverLocation
                            ,getString(R.string.google_api_key))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<String>() {
                                @Override
                                public void accept(String returnRusult) throws Exception {
                                  PolylineOptions blackpolylineOptions=null;
                                  List<LatLng>polylinelist=null;
                                 Polyline blackpolyline=null;

                                    try {
                                        JSONObject jsonObject= new JSONObject(returnRusult);
                                        JSONArray jsonArray=jsonObject.getJSONArray("routes");
                                        for(int i=0 ;i<jsonArray.length();i++){
                                            JSONObject route=jsonArray.getJSONObject(i);
                                            JSONObject poly=route.getJSONObject("overview_polyline");
                                            String polyline=poly.getString("points");
                                            polylinelist=Common.decodePoly(polyline);

                                        }
                                        //moving


                                        blaclpolylineoption=new PolylineOptions();
                                        blaclpolylineoption.color(Color.BLACK);
                                        blaclpolylineoption.width(5);
                                        blaclpolylineoption.startCap(new SquareCap());
                                        blaclpolylineoption.jointType(JointType.ROUND);
                                        blaclpolylineoption.addAll(polylinelist);
                                        balckpolyline=mMap.addPolyline( blaclpolylineoption);
                                        //animator
                                        JSONObject object=jsonArray.getJSONObject(0);
                                        JSONArray legs=object.getJSONArray("legs");
                                        JSONObject legsobject=legs.getJSONObject(0);
                                        JSONObject time=legsobject.getJSONObject("duration");
                                        String duration=time.getString("text");
                                        JSONObject distanceEstimate =legsobject.getJSONObject("distance");
                                        String distance=distanceEstimate.getString("text");
                                        LatLng origin=new LatLng(Double.parseDouble(tripPlanModel.getOrigin().split(",")[0])
                                                ,Double.parseDouble(tripPlanModel.getOrigin().split(",")[1]));
                                        LatLng destination=new LatLng(tripPlanModel.getCurrentLat(),tripPlanModel.getCurrentLog());
                                        LatLngBounds latLngBounds=new LatLngBounds.Builder()
                                                .include(origin)
                                                .include(destination)
                                                .build();
                                        //add car icon for origin
                                        addPickerMarkerWithDuration(duration,origin);
                                       addDriverMarker(destination);




                                        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds,160));
                                        mMap.moveCamera(CameraUpdateFactory.zoomTo(  mMap.getCameraPosition().zoom-1));
                                      initDriverFromMoving(event.getTripId(),tripPlanModel);


             //      if(!tripPlanModel.getDriverInfo().getImage().equals("default")) {
//                        Glide.with(RequestDriverActivity.this).load(tripPlanModel.getDriverInfo().getImage()).into(img_driver);
//                    }
                                        // txt_driver_name.setText(tripPlanModel.getDriverInfo().getName());
                                        confirm_pickup_layout.setVisibility(View.GONE);
                                        confirm_uber_layout.setVisibility(View.GONE);
                                        layout_driver_info.setVisibility(View.VISIBLE);




                                    } catch (Exception e){
                                        Toast.makeText(RequestDriverActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();



                                    }

                                }


                            }));



                }else{
                    Snackbar.make(main_layout,getString(R.string.trip_not_found),Snackbar.LENGTH_LONG).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Snackbar.make(main_layout,error.getMessage(),Snackbar.LENGTH_LONG).show();

            }
        });



    }

    private void initDriverFromMoving(String tripId, TripPlanModel tripPlanModel) {
      driverOldPosition= new StringBuilder()
                .append(tripPlanModel.getCurrentLat())
                .append(",")
                .append(tripPlanModel.getCurrentLog())
                .toString();
      FirebaseDatabase.getInstance().getReference(Common.Trips).child(tripId).addListenerForSingleValueEvent(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot snapshot) {
              if(snapshot.exists()) {
                  TripPlanModel newData = snapshot.getValue(TripPlanModel.class);
                  String driverNewLocation = new StringBuilder()
                          .append(newData.getCurrentLat())
                          .append(",")
                          .append(newData.getCurrentLog())
                          .toString();
                  if (!driverOldPosition.equals(driverNewLocation)) {
                      moveMarkerAnimatin(destinationMarker, driverOldPosition, driverNewLocation);
                  }
              }
          }

          @Override
          public void onCancelled(@NonNull DatabaseError error) {
              Snackbar.make(main_layout,error.getMessage(),Snackbar.LENGTH_LONG).show();
          }
      });
    }

    private void moveMarkerAnimatin(Marker marker, String from, String to) {
        compositeDisposable.add(igoogleApi.getDirections("driving","less_driving",from,to,
                getString(R.string.google_api_key))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String returnRusult) throws Exception {
                        Log.d("API_RESULT",returnRusult);

                        try {
                            JSONObject jsonObject= new JSONObject(returnRusult);
                            JSONArray jsonArray=jsonObject.getJSONArray("routes");
                            for(int i=0 ;i<jsonArray.length();i++){
                                JSONObject route=jsonArray.getJSONObject(i);
                                JSONObject poly=route.getJSONObject("overview_polyline");
                                String polyline=poly.getString("points");
                                 polylinelist=Common.decodePoly(polyline);

                            }
                            //moving
                            blaclpolylineoption=new PolylineOptions();
                            blaclpolylineoption.color(Color.BLACK);
                            blaclpolylineoption.width(5);
                            blaclpolylineoption.startCap(new SquareCap());
                            blaclpolylineoption.jointType(JointType.ROUND);
                            blaclpolylineoption.addAll(polylinelist);
                            balckpolyline=mMap.addPolyline( blaclpolylineoption);
                            //animator
                            JSONObject object=jsonArray.getJSONObject(0);
                            JSONArray legs=object.getJSONArray("legs");
                            JSONObject legsobject=legs.getJSONObject(0);
                            JSONObject time=legsobject.getJSONObject("duration");
                            String duration=time.getString("text");
                            JSONObject distanceEstimate =legsobject.getJSONObject("distance");
                            String distance=distanceEstimate.getString("text");

                         Bitmap bitmap= Common.createIconWithDuration(RequestDriverActivity.this,duration);
                         originMarker.setIcon(BitmapDescriptorFactory.fromBitmap(bitmap));
                           handler= new Handler();
                            //run handler
                          next=1;
                          index=-1;
                          handler.postDelayed(new Runnable() {
                              @Override
                              public void run() {
                                  if(index<polylinelist.size()-2){
                                      index++;
                                      next=index+1;
                                      start=polylinelist.get(index);
                                      end=polylinelist.get(next);
                                  }
                                  ValueAnimator valueAnimator = ValueAnimator.ofInt(0, 100);
                                  valueAnimator.setDuration(1100);

                                  valueAnimator.setInterpolator(new LinearInterpolator());
                                  valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                      @Override
                                      public void onAnimationUpdate(ValueAnimator animation) {
                                          v=animation.getAnimatedFraction();
                                          lat=v*end.longitude+(1-v)*start.longitude;
                                          lng=v*end.latitude+(1-v)*start.latitude;
                                          LatLng newPos=new LatLng(lat,lng);
                                          marker.setPosition(newPos);
                                          marker.setAnchor(0.5f,.5f);
                                          marker.setRotation(Common.getBearing(start,newPos));
                                          mMap.moveCamera(CameraUpdateFactory.newLatLng(newPos));


                                      }
                                  });
                                  valueAnimator.start();
                                  if(index<polylinelist.size()-2){
                                      handler.postDelayed(this,1500);

                                  }
                                  else if(index<polylinelist.size()-1){

                                  }
                              }
                          },1500);
                          driverOldPosition=to;//set driver new position


                        } catch (Exception e){
                            Snackbar.make(main_layout, e.getMessage(), Snackbar.LENGTH_SHORT).show();



                        }

                    }
                },throwable -> {
                    if(throwable!=null){
                        Snackbar.make(main_layout, throwable.getMessage(), Snackbar.LENGTH_SHORT).show();
                    }
                }));
    }

    private void addDriverMarker(LatLng destination) {
        destinationMarker=mMap.addMarker(new MarkerOptions().position(destination).flat(true).icon(BitmapDescriptorFactory.fromResource(R.drawable.car)));
    }

    private void addPickerMarkerWithDuration(String duration, LatLng origin) {
        Bitmap icon=Common.createIconWithDuration(this,duration);
        originMarker=mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(icon)).position(origin));

    }

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public  void onSelectPlaceEvent(SelectPlaceEvent event){
        selectPlaceEvent=event;
    }
    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public  void onDeclineRequestEvent(DeclineRequestFromDriver event){
        if(lastDriverCall!=null){
            Common.driversFound.get(lastDriverCall.getKey()).setDecline(true);
            //driver cancel request,find new driver
            findNearbyDriver(selectPlaceEvent);
        }

    }
    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public  void onDeclineRequestAndRemoveTripEvent(DeclineRequestAndRemoveTripFromDriver event){
        if(lastDriverCall!=null){
            Common.driversFound.get(lastDriverCall.getKey()).setDecline(true);
            //driver cancel request,finish this action
           finish();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_driver);
        intin();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void intin() {
        ButterKnife.bind(this);
        igoogleApi= RetrofitClient.getInstance().create(IgoogleApi.class);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if(selectPlaceEvent.getPointer()==1||selectPlaceEvent.getPointer()==2) {
            drawPath(selectPlaceEvent);

        }else if(selectPlaceEvent.getPointer()==3){
            confirm_pickup_layout.setVisibility(View.VISIBLE);//show layout pickup
            confirm_uber_layout.setVisibility(View.GONE);//hide uber layout
            setDatapickup();
        }

        try {
            Boolean success=googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this,R.raw.uber_maps_style));
            if(!success){

                Toast.makeText(this, "loading map style failed", Toast.LENGTH_SHORT).show();
            }
        }catch (Resources.NotFoundException e){

            Toast.makeText(this,e.getMessage() , Toast.LENGTH_SHORT).show();
        }
    }

    private void drawPath(SelectPlaceEvent selectPlaceEvent) {
        // Request Api
        compositeDisposable.add(igoogleApi.getDirections("driving","less_driving"
                ,selectPlaceEvent.getOriginString()
                ,selectPlaceEvent.getDestinationString()
                ,getString(R.string.google_api_key))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String returnRusult) throws Exception {
                        Log.d("API_RESULT",returnRusult);

                        try {
                            JSONObject jsonObject= new JSONObject(returnRusult);
                            JSONArray jsonArray=jsonObject.getJSONArray("routes");
                            for(int i=0 ;i<jsonArray.length();i++){
                                JSONObject route=jsonArray.getJSONObject(i);
                                JSONObject poly=route.getJSONObject("overview_polyline");
                                String polyline=poly.getString("points");
                                 polylinelist=Common.decodePoly(polyline);

                            }
                            //moving
                            polylineOptions=new PolylineOptions();
                            polylineOptions.color(Color.GRAY);
                            polylineOptions.width(12);
                            polylineOptions.startCap(new SquareCap());
                            polylineOptions.jointType(JointType.ROUND);
                            polylineOptions.addAll(polylinelist);
                            greypolyline=mMap.addPolyline(polylineOptions);
                            blaclpolylineoption=new PolylineOptions();
                            blaclpolylineoption.color(Color.BLACK);
                            blaclpolylineoption.width(5);
                            blaclpolylineoption.startCap(new SquareCap());
                            blaclpolylineoption.jointType(JointType.ROUND);
                            blaclpolylineoption.addAll(polylinelist);
                            balckpolyline=mMap.addPolyline( blaclpolylineoption);
                            //animator
                            ValueAnimator valueAnimator = ValueAnimator.ofInt(0, 100);
                            valueAnimator.setDuration(1100);
                            valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
                            valueAnimator.setInterpolator(new LinearInterpolator());
                            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator animation) {
                                 List<LatLng>points=greypolyline.getPoints();
                                 int percentvalue=(int)animation.getAnimatedValue();
                                 int size=points.size();
                                 int newsize=(int)(size*(percentvalue/100.0f));
                                 List<LatLng>p=points.subList(0,newsize);
                                    balckpolyline.setPoints(p);

                                }
                            });
                            valueAnimator.start();
                            LatLngBounds latLngBounds=new LatLngBounds.Builder()
                                    .include(selectPlaceEvent.getOrigin())
                                    .include(selectPlaceEvent.getDestination())
                                    .build();
                                    //add car icon for origin
                            JSONObject object=jsonArray.getJSONObject(0);
                            JSONArray legs=object.getJSONArray("legs");
                            JSONObject legsobject=legs.getJSONObject(0);
                            JSONObject time=legsobject.getJSONObject("duration");
                            String duration=time.getString("text");
                            String start_address=legsobject.getString("start_address");
                            String end_address=legsobject.getString("end_address");
                            addOriginMarker(duration,start_address);
                            addDestinatinMarker(end_address);
                            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds,160));
                            mMap.moveCamera(CameraUpdateFactory.zoomTo(  mMap.getCameraPosition().zoom-1));




                          

                        } catch (Exception e){
                            Toast.makeText(RequestDriverActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();



                        }

                    }


                }));
    }

    private void addOriginMarker(String duration, String start_address) {
        View view= getLayoutInflater().inflate(R.layout.origin_ifo_windows,null);
        TextView tex_time=(  TextView)view.findViewById(R.id.tex_time);
        tex_origin=(  TextView)view.findViewById(R.id.tex_origin);
        tex_time.setText(Common.formatDuration(duration));
        tex_origin.setText(Common.formatAdress(start_address));
        //create icon from marker
        IconGenerator generator=new IconGenerator(this);
        generator.setContentView(view);
        generator.setBackground(new ColorDrawable(Color.TRANSPARENT));
        Bitmap icon=generator.makeIcon();
        originMarker=mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(icon)).position(selectPlaceEvent.getOrigin()));

    }
    private void addDestinatinMarker(String end_address) {
        View view= getLayoutInflater().inflate(R.layout.destination_ifo_windows,null);
        TextView tex_destination=(  TextView)view.findViewById(R.id.tex_destination);
        tex_destination.setText(Common.formatAdress(end_address));
        IconGenerator generator=new IconGenerator(RequestDriverActivity.this);
        generator.setContentView(view);
        generator.setBackground(new ColorDrawable(Color.TRANSPARENT));
        Bitmap icon=generator.makeIcon();
        destinationMarker=mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(icon)).position(selectPlaceEvent.getDestination()));
    }
}