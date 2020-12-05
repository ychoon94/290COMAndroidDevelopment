package com.rwp.eboxsaloonapp.activities.customer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.rwp.eboxsaloonapp.R;
import com.rwp.eboxsaloonapp.databinding.ActivityCustomerProfileBinding;
import com.rwp.eboxsaloonapp.models.ApiResponse;
import com.rwp.eboxsaloonapp.retrofitutil.ApiClient;
import com.rwp.eboxsaloonapp.retrofitutil.ApiInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerProfile extends AppCompatActivity {

    ActivityCustomerProfileBinding mProfileBinding;

    private String customerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_customer_profile);

        mProfileBinding = DataBindingUtil.setContentView(this, R.layout.activity_customer_profile);

        mProfileBinding.toolbar.setTitle("User Profile");

        Intent intent = getIntent();
        customerName = intent.getStringExtra("custname");

        setCustomerDetail(customerName);
    }

    private void setCustomerDetail(String customerName) {
        Call<ApiResponse> call = ApiClient.getApiClient().create(ApiInterface.class).getCustomerProfile(customerName);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.code() == 200){
                    if (response.body().getStatus().equals("ok")){
                        if (response.body().getResultCode()==1){
                            mProfileBinding.username1.setText(customerName);
                            mProfileBinding.address1.setText(response.body().getAddress());
                            mProfileBinding.phonenumber1.setText(response.body().getPhone());
                        } else {
                            Toast.makeText(CustomerProfile.this, "No such customer", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(CustomerProfile.this, "No such customer", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CustomerProfile.this, "No such customer", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(CustomerProfile.this, "no response from server", Toast.LENGTH_SHORT).show();
            }
        });
    }
}