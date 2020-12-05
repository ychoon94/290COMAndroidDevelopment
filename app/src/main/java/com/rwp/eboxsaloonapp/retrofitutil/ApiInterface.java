package com.rwp.eboxsaloonapp.retrofitutil;

import com.rwp.eboxsaloonapp.models.ApiResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiInterface {

    @FormUrlEncoded
    @POST("registerapp.php")
    Call<ApiResponse> performUserRegistration(@Field("user_name") String username,
                                              @Field("address") String address,@Field("phone") String phone,
                                              @Field("user_password") String password);

    @FormUrlEncoded
    @POST("staffloginapp.php")
    Call<ApiResponse> performStaffLogin(@Field("user_name") String username, @Field("user_password") String password);

    @FormUrlEncoded
    @POST("customerloginapp.php")
    Call<ApiResponse> performUserLogin(@Field("user_name") String username, @Field("user_password") String password);

    @FormUrlEncoded
    @POST("staffappointmentapp_detail.php")
    Call<ApiResponse> getStaffAppointmentDetail(@Field("id") String appointmentID);

    @FormUrlEncoded
    @POST("makenewappointment.php")
    Call<ApiResponse> insertNewAppointment(@Field("staffid") String staff, @Field("service") String service,
                                           @Field("name") String custname, @Field("date") String date,
                                           @Field("time") String time,
                                           @Field("phone") String custphone, @Field("location") String location);

    @FormUrlEncoded
    @POST("getservicestaffphone.php")
    Call<ApiResponse> getServiceStaffPhone(@Field("staffid") String staffID);

    @FormUrlEncoded
    @POST("customermarkservicecompletion.php")
    Call<ApiResponse> markServiceCompletion(@Field("appointmentID") String appointmentID, @Field("appointmentStatus") String appointmentStatus);

    @FormUrlEncoded
    @POST("customerprofile.php")
    Call<ApiResponse> getCustomerProfile(@Field("customerName") String customerName);
}