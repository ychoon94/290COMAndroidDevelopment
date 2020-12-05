package com.rwp.eboxsaloonapp.activities.customer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.rwp.eboxsaloonapp.R;
import com.rwp.eboxsaloonapp.activities.staff.FetchStaffLocation;
import com.rwp.eboxsaloonapp.databinding.ActivityCustomerCheckAppointmentDetailBinding;
import com.rwp.eboxsaloonapp.models.ApiResponse;
import com.rwp.eboxsaloonapp.retrofitutil.ApiClient;
import com.rwp.eboxsaloonapp.retrofitutil.ApiInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerCheckAppointmentDetail extends AppCompatActivity {

    ActivityCustomerCheckAppointmentDetailBinding mAppointmentDetailBinding;

    private String StaffID;
    private String appointmentID;
    private String staffPhone;
    private String appointmentStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_customer_check_appointment_detail);
        mAppointmentDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_customer_check_appointment_detail);
        Intent intent = getIntent();
        appointmentID = intent.getStringExtra("appointmentID");
        appointmentStatus = intent.getStringExtra("appointmentStatus");

        if (appointmentStatus.equals("Completed")){
            mAppointmentDetailBinding.callServicestaff.setVisibility(View.INVISIBLE);
            mAppointmentDetailBinding.buttonViewStaffLocation.setVisibility(View.INVISIBLE);
        }


        fillAppointmentDetail(appointmentID);

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
                    StaffID = response.body().getStaffid();
                    Log.d("staffid",StaffID);
                    getStaffPhoneNumber(StaffID);

                } else {
                    displayUserInformation("Something went wrong...");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {

            }
        });

    }

    public void serviceCompleted(View view) {
        Call<ApiResponse> call = ApiClient.getApiClient().create(ApiInterface.class).markServiceCompletion(appointmentID, "Completed");
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.code() == 200){
                    if (response.body().getStatus().equals("ok")){
                        if (response.body().getResultCode() == 1){
                            Toast.makeText(CustomerCheckAppointmentDetail.this, "Appointment mark completion successfully!!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(CustomerCheckAppointmentDetail.this, "Something went wrong at resultcode 0", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(CustomerCheckAppointmentDetail.this, "Something went wrong at status not OK", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CustomerCheckAppointmentDetail.this, "Connection not estabished", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(CustomerCheckAppointmentDetail.this, "FUCKING ERRORED", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getStaffPhoneNumber(String staffID){
        Call<ApiResponse> call = ApiClient.getApiClient().create(ApiInterface.class).getServiceStaffPhone(staffID);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.code() == 200){
                    Log.d("TESTING!!!!", appointmentID);
                    staffPhone = response.body().getStaff_phone();
                    //Log.d("staffphone1", staffPhone);

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
        //mAppointmentDetailBinding.showProgress.setVisibility(View.INVISIBLE);
    }

    public void launchNavigationToCustomerLocation(View view) {
        Intent launchNavigation = new Intent(this, FetchStaffLocation.class);
        launchNavigation.putExtra("StaffID", StaffID);
        launchNavigation.putExtra("appointmentID", appointmentID);
        launchNavigation.putExtra("Address", mAppointmentDetailBinding.textViewAddress.getText().toString());
        launchNavigation.putExtra("StaffPhone", staffPhone);
        startActivity(launchNavigation);
    }

//    public void callServiceStaff(View view) {
//        Log.d("staffphone1", staffPhone);
//        Intent intent = new Intent(Intent.ACTION_DIAL);
//        intent.setData(Uri.parse("tel: " + staffPhone));
//        if (intent.resolveActivity(getPackageManager()) != null) {
//            startActivity(intent);
//        }
//    }


}

