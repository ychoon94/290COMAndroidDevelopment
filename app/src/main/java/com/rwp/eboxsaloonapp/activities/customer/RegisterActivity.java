package com.rwp.eboxsaloonapp.activities.customer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.rwp.eboxsaloonapp.R;
import com.rwp.eboxsaloonapp.databinding.ActivityRegisterBinding;
import com.rwp.eboxsaloonapp.models.ApiResponse;
import com.rwp.eboxsaloonapp.retrofitutil.ApiClient;
import com.rwp.eboxsaloonapp.retrofitutil.ApiInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding mRegisterBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mRegisterBinding = DataBindingUtil.setContentView(this, R.layout.activity_register);

        mRegisterBinding.buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performSignUp();
                mRegisterBinding.showProgress.setVisibility(View.VISIBLE);
            }
        });
    }

    private void performSignUp(){
        String username = mRegisterBinding.editTextTextEmailAddress.getText().toString();
        String password = mRegisterBinding.editTextTextPassword.getText().toString();
        String address = mRegisterBinding.editTextTextPostalAddress.getText().toString();
        String phone = mRegisterBinding.editTextPhone.getText().toString();

        if (mRegisterBinding.editTextTextPassword.getText().toString()
                .equals(mRegisterBinding.editTextTextPassword2.getText().toString())){

            retrofit2.Call<ApiResponse> call = ApiClient.getApiClient().create(ApiInterface.class)
                    .performUserRegistration(username, address, phone, password);
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    if (response.code() == 200){
                        if (response.body().getStatus().equals("ok")){
                            if (response.body().getResultCode()==1){
                                Toast.makeText(RegisterActivity.this,
                                        "Registration Success. Now you can login", Toast.LENGTH_LONG).show();
                                onBackPressed();
                                finish();
                            } else {
                                displayUserInfo("User already exists...");
                                mRegisterBinding.editTextTextPassword.setText("");
                            }
                        } else {
                            displayUserInfo("Something went wrong...");
                            mRegisterBinding.editTextTextPassword.setText("");
                        }
                    } else {
                        displayUserInfo("Request failed...");
                        Log.d("ERROR", String.valueOf(response.code()));
                        mRegisterBinding.editTextTextPassword.setText("");
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    Log.d("ERROR", "ERROR FOUND");
                }
            });
        } else {
            mRegisterBinding.progressBar.setVisibility(View.INVISIBLE);
            mRegisterBinding.textViewLoggingIn.setVisibility(View.INVISIBLE);
            //displayUserInfo("Wrong password, try again");
            Toast.makeText(this, "Wrong password, Please try again.", Toast.LENGTH_SHORT).show();
            mRegisterBinding.editTextTextPassword.setText("");
            mRegisterBinding.editTextTextPassword2.setText("");
        }
    }

    private void displayUserInfo(String message){
        Snackbar.make(mRegisterBinding.myConstraintLayout,message,Snackbar.LENGTH_SHORT).show();
        mRegisterBinding.editTextTextPassword.setText("");
        mRegisterBinding.progressBar.setVisibility(View.INVISIBLE);
        mRegisterBinding.textViewLoggingIn.setVisibility(View.INVISIBLE);
//        mRegisterBinding.showProgress.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void returnToCustomerLoginPage(View view) {
        onBackPressed();
        finish();
    }
}