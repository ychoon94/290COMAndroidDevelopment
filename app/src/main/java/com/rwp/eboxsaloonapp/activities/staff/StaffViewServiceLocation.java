package com.rwp.eboxsaloonapp.activities.staff;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.android.data.Point;
import com.google.maps.model.GeocodingResult;
import com.rwp.eboxsaloonapp.R;
import com.rwp.eboxsaloonapp.models.latlng;


//unused activity
public class StaffViewServiceLocation extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String customerAddress;

    latlng dLatitude;
    latlng dLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_view_service_location);

        Intent intent = getIntent();
        customerAddress = intent.getStringExtra("customername");

//        getDestinationofCustomer();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        GeoApiContext context = new GeoApiContext.Builder().apiKey("AIzaSyDXTdo0lt04r9Z0TvbY1-d1OM1p1_rv6EI").build();
        try {
            if (customerAddress.length() > 0){
                GeocodingResult[] results = GeocodingApi.geocode(context, customerAddress).await();

                //dLatitude.setLatitude(results[0].geometry.location.lat);
                //dLongitude.setLongitude(results[0].geometry.location.lng);
                LatLng latLng = new LatLng(results[0].geometry.location.lat, results[0].geometry.location.lng);
                //Point point = new Point(latLng);
            } else {
                Log.d("Geocode error","Unable to geocode address of " + customerAddress + ": No address available");
            }
        } catch ( Exception e){
            e.printStackTrace();
        }


        // Add a marker in Sydney and move the camera
        LatLng serviceLocation = new LatLng(getDestinationofCustomer().latitude, getDestinationofCustomer().longitude);
        mMap.addMarker(new MarkerOptions().position(serviceLocation).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(serviceLocation, 10));
    }

    public void launchGoogleMap(View view) {

    }

    public LatLng getDestinationofCustomer(){
        return null;
    }
}