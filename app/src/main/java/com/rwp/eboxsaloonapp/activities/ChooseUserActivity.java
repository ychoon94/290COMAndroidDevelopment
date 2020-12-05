package com.rwp.eboxsaloonapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.rwp.eboxsaloonapp.R;
import com.rwp.eboxsaloonapp.activities.customer.CustomerLoginActivity;
import com.rwp.eboxsaloonapp.activities.staff.StaffLoginActivity;

public class ChooseUserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_user);
    }

    public void launchStaffLoginPage(View view) {
        Intent staffLogin = new Intent(this, StaffLoginActivity.class);
        startActivity(staffLogin);
    }

    public void launchCustomerLoginPage(View view) {
        Intent customerLogin = new Intent(this, CustomerLoginActivity.class);
        startActivity(customerLogin);
    }
}