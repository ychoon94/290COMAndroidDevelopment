package com.rwp.eboxsaloonapp.activities.staff;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.android.PolyUtil;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.TravelMode;
import com.rwp.eboxsaloonapp.R;
import com.rwp.eboxsaloonapp.activities.staff.FetchAddressTask;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class FetchStaffLocation extends AppCompatActivity implements OnMapReadyCallback, FetchAddressTask.OnTaskCompleted {

    // Constants
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private static final String TRACKING_LOCATION_KEY = "tracking_location";

    // Location classes
    private boolean mTrackingLocation;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;

    // Views
    private Button mLocationButton;
    private TextView mLocationTextView;

    private static final int overview = 0;

    GoogleMap mMap;

    private String StaffID;
    private String appointmentID;
    private String address;

    private Double latitude;
    private Double longitude;

    private DatabaseReference mDatabaseReference;

    LatLng stafflocation;
    private String staffPhone;

    //List<Marker> AllMarkers = new ArrayList<Marker>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fetch_staff_location);

        Intent intent = getIntent();
        StaffID = intent.getStringExtra("StaffID");
        appointmentID = intent.getStringExtra("appointmentID");
        address = intent.getStringExtra("Address");
        staffPhone = intent.getStringExtra("StaffPhone");

        Log.d("Staffid", address);


        mLocationButton = (Button) findViewById(R.id.button_location);
        mLocationTextView = (TextView) findViewById(R.id.textview_location);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);

        //the structure for FirebaseDatabase.getInstance().getReference().child("StaffID").child("appointmentID");
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child(StaffID).child(appointmentID);

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()) {
                    latitude = Double.parseDouble(Objects.requireNonNull(ds.child("latitude").getValue(String.class)));
                    longitude = Double.parseDouble(Objects.requireNonNull(ds.child("longitude").getValue(String.class)));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 14));
                }
                stafflocation = new LatLng(latitude, longitude);
                mLocationTextView.setText(stafflocation.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("FIREBASE retrieval fail", error.getMessage());
            }
        };
        mDatabaseReference.addValueEventListener(valueEventListener);




        //initialize the FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        //Restore the state if activity is recreated
        if (savedInstanceState != null){
            mTrackingLocation = savedInstanceState.getBoolean(TRACKING_LOCATION_KEY);
        }

        //startTrackingLocation();
        //set the listener for the location button
        mLocationButton.setOnClickListener(new View.OnClickListener(){
            /**
             * Toggle the tracking state.
             * @param v The track location button.
             */
            @Override
            public void onClick(View v) {
                if (!mTrackingLocation){
                    startTrackingLocation();
                } else {
                    stopTrackingLocation();
                }
            }
        });

        //Initialize the location callbacks.
        mLocationCallback = new LocationCallback(){
            /**
             * This is the callback that is triggered when the
             * FusedLocationClient updates your location.
             * @param locationResult The result containing the device location.
             */
            @Override
            public void onLocationResult(LocationResult locationResult) {
                //super.onLocationResult(locationResult);
                if (mTrackingLocation) {
                    new FetchAddressTask(FetchStaffLocation.this, FetchStaffLocation.this)
                            .execute(locationResult.getLastLocation());
                    Log.d("last location res", locationResult.getLastLocation().toString());
                }
            }
        };
    }

    /**
     * Get direction result
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private DirectionsResult getDirectionsDetails(String origin, String destination, TravelMode mode){
        try {
            return DirectionsApi.newRequest(getGeoContext()).mode(mode).origin(origin).destination(destination)
                    .departureTime(Instant.now()).await();
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        setupGoogleMapScreenSettings(googleMap);
        mMap = googleMap;

        //DirectionsResult results = getDirectionsDetails("483 George St, Sydney NSW 2000, Australia","182 Church St, Parramatta NSW 2150, Australia",TravelMode.DRIVING);
//        DirectionsResult results = getDirectionsDetails(mLocationTextView.getText().toString(),"BL Avenue",TravelMode.DRIVING);
//
//        if (results != null) {
//            addPolyline(results, googleMap);
//            positionCamera(results.routes[overview], googleMap);
//            addMarkersToMap(results, googleMap);
//        }
    }


    private void setupGoogleMapScreenSettings(GoogleMap mMap){
        mMap.setBuildingsEnabled(true);
        mMap.setIndoorEnabled(true);
        mMap.setTrafficEnabled(true);
        UiSettings mUiSettings = mMap.getUiSettings();
        mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setCompassEnabled(true);
        mUiSettings.setMyLocationButtonEnabled(true);
        mUiSettings.setScrollGesturesEnabled(true);
        mUiSettings.setZoomGesturesEnabled(true);
        mUiSettings.setTiltGesturesEnabled(true);
        mUiSettings.setRotateGesturesEnabled(true);
    }

    private void addMarkersToMap(DirectionsResult results, GoogleMap mMap) {
        //mMap.addMarker(new MarkerOptions().position(new LatLng(results.routes[overview].legs[overview].startLocation.lat,results.routes[overview].legs[overview].startLocation.lng)).title(results.routes[overview].legs[overview].startAddress));
        Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).draggable(false).title("staff" + latitude + " , " + longitude));
        mMap.addMarker(new MarkerOptions().position(new LatLng(results.routes[overview].legs[overview].endLocation.lat,results.routes[overview].legs[overview].endLocation.lng)).title(results.routes[overview].legs[overview].endAddress).snippet(getEndLocationTitle(results)));
    }

    private void positionCamera(DirectionsRoute route, GoogleMap mMap) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(route.legs[overview].startLocation.lat, route.legs[overview].startLocation.lng), 14));
    }

    private void addPolyline(DirectionsResult results, GoogleMap mMap) {
        List<LatLng> decodedPath = PolyUtil.decode(results.routes[overview].overviewPolyline.getEncodedPath());
        mMap.addPolyline(new PolylineOptions().addAll(decodedPath));
    }

    private String getEndLocationTitle(DirectionsResult results){
        return  "Time :"+ results.routes[overview].legs[overview].duration.humanReadable + " Distance :" + results.routes[overview].legs[overview].distance.humanReadable;
    }

    private GeoApiContext getGeoContext(){
        GeoApiContext geoApiContext = new GeoApiContext.Builder().apiKey("AIzaSyDXTdo0lt04r9Z0TvbY1-d1OM1p1_rv6EI").queryRateLimit(3)
                .readTimeout(1, TimeUnit.SECONDS).connectTimeout(1,TimeUnit.SECONDS).writeTimeout(1,TimeUnit.SECONDS).build();

        return geoApiContext;
    }


    /**
     * Starts tracking the device. Checks for
     * permissions, and requests them if they aren't present. If they are,
     * requests periodic location updates, sets a loading text and starts the
     * animation.
     */
    private void startTrackingLocation(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        } else {
            mTrackingLocation = true;
            mFusedLocationClient.requestLocationUpdates(getLocationRequest(), mLocationCallback, null /* looper*/);

            Log.d("LOACATION REQUEST", getLocationRequest().toString());

            // Set a loading text while you wait for the address to be
            // returned
//            mLocationTextView.setText(getString(R.string.address_text,
//                    getString(R.string.loading),
//                    System.currentTimeMillis()));
            mLocationTextView.setText(getString(R.string.address_text1,
                    getString(R.string.loading)));

            mLocationButton.setText(R.string.stop_tracking_location);
        }
    }

    /**
     * Stops tracking the device. Removes the location
     * updates, stops the animation, and resets the UI.
     */
    private void stopTrackingLocation(){
        if (mTrackingLocation){
            mTrackingLocation = false;
            mLocationButton.setText(R.string.start_tracking_location);
            mLocationTextView.setText(R.string.textview_hint);
        }
    }

    /**
     * Sets up the location request.
     *
     * @return The LocationRequest object containing the desired parameters.
     */
    private LocationRequest getLocationRequest(){
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

    /**
     * Saves the last location on configuration change
     */
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBoolean(TRACKING_LOCATION_KEY, mTrackingLocation);
        super.onSaveInstanceState(outState);
    }

    /**
     * Callback that is invoked when the user responds to the permissions
     * dialog.
     *
     * @param requestCode  Request code representing the permission request
     *                     issued by the app.
     * @param permissions  An array that contains the permissions that were
     *                     requested.
     * @param grantResults An array with the results of the request for each
     *                     permission requested.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_LOCATION_PERMISSION:
                // if the permission is granted, get the location, otherwise,
                // show a toast
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    startTrackingLocation();
                } else {
                    Toast.makeText(this, R.string.location_permission_denied, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onTaskCompleted(String result) {
        if (mTrackingLocation){
            //update the UI
            //mLocationTextView.setText(getString(R.string.address_text, result, System.currentTimeMillis()));
            mLocationTextView.setText(getString(R.string.address_text1, result));
        }
    }

    @Override
    protected void onPause() {
        if (mTrackingLocation){
            stopTrackingLocation();
            mTrackingLocation = true;
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (mTrackingLocation){
            startTrackingLocation();
        }
        super.onResume();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void plotDirectionONMAP(View view) {
//        DirectionsResult results = getDirectionsDetails(mLocationTextView.getText().toString(),"BL Avenue",TravelMode.DRIVING);

        DirectionsResult results = getDirectionsDetails(mLocationTextView.getText().toString(),address,TravelMode.DRIVING);
        Log.d("Staffid", address);
        Log.d("Staffid", stafflocation.toString());

        //Log.d("DOES IT WORK??", mLocationTextView.getText().toString());

        if (results != null) {
            //addPolyline(results, mMap);
            positionCamera(results.routes[overview], mMap);
            addMarkersToMap(results, mMap);
        }
    }

    public void callServiceStaff(View view) {
        Log.d("staffphone1", staffPhone);
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel: " + staffPhone));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}