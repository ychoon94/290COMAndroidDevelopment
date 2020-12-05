package com.rwp.eboxsaloonapp.activities.customer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;

import com.rwp.eboxsaloonapp.R;
import com.rwp.eboxsaloonapp.databinding.ActivityCustomerCheckAppointmentBinding;
import com.rwp.eboxsaloonapp.fragments.customerCompletedAppointment;
import com.rwp.eboxsaloonapp.fragments.customerCurrentAppointment;
import com.rwp.eboxsaloonapp.fragments.staffCompletedAppointment;
import com.rwp.eboxsaloonapp.fragments.staffScheduledAppointment;
import com.rwp.eboxsaloonapp.models.ViewPagerAdapter;

public class CustomerCheckAppointment extends AppCompatActivity {

    private ActivityCustomerCheckAppointmentBinding mAppointmentBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_customer_check_appointment);

        mAppointmentBinding = DataBindingUtil.setContentView(this, R.layout.activity_customer_check_appointment);

        mAppointmentBinding.toolbar.setTitle("Appointment");

        Intent intent = getIntent();
        String custName = intent.getStringExtra("custname");

        addTabs(mAppointmentBinding.viewPager);

        mAppointmentBinding.myTabLayout.setupWithViewPager(mAppointmentBinding.viewPager);
    }

    private void addTabs(ViewPager viewPager){
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

//        Fragment staffScheduledAppointmentFrag = new staffScheduledAppointment();
//        staffScheduledAppointmentFrag.setArguments(bundle);
//        adapter.addFrag(staffScheduledAppointmentFrag, "Scheduled");
        adapter.addFrag(new customerCurrentAppointment(), "Current");

//        Fragment staffCompletedAppointmentFrag = new staffCompletedAppointment();
//        staffCompletedAppointmentFrag.setArguments(bundle);
//        adapter.addFrag(staffCompletedAppointmentFrag, "Completed");
        adapter.addFrag(new customerCompletedAppointment(), "Completed");
        mAppointmentBinding.viewPager.setAdapter(adapter);
    }
}