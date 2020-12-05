package com.rwp.eboxsaloonapp.activities.staff;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rwp.eboxsaloonapp.R;
import com.rwp.eboxsaloonapp.databinding.ActivityStaffAppointmentDetailBinding;
import com.rwp.eboxsaloonapp.databinding.ActivityStaffJobBinding;
import com.rwp.eboxsaloonapp.models.ApiResponse;
import com.rwp.eboxsaloonapp.models.latlng;
import com.rwp.eboxsaloonapp.retrofitutil.ApiClient;
import com.rwp.eboxsaloonapp.retrofitutil.ApiInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StaffAppointmentDetail extends AppCompatActivity {

    private ActivityStaffAppointmentDetailBinding mAppointmentDetailBinding;

    private DatabaseReference mDatabaseReference;

    public static final String MyPREFERENCES = "MyPrefs";

    private LocationListener mLocationListener;
    private LocationManager mLocationManager;
    private final long MIN_TIME = 3000; //in millisecond
    private final long MIN_DIST = 0; //in meter
    private static final int REQUEST_LOCATION_PERMISSION = 1;

    private String appointmentID;

    latlng mLatlng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_staff_appointment_detail);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION},
                PackageManager.PERMISSION_GRANTED);

        SharedPreferences mSharedPreferences = this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String restoredText = mSharedPreferences.getString("staffID", null);

        mAppointmentDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_staff_appointment_detail);
        Intent intent = getIntent();
        appointmentID = intent.getStringExtra("appointmentID");
        String status = intent.getStringExtra("status");

        if (status.equals("Completed")){
            mAppointmentDetailBinding.buttonNavigationhistory.setVisibility(View.VISIBLE);
            mAppointmentDetailBinding.buttonNavigate.setVisibility(View.INVISIBLE);
        }

        if (status.equals("Ongoing")){
            mAppointmentDetailBinding.buttonNavigationhistory.setVisibility(View.INVISIBLE);
            mAppointmentDetailBinding.buttonNavigate.setVisibility(View.VISIBLE);
        }

        Log.d("TESTING!!!23423423423!", appointmentID);

        fillAppointmentDetail(appointmentID);

        mLatlng = new latlng();

        //the structure for FirebaseDatabase.getInstance().getReference("StaffID").child(appointmentID);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference(restoredText).child(appointmentID);

    }

    private void fillAppointmentDetail(String appointmentID){
        Call<ApiResponse> call = ApiClient.getApiClient().create(ApiInterface.class).getStaffAppointmentDetail(appointmentID);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.code() == 200){
                    Log.d("TESTING!!!!", appointmentID);
                    mAppointmentDetailBinding.textViewServicetype.setText("Service: " + response.body().getService());
                    mAppointmentDetailBinding.textViewCustomername.setText("Customer Name: " + response.body().getCustname());
                    mAppointmentDetailBinding.textViewCustomerphone.setText("Contact Number: " + response.body().getCustphone());
                    mAppointmentDetailBinding.textViewDate.setText("Date: " + response.body().getAppointment_date());
                    mAppointmentDetailBinding.textViewTime.setText("Time: " + response.body().getAppointment_time());
                    mAppointmentDetailBinding.textViewAddress.setText("Address: " + response.body().getAppointment_location());
                    mAppointmentDetailBinding.textViewStatus.setText("Job Status: " + response.body().getAppointment_status());

                } else {
                    displayUserInformation("Something went wrong...");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {

            }
        });

    }

    private void displayUserInformation(String message){
        Snackbar.make(mAppointmentDetailBinding.myConstraintLayout, message, Snackbar.LENGTH_SHORT).show();
        //mAppointmentDetailBindin/g.showProgress.setVisibility(View.INVISIBLE);
    }

    public void launchNavigationToCustomerLocation(View view) {

        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + mAppointmentDetailBinding.textViewAddress.getText().toString() + "&mode=d");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        staffLocationTracking(view);
        startActivity(mapIntent);
    }

    public void launchNavigationHistory(View view) {
        Intent launchRouteHistory = new Intent(this, StaffRouteHistory.class);
        launchRouteHistory.putExtra("appointmentID", appointmentID);
        startActivity(launchRouteHistory);
    }

    public void staffLocationTracking(View view){
        mLocationListener = location -> {
            try {
                mLatlng.setLatitude(Double.toString(location.getLatitude()));
                mLatlng.setLongitude(Double.toString(location.getLongitude()));
                updateDatabaseLocation();
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        } else {
            try {
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DIST, mLocationListener);
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DIST, mLocationListener);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void updateDatabaseLocation(){
        mDatabaseReference.push().setValue(mLatlng);
    }
}