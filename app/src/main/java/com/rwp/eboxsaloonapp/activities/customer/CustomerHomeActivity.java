package com.rwp.eboxsaloonapp.activities.customer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.rwp.eboxsaloonapp.R;
import com.rwp.eboxsaloonapp.databinding.ActivityCustomerHomeBinding;

public class CustomerHomeActivity extends AppCompatActivity {

    private ActivityCustomerHomeBinding mCustomerHomeBinding;
    SharedPreferences mSharedPreferences;
    private String custName;

    public static final String MyPREFERENCES = "MyPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_home);

        mCustomerHomeBinding = DataBindingUtil.setContentView(this, R.layout.activity_customer_home);

        mCustomerHomeBinding.toolbar.setTitle("Home");
        custName = getIntent().getStringExtra("username");

        //sharing data across the activity
        mSharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();

        editor.putString("custname", custName);
        editor.commit();
    }

    public void launchBookService(View view) {
        Intent bookService = new Intent(this, CustomerBookServiceActivity.class);
        bookService.putExtra("custname", custName);
        startActivity(bookService);
    }

    public void launchCheckAppointment(View view) {
        Intent checkAppointment = new Intent(this, CustomerCheckAppointment.class);
        checkAppointment.putExtra("custname", custName);
        startActivity(checkAppointment);
    }

    public void launchCustomerProfile(View view) {
        Intent checkProfile = new Intent(this, CustomerProfile.class);
        checkProfile.putExtra("custname", custName);
        startActivity(checkProfile);
    }

    public void Logout(View view) {
        finish();
    }
}