package com.rwp.eboxsaloonapp.models;

import com.google.gson.annotations.SerializedName;

public class ApiResponse {

    @SerializedName("status")
    private String status;

    @SerializedName("result_code")
    private int resultCode;

    @SerializedName("user_name")
    private String username;

    public String getStatus() {
        return status;
    }

    public int getResultCode() {
        return resultCode;
    }

    public String getUsername() {
        return username;
    }

    @SerializedName("id")
    private String id;

    @SerializedName("staffid")
    private String staffid;

    @SerializedName("service")
    private String service;

    @SerializedName("custname")
    private String custname;

    @SerializedName("custphone")
    private String custphone;

    @SerializedName("appointment_date")
    private String appointment_date;

    @SerializedName("appointment_time")
    private String appointment_time;

    @SerializedName("appointment_location")
    private String appointment_location;

    @SerializedName("appointment_status")
    private String appointment_status;

    @SerializedName("staffphone")
    private String staffphone;

    @SerializedName("address")
    private String address;

    @SerializedName("phone")
    private String phone;

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }
    public String getStaff_phone() {
        return staffphone;
    }

    public String getId() {
        return id;
    }

    public String getStaffid() {
        return staffid;
    }

    public String getService() {
        return service;
    }

    public String getCustname() {
        return custname;
    }

    public String getCustphone() {
        return custphone;
    }

    public String getAppointment_date() {
        return appointment_date;
    }

    public String getAppointment_time() {
        return appointment_time;
    }

    public String getAppointment_location() {
        return appointment_location;
    }

    public String getAppointment_status() {
        return appointment_status;
    }
}
