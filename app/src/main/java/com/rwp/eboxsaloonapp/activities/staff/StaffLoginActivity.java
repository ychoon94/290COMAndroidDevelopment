package com.rwp.eboxsaloonapp.activities.staff;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;
import com.rwp.eboxsaloonapp.R;
import com.rwp.eboxsaloonapp.databinding.ActivityStaffLoginBinding;
import com.rwp.eboxsaloonapp.models.ApiResponse;
import com.rwp.eboxsaloonapp.retrofitutil.ApiClient;
import com.rwp.eboxsaloonapp.retrofitutil.ApiInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StaffLoginActivity extends AppCompatActivity {

    private ActivityStaffLoginBinding mStaffLoginBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_staff_login);
        mStaffLoginBinding = DataBindingUtil.setContentView(this, R.layout.activity_staff_login);

        mStaffLoginBinding.buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performLogin();
                mStaffLoginBinding.showProgress.setVisibility(View.VISIBLE);
            }
        });
    }

    private void performLogin(){
        String username = mStaffLoginBinding.editTextTextEmailAddress.getText().toString();
        String password = mStaffLoginBinding.editTextTextPassword.getText().toString();

        retrofit2.Call<ApiResponse> call = ApiClient.getApiClient().create(ApiInterface.class).performStaffLogin(username, password);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if(response.code() == 200){
                    if (response.body().getStatus().equals("ok")){
                        if (response.body().getResultCode()==1){
                            String staffID = response.body().getStaffid();
                            Intent intent = new Intent(StaffLoginActivity.this, StaffHomeActivity.class);
                            intent.putExtra("staffID", staffID);
                            Log.d("loginscreenstaff ID", staffID);
                            startActivity(intent);
                            finish();
                        } else {
//                            Log.d("GETBODY", response.body().getStatus());
//                            Log.d("GETRESULT", String.valueOf(response.body().getResultCode()));
//                            Log.d("USERNAME", username);
//                            Log.d("PASSWORD", password);
                            displayUserInformation("Login fail");
                            mStaffLoginBinding.editTextTextPassword.setText("");
                        }
                    } else {
                        displayUserInformation("Something went wrong...");
                        mStaffLoginBinding.editTextTextPassword.setText("");
                    }
                } else {
                    displayUserInformation("Something went wrong...");
                    mStaffLoginBinding.editTextTextPassword.setText("");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {

            }
        });
    }

    private void displayUserInformation(String message){
        Snackbar.make(mStaffLoginBinding.myConstraintLayout, message, Snackbar.LENGTH_SHORT).show();
        mStaffLoginBinding.showProgress.setVisibility(View.INVISIBLE);
    }

    public void launchForgetPasswordScreen(View view) {
        Intent intent = new Intent(StaffLoginActivity.this, StaffForgotPasswordActivity.class);
        startActivity(intent);
        finish();
    }
}