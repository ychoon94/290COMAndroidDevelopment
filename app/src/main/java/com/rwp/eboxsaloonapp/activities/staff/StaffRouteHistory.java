package com.rwp.eboxsaloonapp.activities.staff;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rwp.eboxsaloonapp.R;

import java.util.Objects;

public class StaffRouteHistory extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private DatabaseReference mDatabaseReference;

    public static final String MyPREFERENCES = "MyPrefs";

    private String appointmentID;

    private LocationListener mLocationListener;
    private LocationManager mLocationManager;
    private final long MIN_TIME = 3000; //millisecond
    private final long MIN_DIST = 0; //meter

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_route_history);

        SharedPreferences mSharedPreferences = this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String restoredText = mSharedPreferences.getString("staffID", null);

        Intent intent = getIntent();
        appointmentID = intent.getStringExtra("appointmentID");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child(restoredText).child(appointmentID);

        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                PolylineOptions polylineOptions = new PolylineOptions();
                polylineOptions.color(Color.RED);
                polylineOptions.width(6);
                for (DataSnapshot ds : snapshot.getChildren()){
                    Double latitude = Double.parseDouble(Objects.requireNonNull(ds.child("latitude").getValue(String.class)));
                    Double longitude = Double.parseDouble(Objects.requireNonNull(ds.child("longitude").getValue(String.class)));
                    polylineOptions.add(new LatLng(latitude,longitude));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(5.371400, 100.248787), 10));
                }
                Polyline polyline = mMap.addPolyline(polylineOptions);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("FIREBASE retrieval fail", error.getMessage());
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        setupGoogleMapScreenSettings(googleMap);
        mMap = googleMap;
        //default zoom to Penang Island
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(5.371400, 100.248787), 9));
    }

    private void setupGoogleMapScreenSettings(GoogleMap mMap){
        mMap.setBuildingsEnabled(true);
        mMap.setIndoorEnabled(true);
        mMap.setTrafficEnabled(false);
        UiSettings mUiSettings = mMap.getUiSettings();
        mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setCompassEnabled(true);
        mUiSettings.setMyLocationButtonEnabled(true);
        mUiSettings.setScrollGesturesEnabled(true);
        mUiSettings.setZoomGesturesEnabled(true);
        mUiSettings.setTiltGesturesEnabled(true);
        mUiSettings.setRotateGesturesEnabled(true);
    }
}