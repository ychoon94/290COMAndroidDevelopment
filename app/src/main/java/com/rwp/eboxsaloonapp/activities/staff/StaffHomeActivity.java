package com.rwp.eboxsaloonapp.activities.staff;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.rwp.eboxsaloonapp.R;
import com.rwp.eboxsaloonapp.databinding.ActivityHomeBinding;

public class StaffHomeActivity extends AppCompatActivity {

    private ActivityHomeBinding mActivityHomeBinding;
    SharedPreferences mSharedPreferences;

    public static final String MyPREFERENCES = "MyPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_home);
        mActivityHomeBinding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        String staffID = getIntent().getStringExtra("staffID");
        //mHomeBinding.textwelcomeUser.setText("Welcome, " + username);

        //sharing data across the activity
        mSharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();

        editor.putString("staffID", staffID);
        editor.commit();
    }

    public void launchProfileActivity(View view) {
    }

    public void launchJobActivity(View view) {
        Intent launchJobActivity = new Intent(this, StaffJobActivity.class);
        String staffID = getIntent().getStringExtra("staffID");
        launchJobActivity.putExtra("staffID", staffID);
        startActivity(launchJobActivity);
    }

    public void LaunchRatingActivity(View view) {
    }

    public void LaunchScheduleActivity(View view) {
    }

    public void sessionLogout(View view) {
        finish();
    }
}