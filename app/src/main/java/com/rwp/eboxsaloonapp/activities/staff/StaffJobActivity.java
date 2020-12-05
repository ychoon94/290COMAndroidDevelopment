package com.rwp.eboxsaloonapp.activities.staff;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.rwp.eboxsaloonapp.R;
import com.rwp.eboxsaloonapp.databinding.ActivityStaffJobBinding;
import com.rwp.eboxsaloonapp.databinding.FragmentCompletedAppointmentBinding;
import com.rwp.eboxsaloonapp.fragments.staffCompletedAppointment;
import com.rwp.eboxsaloonapp.fragments.staffScheduledAppointment;
import com.rwp.eboxsaloonapp.models.ViewPagerAdapter;

public class StaffJobActivity extends AppCompatActivity {

    private ActivityStaffJobBinding mStaffJobBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mStaffJobBinding = DataBindingUtil.setContentView(this, R.layout.activity_staff_job);

        mStaffJobBinding.toolbar.setTitle("Job");

        Intent intent = getIntent();
        String staffID = intent.getStringExtra("staffID");


        addTabs(mStaffJobBinding.viewPager);

        mStaffJobBinding.myTabLayout.setupWithViewPager(mStaffJobBinding.viewPager);
    }

    private void addTabs(ViewPager viewPager){
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

//        Fragment staffScheduledAppointmentFrag = new staffScheduledAppointment();
//        staffScheduledAppointmentFrag.setArguments(bundle);
//        adapter.addFrag(staffScheduledAppointmentFrag, "Scheduled");
        adapter.addFrag(new staffScheduledAppointment(), "Scheduled");

//        Fragment staffCompletedAppointmentFrag = new staffCompletedAppointment();
//        staffCompletedAppointmentFrag.setArguments(bundle);
//        adapter.addFrag(staffCompletedAppointmentFrag, "Completed");
        adapter.addFrag(new staffCompletedAppointment(), "Completed");
        mStaffJobBinding.viewPager.setAdapter(adapter);
    }
}