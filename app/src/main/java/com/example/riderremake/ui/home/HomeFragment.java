package com.example.riderremake.ui.home;

import android.Manifest;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.riderremake.Model.AnimationModel;
import com.example.riderremake.CallBack.IfirebaseDriverInfoListener;
import com.example.riderremake.CallBack.IfirebaseFialedListener;
import com.example.riderremake.Common;
import com.example.riderremake.Model.DriverGeomodel;
import com.example.riderremake.Model.DriverInfo;
import com.example.riderremake.EventBus.SelectPlaceEvent;
import com.example.riderremake.Model.GeoQueryModel;
import com.example.riderremake.Model.MechanicInfoModel;
import com.example.riderremake.R;
import com.example.riderremake.RequestDriverActivity;
import com.example.riderremake.Utils.LocationUtils;
import com.example.riderremake.Model.WinchInfoModel;
import com.example.riderremake.remote.IgoogleApi;
import com.example.riderremake.remote.RetrofitClient;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class HomeFragment extends Fragment implements OnMapReadyCallback, IfirebaseFialedListener, IfirebaseDriverInfoListener {
private int pointer;
    //search
    @BindView(R.id.activity_main)
    SlidingUpPanelLayout slidingUpPanelLayout;
    @BindView(R.id.txt_welcome)
    TextView txt_welcome;
    @BindView(R.id.main_search)
    LinearLayout main_search;
    @BindView(R.id.pickup_service)
    Button btn_pickup_service;
    @BindView(R.id.image_car)
    ImageView car_service;
    @BindView(R.id.image_mechanic)
    ImageView mechanic_service;
    @BindView(R.id.image_wnsh)
    ImageView winch_service;
    @BindView(R.id.linear_choose_service)
    LinearLayout linear_choose_service;
    @BindView(R.id.layout_choose_saved_place)
    LinearLayout linear_choose_place;
    @BindView(R.id.liner_type_car)
    LinearLayout liner_type_car ;
    @BindView(R.id.edit_type_car)
    EditText edit_type_car ;
    private boolean isnextLaunch=false;
    String type_car;
    @OnClick(R.id.pickup_service)
    void onPickupService() {

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mfusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                LatLng origin = new LatLng(location.getLatitude(), location.getLongitude());
                type_car=edit_type_car.getText().toString();
                if (type_car.isEmpty()) {
                    edit_type_car.setError("you must enter type your car ");
                  edit_type_car.requestFocus();
                    return;
                }
                if(pointer==3){
                    startActivity(new Intent(getContext(), RequestDriverActivity.class));

                    EventBus.getDefault().postSticky(new SelectPlaceEvent(origin, type_car,pointer));

                }


            }
        });
    }
    @OnClick(R.id.image_car)
    void onClickCar(){

        btn_pickup_service.setVisibility(View.GONE);
        main_search.setVisibility(View.VISIBLE);
        linear_choose_service.setVisibility(View.GONE);
        linear_choose_place.setVisibility(View.VISIBLE);
        txt_welcome.setVisibility(View.VISIBLE);
        buildLocationRequest();
        buildLocationCallbackDrivers();
        updateLocation();
        loadAvailableDrivers();
        pointer=1;
    }
    @OnClick(R.id.image_mechanic)
    void onClickMechanic(){
        buildLocationRequest();
        buildLocationCallbackMecanic();
        updateLocation();
        loadAvailableMechanic();
        main_search.setVisibility(View.GONE);
        btn_pickup_service.setVisibility(View.VISIBLE);
        linear_choose_service.setVisibility(View.GONE);
        linear_choose_place.setVisibility(View.GONE);
        txt_welcome.setVisibility(View.VISIBLE);
        liner_type_car.setVisibility(View.VISIBLE);
        pointer=3;

    }
    @OnClick(R.id.image_wnsh)
    void onClickWinch(){
        btn_pickup_service.setVisibility(View.GONE);
        main_search.setVisibility(View.VISIBLE);
        linear_choose_service.setVisibility(View.GONE);
        linear_choose_place.setVisibility(View.GONE);
        txt_welcome.setVisibility(View.VISIBLE);
        liner_type_car.setVisibility(View.VISIBLE);
        buildLocationRequest();
        buildLocationCallbackWinch();
        updateLocation();
        loadAvailableWinch();
        pointer=2;
    }


    private AutocompleteSupportFragment autocompleteSupportFragment;
    private HomeViewModel homeViewModel;
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    //location
    private FusedLocationProviderClient mfusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback callback;
    //load driver
    private double distance = 1.0;
    private static final double LIMIT_RANGE = 20.0;//km
    private Location previousLocation, currentLocation;// use to calculate distance
    private boolean fristtime = true;
    //listener
    IfirebaseDriverInfoListener ifirebaseDriverInfoListener;
    IfirebaseFialedListener ifirebaseFialedListener;
    String cityName;
    //move car
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private IgoogleApi igoogleApi;




    @Override
    public void onStop() {
        compositeDisposable.clear();

        super.onStop();
    }




    @Override
    public void onResume() {
        super.onResume();
        if(isnextLaunch){
            if(pointer==1){
                loadAvailableDrivers();

            }
            else if(pointer==2){
                loadAvailableWinch();
            }else if(pointer==3){
                loadAvailableMechanic();
            }
        }else{
            isnextLaunch=true;
        }
    }

    @Override
    public void onDestroy() {
        if(callback!=null)
        mfusedLocationProviderClient.removeLocationUpdates(callback);
        super.onDestroy();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);


        mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        initView(root);
       init();

        return root;
    }

    private void initView(View root) {
        ButterKnife.bind(this, root);
        Common.setWelcomeMessage(txt_welcome);
    }

    private void init() {

//search

        Places.initialize(getContext(), getString(R.string.google_maps_key));
        autocompleteSupportFragment = (AutocompleteSupportFragment) getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);


        autocompleteSupportFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.ADDRESS,//Place.Field.NAME,
                Place.Field.LAT_LNG));
        autocompleteSupportFragment.setHint(getString(R.string.where_to));

        autocompleteSupportFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                // Snackbar.make(getView(),"lam here"+place.getLatLng()+" ",Snackbar.LENGTH_LONG).show();
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    //Snackbar.make(getView(),getString(R.string.pem),Snackbar.LENGTH_LONG).show();
                    return;
                }
                mfusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        LatLng origin =new LatLng(location.getLatitude(),location.getLongitude());
                        LatLng destination =new LatLng(place.getLatLng().latitude,place.getLatLng().longitude);

                        if(pointer==1) {

                            EventBus.getDefault().postSticky(new SelectPlaceEvent(origin, destination,place.getAddress(),pointer));
                            startActivity(new Intent(getContext(), RequestDriverActivity.class));
                        } else if (pointer==2) {
                            type_car=edit_type_car.getText().toString();
                            if (type_car.isEmpty()) {
                                edit_type_car.setError("you must enter type your car ");
                                edit_type_car.requestFocus();
                                return;
                            }


                                EventBus.getDefault().postSticky(new SelectPlaceEvent(origin,destination,place.getAddress(), type_car,pointer));
                            startActivity(new Intent(getContext(), RequestDriverActivity.class));
                        }

                    }
                });
            }

            @Override
            public void onError(@NonNull Status status) {
                Snackbar.make(getView(),status.getStatusMessage()+"line 175 ",Snackbar.LENGTH_LONG).show();
                Log.d("error",status.getStatusMessage());
            }
        });


        igoogleApi= RetrofitClient.getInstance().create(IgoogleApi.class);


        ifirebaseFialedListener = this;
        ifirebaseDriverInfoListener = this;
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Snackbar.make(mapFragment.getView(),getString(R.string.permission_require) , Snackbar.LENGTH_LONG).show();
            return;
        }








    }

    private void updateLocation() {
        mfusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mfusedLocationProviderClient.requestLocationUpdates(locationRequest, callback, Looper.myLooper());

    }

    private void buildLocationCallbackDrivers() {
        if(callback==null){
            callback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    LatLng newpostion = new LatLng(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newpostion, 18f));
                    //if user change location,calculate and load driver again
                    if (fristtime) {
                        previousLocation = currentLocation = locationResult.getLastLocation();
                        fristtime = false;
                        setRestrictplaceInCountry(locationResult.getLastLocation());
                    } else {
                        previousLocation = currentLocation;
                        currentLocation = locationResult.getLastLocation();
                    }
                    if (previousLocation.distanceTo(currentLocation) / 1000 <= LIMIT_RANGE) ;
                    loadAvailableDrivers();


                }
            };

        }
    }
    private void buildLocationCallbackMecanic() {
        if(callback==null){
            callback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    LatLng newpostion = new LatLng(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newpostion, 18f));
                    //if user change location,calculate and load driver again
                    if (fristtime) {
                        previousLocation = currentLocation = locationResult.getLastLocation();
                        fristtime = false;
                        setRestrictplaceInCountry(locationResult.getLastLocation());
                    } else {
                        previousLocation = currentLocation;
                        currentLocation = locationResult.getLastLocation();
                    }
                    if (previousLocation.distanceTo(currentLocation) / 1000 <= LIMIT_RANGE) ;
                    loadAvailableMechanic();


                }
            };

        }
    }
    private void buildLocationCallbackWinch() {
        if(callback==null){
            callback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    LatLng newpostion = new LatLng(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newpostion, 18f));
                    //if user change location,calculate and load driver again
                    if (fristtime) {
                        previousLocation = currentLocation = locationResult.getLastLocation();
                        fristtime = false;

                    } else {
                        previousLocation = currentLocation;
                        currentLocation = locationResult.getLastLocation();
                    }
                    setRestrictplaceInCountry(locationResult.getLastLocation());
                    if (previousLocation.distanceTo(currentLocation) / 1000 <= LIMIT_RANGE) ;
                    loadAvailableWinch();


                }
            };

        }
    }

    private void buildLocationRequest() {
        if(locationRequest==null){
            locationRequest = new LocationRequest();
            locationRequest.setSmallestDisplacement(10f);
            locationRequest.setInterval(5000);
            locationRequest.setFastestInterval(3000);
            locationRequest.setPriority(locationRequest.PRIORITY_HIGH_ACCURACY);

        }
    }

    private void setRestrictplaceInCountry(Location lastLocation)  {

        try {
            Geocoder geocoder=new Geocoder(getContext(),Locale.getDefault());
            List<Address> addressList = geocoder.getFromLocation(lastLocation.getLatitude(),lastLocation.getLongitude(),1);

            if(addressList.size()>0)
                autocompleteSupportFragment.setCountry(addressList.get(0).getCountryCode());
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void loadAvailableDrivers() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Snackbar.make(getView(),getString(R.string.permission_require) , Snackbar.LENGTH_LONG).show();
            return;
        }
        mfusedLocationProviderClient.getLastLocation().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), e.getMessage() + "Eror Homefragment line 131", Toast.LENGTH_SHORT).show();

            }
        }).addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(final Location location) {
                //back all driver in city
                cityName= LocationUtils.getAddressFromLocation(getContext(),location);


                    if(!TextUtils.isEmpty(cityName)) {
                        //query
                        DatabaseReference driverlocationref = FirebaseDatabase.getInstance().getReference(Common.DRIVER_LOCATION_REFERANCE)
                                .child(cityName);
                        GeoFire geoFire = new GeoFire(driverlocationref);
                        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(location.getLatitude(),
                                location.getLongitude()), distance);

                        geoQuery.removeAllListeners();
                        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                            @Override
                            public void onKeyEntered(String key, GeoLocation location) {
                               // Common.driversFound.add(new DriverGeomodel(key, location));
                                if(!Common.driversFound.containsKey(key))
                                    Common.driversFound.put(key,new DriverGeomodel(key,location));//add if not exit
                            }

                            @Override
                            public void onKeyExited(String key) {

                            }

                            @Override
                            public void onKeyMoved(String key, GeoLocation location) {

                            }

                            @Override
                            public void onGeoQueryReady() {
                                if (distance <= LIMIT_RANGE) {
                                    distance++;
                                    loadAvailableDrivers();//continue search in new distanse
                                } else {
                                    distance = 1.0;//reset it
                                    addDriverMarker();
                                }
                            }

                            @Override
                            public void onGeoQueryError(DatabaseError error) {
                                Toast.makeText(getContext(), "Error in homefragment line 191 (query)", Toast.LENGTH_SHORT).show();
                            }
                        });
                        driverlocationref.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                GeoQueryModel geoQueryModel = snapshot.getValue(GeoQueryModel.class);
                                GeoLocation geoLocation = new GeoLocation(geoQueryModel.getL().get(0), geoQueryModel.getL().get(1));
                                DriverGeomodel driverGeomodel = new DriverGeomodel(snapshot.getKey(), geoLocation);
                                Location newDriverlocation = new Location("");
                                newDriverlocation.setLatitude(geoLocation.latitude);
                                newDriverlocation.setLongitude(geoLocation.longitude);
                                Float newdistance = location.distanceTo(newDriverlocation) / 1000;
                                if (newdistance <= LIMIT_RANGE)
                                    findDriverById(driverGeomodel);
                            }

                            @Override
                            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                            }

                            @Override
                            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                            }

                            @Override
                            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                    else
                        Snackbar.make(getView(),"name city is empty",Snackbar.LENGTH_LONG).show();


            }
        });
    }
    private void loadAvailableMechanic() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Snackbar.make(getView(),getString(R.string.permission_require) , Snackbar.LENGTH_LONG).show();
            return;
        }
        mfusedLocationProviderClient.getLastLocation().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), e.getMessage() + "Eror Homefragment line 131", Toast.LENGTH_SHORT).show();

            }
        }).addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(final Location location) {
                //back all driver in city
                cityName= LocationUtils.getAddressFromLocation(getContext(),location);


                    if(!TextUtils.isEmpty(cityName)) {
                        //query
                        DatabaseReference driverlocationref = FirebaseDatabase.getInstance().getReference("MechanicLocation")
                                .child(cityName);
                        GeoFire geoFire = new GeoFire(driverlocationref);
                        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(location.getLatitude(),
                                location.getLongitude()), distance);

                        geoQuery.removeAllListeners();
                        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                            @Override
                            public void onKeyEntered(String key, GeoLocation location) {
                                // Common.driversFound.add(new DriverGeomodel(key, location));
                                if(!Common.driversFound.containsKey(key))
                                    Common.driversFound.put(key,new DriverGeomodel(key,location));//add if not exit
                            }

                            @Override
                            public void onKeyExited(String key) {

                            }

                            @Override
                            public void onKeyMoved(String key, GeoLocation location) {

                            }

                            @Override
                            public void onGeoQueryReady() {
                                if (distance <= LIMIT_RANGE) {
                                    distance++;
                                    loadAvailableMechanic();//continue search in new distanse
                                } else {
                                    distance = 1.0;//reset it
                                    addMecanicMarker();
                                }
                            }

                            @Override
                            public void onGeoQueryError(DatabaseError error) {
                                Toast.makeText(getContext(), "Error in homefragment line 191 (query)", Toast.LENGTH_SHORT).show();
                            }
                        });
                        driverlocationref.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                GeoQueryModel geoQueryModel = snapshot.getValue(GeoQueryModel.class);
                                GeoLocation geoLocation = new GeoLocation(geoQueryModel.getL().get(0), geoQueryModel.getL().get(1));
                                DriverGeomodel driverGeomodel = new DriverGeomodel(snapshot.getKey(), geoLocation);
                                Location newDriverlocation = new Location("");
                                newDriverlocation.setLatitude(geoLocation.latitude);
                                newDriverlocation.setLongitude(geoLocation.longitude);
                                Float newdistance = location.distanceTo(newDriverlocation) / 1000;
                                if (newdistance <= LIMIT_RANGE)
                                    findMechanicById(driverGeomodel);
                            }

                            @Override
                            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                            }

                            @Override
                            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                            }

                            @Override
                            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                    else
                        Snackbar.make(getView(),"name city is empty",Snackbar.LENGTH_LONG).show();


            }
        });
    }
    private void loadAvailableWinch() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Snackbar.make(getView(),getString(R.string.permission_require) , Snackbar.LENGTH_LONG).show();
            return;
        }
        mfusedLocationProviderClient.getLastLocation().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), e.getMessage() + "Eror Homefragment line 131", Toast.LENGTH_SHORT).show();

            }
        }).addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(final Location location) {
                //back all driver in city
                cityName= LocationUtils.getAddressFromLocation(getContext(),location);
                    if(!TextUtils.isEmpty(cityName)) {
                        //query
                        DatabaseReference driverlocationref = FirebaseDatabase.getInstance().getReference("RescueWinchLocation")
                                .child(cityName);
                        GeoFire geoFire = new GeoFire(driverlocationref);
                        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(location.getLatitude(),
                                location.getLongitude()), distance);

                        geoQuery.removeAllListeners();
                        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                            @Override
                            public void onKeyEntered(String key, GeoLocation location) {
                                // Common.driversFound.add(new DriverGeomodel(key, location));
                                if (!Common.driversFound.containsKey(key))
                                    Common.driversFound.put(key, new DriverGeomodel(key, location));//add if not exit
                            }

                            @Override
                            public void onKeyExited(String key) {

                            }

                            @Override
                            public void onKeyMoved(String key, GeoLocation location) {

                            }

                            @Override
                            public void onGeoQueryReady() {
                                if (distance <= LIMIT_RANGE) {
                                    distance++;
                                    loadAvailableWinch();//continue search in new distanse
                                } else {
                                    distance = 1.0;//reset it
                                    addWinchMarker();
                                }
                            }

                            @Override
                            public void onGeoQueryError(DatabaseError error) {
                                Toast.makeText(getContext(), "Error in homefragment line 191 (query)", Toast.LENGTH_SHORT).show();
                            }
                        });
                        driverlocationref.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                GeoQueryModel geoQueryModel = snapshot.getValue(GeoQueryModel.class);
                                GeoLocation geoLocation = new GeoLocation(geoQueryModel.getL().get(0), geoQueryModel.getL().get(1));
                                DriverGeomodel driverGeomodel = new DriverGeomodel(snapshot.getKey(), geoLocation);
                                Location newDriverlocation = new Location("");
                                newDriverlocation.setLatitude(geoLocation.latitude);
                                newDriverlocation.setLongitude(geoLocation.longitude);
                                Float newdistance = location.distanceTo(newDriverlocation) / 1000;
                                if (newdistance <= LIMIT_RANGE)
                                    findWinchById(driverGeomodel);
                            }

                            @Override
                            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                            }

                            @Override
                            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                            }

                            @Override
                            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                    else
                        Snackbar.make(getView(),"name city is empty",Snackbar.LENGTH_LONG).show();


            }
        });
    }

    private void addDriverMarker() {
        if(Common.driversFound.size()>0){
            Observable.fromIterable(Common.driversFound.keySet()).subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(key-> {
                            findDriverById(Common.driversFound.get(key));

                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable Throwable) throws Exception {
                            Toast.makeText(getContext(), Throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }, new Action() {
                        @Override
                        public void run() throws Exception {
                        }
                    });


        }else
            Toast.makeText(getContext(),  "Drivers not Found", Toast.LENGTH_SHORT).show();
    }
    private void addMecanicMarker() {
        if(Common.driversFound.size()>0){
            Observable.fromIterable(Common.driversFound.keySet()).subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(key-> {
                        findMechanicById(Common.driversFound.get(key));

                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable Throwable) throws Exception {
                            Toast.makeText(getContext(), Throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }, new Action() {
                        @Override
                        public void run() throws Exception {
                        }
                    });


        }else
            Toast.makeText(getContext(),  "Drivers not Found", Toast.LENGTH_SHORT).show();
    }
    private void addWinchMarker() {
        if(Common.driversFound.size()>0){
            Observable.fromIterable(Common.driversFound.keySet()).subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(key-> {
                        findWinchById(Common.driversFound.get(key));

                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable Throwable) throws Exception {
                            Toast.makeText(getContext(), Throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }, new Action() {
                        @Override
                        public void run() throws Exception {
                        }
                    });


        }else
            Toast.makeText(getContext(),  "Drivers not Found", Toast.LENGTH_SHORT).show();
    }

    private void findDriverById(final DriverGeomodel driverGeomodel) {
        FirebaseDatabase.getInstance().getReference(Common.DRIVER__INFO)
                .child(driverGeomodel.getKey())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.hasChildren()){
                            driverGeomodel.setDriverInfo(snapshot.getValue(DriverInfo.class));


                            Common.driversFound.get(driverGeomodel.getKey()).setDriverInfo(snapshot.getValue(DriverInfo.class));
                            ifirebaseDriverInfoListener.onDriverInfoLoadSuccess(driverGeomodel);
                        }else
                            ifirebaseFialedListener.onFirebaseLoadFialed("not find driver key");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
ifirebaseFialedListener.onFirebaseLoadFialed(error.getMessage());
                    }
                });
    }
    private void findMechanicById(final DriverGeomodel driverGeomodel) {
        FirebaseDatabase.getInstance().getReference(Common.DRIVER__INFO)
                .child(driverGeomodel.getKey())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.hasChildren()){
                            driverGeomodel.setMechanicInfoModel(snapshot.getValue(MechanicInfoModel.class));


                            Common.driversFound.get(driverGeomodel.getKey()).setMechanicInfoModel(snapshot.getValue(MechanicInfoModel.class));
                            ifirebaseDriverInfoListener.onMechanicInfoLoadSuccess(driverGeomodel);
                        }else
                            ifirebaseFialedListener.onFirebaseLoadFialed("not find driver key");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        ifirebaseFialedListener.onFirebaseLoadFialed(error.getMessage());
                    }
                });
    }
    private void findWinchById(final DriverGeomodel driverGeomodel) {
        FirebaseDatabase.getInstance().getReference(Common.DRIVER__INFO)
                .child(driverGeomodel.getKey())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.hasChildren()){
                            driverGeomodel.setWinchInfoModel(snapshot.getValue(WinchInfoModel.class));


                            Common.driversFound.get(driverGeomodel.getKey()).setWinchInfoModel(snapshot.getValue(WinchInfoModel.class));
                            ifirebaseDriverInfoListener.onWnchInfoLoadSuccess(driverGeomodel);
                        }else
                            ifirebaseFialedListener.onFirebaseLoadFialed("not find driver key");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        ifirebaseFialedListener.onFirebaseLoadFialed(error.getMessage());
                    }
                });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getContext());
        mMap = googleMap;
        mfusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        Dexter.withContext(getContext()).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Snackbar.make(mapFragment.getView(),getString(R.string.permission_require) , Snackbar.LENGTH_LONG).show();
                    return;
                }
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);

                mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                    @Override
                    public boolean onMyLocationButtonClick() {

                        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                            return true;
                        }
                        mfusedLocationProviderClient.getLastLocation().addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        }).addOnSuccessListener(new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                if( mMap.isMyLocationEnabled()){
                                    LatLng userlocation = new LatLng(location.getLatitude(), location.getLongitude());
                                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userlocation, 18f));
                                }







                            }
                        });
                        return true;
                    }
                });



                //layoutbutton
                View locationbutton=((View)mapFragment.getView().findViewById(Integer.parseInt("1")).getParent())
                        .findViewById(Integer.parseInt("2"));
                RelativeLayout.LayoutParams params=( RelativeLayout.LayoutParams)locationbutton.getLayoutParams();
                //right button
                params.addRule(RelativeLayout.ALIGN_PARENT_TOP,0);
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,RelativeLayout.TRUE);
                params.setMargins(0,0,0,250);

//                buildLocationRequest();
//                buildLocationCallback();
//                updateLocation();




            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                Toast.makeText(getContext(), permissionDeniedResponse.getPermissionName(), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {

            }
        }).check();
        mMap.getUiSettings().setZoomControlsEnabled(true);
        try {
            Boolean success=googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(),R.raw.uber_maps_style));
            if(!success){
                Log.e("EDMT_ERROR","style parsing Error");
                Snackbar.make(getView(),"loading map style error",Snackbar.LENGTH_LONG).show();
            }
        }catch (Resources.NotFoundException e){
            Log.e("EDMT_ERROR",e.getMessage());
            Snackbar.make(getView(),e.getMessage(),Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onFirebaseLoadFialed(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMechanicInfoLoadSuccess(final DriverGeomodel driverGeomodel) {
        //if already have marker with key,dont set again
        if(!Common.markerList.containsKey(driverGeomodel.getKey()))
            Common.markerList.put(driverGeomodel.getKey(),
                    mMap.addMarker(new MarkerOptions().position(new LatLng(driverGeomodel.getGeoLocation().latitude,
                            driverGeomodel.getGeoLocation().longitude))
                    .flat(true)
                    .title(Common.buildName(driverGeomodel.getMechanicInfoModel().getName()))
                    .snippet(driverGeomodel.getMechanicInfoModel().getPhone()).icon(BitmapDescriptorFactory.fromResource(R.drawable.mechanic_small))));
        if(!TextUtils.isEmpty(cityName)){
            final DatabaseReference driverLocation=FirebaseDatabase.getInstance()
                    .getReference("MechanicLocation")
                    .child(cityName)
                    .child(driverGeomodel.getKey());
            driverLocation.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(!snapshot.hasChildren()){
                        if(Common.markerList.get(driverGeomodel.getKey())!=null)
                            Common.markerList.get(driverGeomodel.getKey()).remove();//remove marker
                        Common.markerList.remove(driverGeomodel.getKey());//remove marker info hash map
                        Common.driverLocationsSubscribe.remove(driverGeomodel.getKey());//remove driver info
                        if(Common.driversFound!=null&&Common.driversFound.size()>0)//remove local info of driver
                            Common.driversFound.remove(driverGeomodel.getKey());
                        driverLocation.removeEventListener(this);//remove event listener
                        //route from my location to search
                    }else{

                        if(Common.markerList.get(driverGeomodel.getKey())!=null){
                            GeoQueryModel geoQueryModel= snapshot.getValue(GeoQueryModel.class);
                            AnimationModel animationModel=new AnimationModel(false,geoQueryModel);
                            if(Common.driverLocationsSubscribe.get(driverGeomodel.getKey())!=null){
                                Marker currentMarker=Common.markerList.get(driverGeomodel.getKey());
                                AnimationModel oldPostion=Common.driverLocationsSubscribe.get(driverGeomodel.getKey());
                                String from =new StringBuilder().append(oldPostion.getGeoQueryModel().getL().get(0))
                                        .append(",").append(animationModel.getGeoQueryModel().getL().get(1)).toString();
                                String to =new StringBuilder().append(animationModel.getGeoQueryModel().getL().get(0))
                                        .append(",").append(oldPostion.getGeoQueryModel().getL().get(1)).toString();
                                moveMarkerAnimation(driverGeomodel.getKey(),animationModel,currentMarker,from,to);

                            }else {
                                // first location start
                                Common.driverLocationsSubscribe.put(driverGeomodel.getKey(),animationModel);
                            }
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), error.getMessage()+" homefragment line 381", Toast.LENGTH_SHORT).show();
                }
            });
        }



    }

    @Override
    public void onWnchInfoLoadSuccess(DriverGeomodel driverGeomodel) {
        //if already have marker with key,dont set again
        if(!Common.markerList.containsKey(driverGeomodel.getKey()))
            Common.markerList.put(driverGeomodel.getKey(),
                    mMap.addMarker(new MarkerOptions().position(new LatLng(driverGeomodel.getGeoLocation().latitude,
                            driverGeomodel.getGeoLocation().longitude))
                            .flat(true)

                            .title(Common.buildName(driverGeomodel.getWinchInfoModel().getName()))
                            .snippet(driverGeomodel.getWinchInfoModel().getPhone())
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.wnsh_small))));
        if(!TextUtils.isEmpty(cityName)){
            final DatabaseReference driverLocation=FirebaseDatabase.getInstance()
                    .getReference("RescueWinchLocation")
                    .child(cityName)
                    .child(driverGeomodel.getKey());
            driverLocation.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(!snapshot.hasChildren()){
                        if(Common.markerList.get(driverGeomodel.getKey())!=null)
                            Common.markerList.get(driverGeomodel.getKey()).remove();//remove marker
                        Common.markerList.remove(driverGeomodel.getKey());//remove marker info hash map
                        Common.driverLocationsSubscribe.remove(driverGeomodel.getKey());//remove driver info
                        if(Common.driversFound!=null&&Common.driversFound.size()>0)//remove local info of driver
                            Common.driversFound.remove(driverGeomodel.getKey());
                        driverLocation.removeEventListener(this);//remove event listener
                        //route from my location to search
                    }else{

                        if(Common.markerList.get(driverGeomodel.getKey())!=null){
                            GeoQueryModel geoQueryModel= snapshot.getValue(GeoQueryModel.class);
                            AnimationModel animationModel=new AnimationModel(false,geoQueryModel);
                            if(Common.driverLocationsSubscribe.get(driverGeomodel.getKey())!=null){
                                Marker currentMarker=Common.markerList.get(driverGeomodel.getKey());
                                AnimationModel oldPostion=Common.driverLocationsSubscribe.get(driverGeomodel.getKey());
                                String from =new StringBuilder().append(oldPostion.getGeoQueryModel().getL().get(0))
                                        .append(",").append(animationModel.getGeoQueryModel().getL().get(1)).toString();
                                String to =new StringBuilder().append(animationModel.getGeoQueryModel().getL().get(0))
                                        .append(",").append(oldPostion.getGeoQueryModel().getL().get(1)).toString();
                                moveMarkerAnimation(driverGeomodel.getKey(),animationModel,currentMarker,from,to);

                            }else {
                                // first location start
                                Common.driverLocationsSubscribe.put(driverGeomodel.getKey(),animationModel);
                            }
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), error.getMessage()+" homefragment line 381", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    @Override
    public void onDriverInfoLoadSuccess(final DriverGeomodel driverGeomodel) {
        //if already have marker with key,dont set again
        if(!Common.markerList.containsKey(driverGeomodel.getKey()))
            Common.markerList.put(driverGeomodel.getKey(),
                    mMap.addMarker(new MarkerOptions().position(new LatLng(driverGeomodel.getGeoLocation().latitude,
                            driverGeomodel.getGeoLocation().longitude))
                            .flat(true)

                            .title(Common.buildName(driverGeomodel.getDriverInfo().getName()))
                            .snippet(driverGeomodel.getDriverInfo().getPhone()).icon(BitmapDescriptorFactory.fromResource(R.drawable.car))));
        if(!TextUtils.isEmpty(cityName)){
            final DatabaseReference driverLocation=FirebaseDatabase.getInstance()
                    .getReference(Common.DRIVER_LOCATION_REFERANCE)
                    .child(cityName)
                    .child(driverGeomodel.getKey());
            driverLocation.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(!snapshot.hasChildren()){
                        if(Common.markerList.get(driverGeomodel.getKey())!=null)
                            Common.markerList.get(driverGeomodel.getKey()).remove();//remove marker
                        Common.markerList.remove(driverGeomodel.getKey());//remove marker info hash map
                        Common.driverLocationsSubscribe.remove(driverGeomodel.getKey());//remove driver info
                        if(Common.driversFound!=null&&Common.driversFound.size()>0)//remove local info of driver
                            Common.driversFound.remove(driverGeomodel.getKey());

                        driverLocation.removeEventListener(this);//remove event listener
                        //route from my location to search
                    }else{

                        if(Common.markerList.get(driverGeomodel.getKey())!=null){
                            GeoQueryModel geoQueryModel= snapshot.getValue(GeoQueryModel.class);
                            AnimationModel animationModel=new AnimationModel(false,geoQueryModel);
                            if(Common.driverLocationsSubscribe.get(driverGeomodel.getKey())!=null){
                                Marker currentMarker=Common.markerList.get(driverGeomodel.getKey());
                                AnimationModel oldPostion=Common.driverLocationsSubscribe.get(driverGeomodel.getKey());
                                String from =new StringBuilder().append(oldPostion.getGeoQueryModel().getL().get(0))
                                        .append(",").append(animationModel.getGeoQueryModel().getL().get(1)).toString();
                                String to =new StringBuilder().append(animationModel.getGeoQueryModel().getL().get(0))
                                        .append(",").append(oldPostion.getGeoQueryModel().getL().get(1)).toString();
                                moveMarkerAnimation(driverGeomodel.getKey(),animationModel,currentMarker,from,to);

                            }else {
                                // first location start
                                Common.driverLocationsSubscribe.put(driverGeomodel.getKey(),animationModel);
                            }
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), error.getMessage()+" homefragment line 381", Toast.LENGTH_SHORT).show();
                }
            });
        }


    }

    private void moveMarkerAnimation(final String key, final AnimationModel animationModel, final Marker currentMarker, String from, String to) {
        if(!animationModel.isRun()){
           // Request Api
            compositeDisposable.add(igoogleApi.getDirections("driving","less_driving",from,to,
                    getActivity().getString(R.string.google_api_key))
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
                                  //  polylinelist=Common.decodePoly(polyline);
                                    animationModel.setPolylinelist(Common.decodePoly(polyline));
                                }
                                //moving

                               animationModel.setIndex(-1);
                                animationModel.setNext(1);
                                Runnable runnable=new Runnable() {
                                    @Override
                                    public void run() {

                                            if (animationModel.getPolylinelist()!=null&&animationModel.getPolylinelist().size() > 1) {
                                                if (animationModel.getIndex() < animationModel.getPolylinelist().size() - 2) {
                                                   // index++;
                                                    animationModel.setIndex(animationModel.getIndex()+1);
                                                    //next=index+1;
                                                    animationModel.setNext(animationModel.getIndex()+1);
                                                  //  start = polylinelist.get(index);
                                                    animationModel.setStart(animationModel.getPolylinelist().get(animationModel.getIndex()));
                                                  //  end = polylinelist.get(next);
                                                    animationModel.setEnd(animationModel.getPolylinelist().get(animationModel.getNext()));
                                                    ValueAnimator valueAnimator = ValueAnimator.ofInt(0, 1);
                                                    valueAnimator.setDuration(3000);
                                                    valueAnimator.setInterpolator(new LinearInterpolator());
                                                    valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                                        @Override
                                                        public void onAnimationUpdate(ValueAnimator animation) {
                                                          //  v = animation.getAnimatedFraction();
                                                            animationModel.setV(animation.getAnimatedFraction());
                                                            //lat = v * end.latitude + (1 - v) * start.latitude;
                                                            animationModel.setLat( (animationModel.getV()* animationModel.getEnd().latitude)+(1-animationModel.getV())*animationModel.getStart().latitude);

                                                            //lng = v * end.longitude + (1 - v) * start.longitude;
                                                            animationModel.setLng( (animationModel.getV()* animationModel.getEnd().longitude)+(1-animationModel.getV())*animationModel.getStart().longitude);
                                                            LatLng newpos = new LatLng(   animationModel.getLat(), animationModel.getLng());
                                                            currentMarker.setPosition(newpos);
                                                            currentMarker.setAnchor(.5f, .5f);
                                                            currentMarker.setRotation(Common.getBearing(   animationModel.getStart(), newpos));
                                                        }
                                                    });
                                                    valueAnimator.start();
                                                   // if (index < polylinelist.size() - 2)//reach destination
                                                    if (animationModel.getIndex() < animationModel.getPolylinelist().size() - 2)
                                                        animationModel.getHandler() .postDelayed(this, 1500);
                                                    else if (animationModel.getIndex() < animationModel.getPolylinelist().size() - 1) {//done

                                                        animationModel.setRun(true);
                                                        Common.driverLocationsSubscribe.put(key, animationModel);//update dara
                                                    }


                                                }
                                            }

                                        }

                                };
                                //run handler
                                animationModel.getHandler().postDelayed(runnable,1500);

                            } catch (Exception e){
                                Toast.makeText(getContext(), e.getMessage()+" Line 484", Toast.LENGTH_SHORT).show();



                                }

                        }
                    }));
        }
    }
}