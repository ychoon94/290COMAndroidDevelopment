package com.rwp.eboxsaloonapp.activities.customer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;
import com.rwp.eboxsaloonapp.R;
import com.rwp.eboxsaloonapp.databinding.ActivityCustomerLoginBinding;
import com.rwp.eboxsaloonapp.models.ApiResponse;
import com.rwp.eboxsaloonapp.retrofitutil.ApiClient;
import com.rwp.eboxsaloonapp.retrofitutil.ApiInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerLoginActivity extends AppCompatActivity {

    private ActivityCustomerLoginBinding mCustomerLoginBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_customer_login);
        mCustomerLoginBinding = DataBindingUtil.setContentView(this, R.layout.activity_customer_login);
        //getSupportActionBar(mCustomerLoginBinding.toolbar);
        //getSupportActionBar().setTitle("Login Page");

        mCustomerLoginBinding.buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CustomerLoginActivity.this, RegisterActivity.class));
            }
        });

        mCustomerLoginBinding.buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performLogin();
                mCustomerLoginBinding.showProgress.setVisibility(View.VISIBLE);
            }
        });
    }

    private void performLogin(){
        String username = mCustomerLoginBinding.editTextTextEmailAddress.getText().toString();
        String password = mCustomerLoginBinding.editTextTextPassword.getText().toString();

        retrofit2.Call<ApiResponse> call = ApiClient.getApiClient().create(ApiInterface.class).performUserLogin(username, password);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if(response.code() == 200){
                    if (response.body().getStatus().equals("ok")){
                        if (response.body().getResultCode()==1){
                            String name = response.body().getUsername();
                            Intent intent = new Intent(CustomerLoginActivity.this, CustomerHomeActivity.class);
                            intent.putExtra("username", name);
                            startActivity(intent);
                            finish();
                        } else {
                            displayUserInformation("Login fail");
                            mCustomerLoginBinding.editTextTextPassword.setText("");
                        }
                    } else {
                        displayUserInformation("Something went wrong...");
                        mCustomerLoginBinding.editTextTextPassword.setText("");
                    }
                } else {
                    displayUserInformation("Something went wrong...");
                    mCustomerLoginBinding.editTextTextPassword.setText("");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {

            }
        });
    }

    private void displayUserInformation(String message){
        Snackbar.make(mCustomerLoginBinding.myConstraintLayout, message, Snackbar.LENGTH_SHORT).show();
        mCustomerLoginBinding.showProgress.setVisibility(View.INVISIBLE);
    }

    public void launchForgetPasswordScreen(View view) {

    }
}