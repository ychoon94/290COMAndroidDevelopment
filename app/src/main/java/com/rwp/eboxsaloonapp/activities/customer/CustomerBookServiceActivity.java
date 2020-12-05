package com.rwp.eboxsaloonapp.activities.customer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.rwp.eboxsaloonapp.R;
import com.rwp.eboxsaloonapp.databinding.ActivityCustomerBookServiceBinding;
import com.rwp.eboxsaloonapp.models.ApiResponse;
import com.rwp.eboxsaloonapp.retrofitutil.ApiClient;
import com.rwp.eboxsaloonapp.retrofitutil.ApiInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerBookServiceActivity extends AppCompatActivity implements  View.OnClickListener{

    private ActivityCustomerBookServiceBinding mBookServiceBinding;
    private String service;
    private String staff;

    private int mYear, mMonth, mDay, mHour, mMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_book_service);

        String apiKey = getString(R.string.api_key);

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(),apiKey);
        }

        PlacesClient placesClient = Places.createClient(this);


        mBookServiceBinding = DataBindingUtil.setContentView(this, R.layout.activity_customer_book_service);

        mBookServiceBinding.toolbar.setTitle("Book an Appointment");

        SharedPreferences mSharedPreferences = this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String restoredCustName = mSharedPreferences.getString("custName", null);

        getServiceTypeJSON("http://choonpi.myddns.me/applogin/servicetype.php");
        getStylistJSON("http://choonpi.myddns.me/applogin/staffname.php");

        mBookServiceBinding.btnDate.setOnClickListener(this);
        mBookServiceBinding.btnTime.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == mBookServiceBinding.btnDate) {

            // Get Current Date
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);


            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {

                            mBookServiceBinding.editTextDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        }
        if (v == mBookServiceBinding.btnTime) {

            // Get Current Time
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {

                            mBookServiceBinding.editTextTime.setText(hourOfDay + ":" + minute);
                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();
        }
    }

    //this method is actually fetching the json string
    private void getServiceTypeJSON(final String urlWebService) {
        /*
         * As fetching the json string is a network operation
         * And we cannot perform a network operation in main thread
         * so we need an AsyncTask
         * The constrains defined here are
         * Void -> We are not passing anything
         * Void -> Nothing at progress update as well
         * String -> After completion it should return a string and it will be the json string
         * */
        class GetJSON extends AsyncTask<Void, Void, String> {

            //this method will be called before execution
            //you can display a progress bar or something
            //so that user can understand that he should wait
            //as network operation may take some time
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            //this method will be called after execution
            //so here we are displaying a toast with the json string
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                try {
                    loadIntoServiceTypeRadioButton(s);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
            }

            //in this method we are fetching the json string
            @Override
            protected String doInBackground(Void... voids) {


                try {
                    //creating a URL
                    URL url = new URL(urlWebService);

                    //Opening the URL using HttpURLConnection
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();

                    //Sent POST data to httpURLConnection
//                    out = new BufferedOutputStream()

                    //StringBuilder object to read the string from the service
                    StringBuilder sb = new StringBuilder();

                    //We will use a buffered reader to read the string from service
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    //A simple string to read values from each line
                    String json;

                    //reading until we don't find null
                    while ((json = bufferedReader.readLine()) != null) {

                        //appending it to string builder
                        sb.append(json + "\n");
                    }

                    //finally returning the read string
                    con.disconnect();
                    return sb.toString().trim();
                } catch (Exception e) {
                    return null;
                }

            }
        }

        //creating asynctask object and executing it
        GetJSON getJSON = new GetJSON();
        getJSON.execute();
    }

    private void loadIntoServiceTypeRadioButton(String json) throws JSONException {
        //creating a json array from the json string
        JSONArray jsonArray = new JSONArray(json);

        //creating a string array for listview
        ArrayList<String> serviceType = new ArrayList<String>();
        ArrayList<String> servicePrice = new ArrayList<String>();
        //String[] appointmentID = new String[jsonArray.length()];

        //looping through all the elements in json array
        for (int i = 0; i < jsonArray.length(); i++) {

            //getting json object from the json array
            JSONObject obj = jsonArray.getJSONObject(i);

            //mBookServiceBinding.radioButton1.setText(obj.getString("id"));
            serviceType.add(obj.getString("type"));
            servicePrice.add(obj.getString("price"));
        }


        mBookServiceBinding.radioButton1.setText(serviceType.get(0));
        mBookServiceBinding.radioButton2.setText(serviceType.get(1));
        mBookServiceBinding.textViewPrice1.setText(servicePrice.get(0));
        mBookServiceBinding.textViewPrice2.setText(servicePrice.get(1));
    }

    public void setTotalPrice(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        if (mBookServiceBinding.radioButton1.isChecked()){
            mBookServiceBinding.textviewServicePrice.setText(mBookServiceBinding.textViewPrice1.getText().toString());
        }
        if (mBookServiceBinding.radioButton2.isChecked()){
            mBookServiceBinding.textviewServicePrice.setText(mBookServiceBinding.textViewPrice2.getText().toString());
        }
    }

    //this method is actually fetching the json string
    private void getStylistJSON(final String urlWebService) {
        /*
         * As fetching the json string is a network operation
         * And we cannot perform a network operation in main thread
         * so we need an AsyncTask
         * The constrains defined here are
         * Void -> We are not passing anything
         * Void -> Nothing at progress update as well
         * String -> After completion it should return a string and it will be the json string
         * */
        class GetJSON extends AsyncTask<Void, Void, String> {

            //this method will be called before execution
            //you can display a progress bar or something
            //so that user can understand that he should wait
            //as network operation may take some time
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            //this method will be called after execution
            //so here we are displaying a toast with the json string
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                try {
                    loadIntoStylistNameRadioButton(s);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
            }

            //in this method we are fetching the json string
            @Override
            protected String doInBackground(Void... voids) {


                try {
                    //creating a URL
                    URL url = new URL(urlWebService);

                    //Opening the URL using HttpURLConnection
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();

                    //StringBuilder object to read the string from the service
                    StringBuilder sb = new StringBuilder();

                    //We will use a buffered reader to read the string from service
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    //A simple string to read values from each line
                    String json;

                    //reading until we don't find null
                    while ((json = bufferedReader.readLine()) != null) {

                        //appending it to string builder
                        sb.append(json + "\n");
                    }

                    con.disconnect();
                    //finally returning the read string
                    return sb.toString().trim();
                } catch (Exception e) {
                    return null;
                }

            }
        }

        //creating asynctask object and executing it
        GetJSON getJSON = new GetJSON();
        getJSON.execute();
    }

    private void loadIntoStylistNameRadioButton(String json) throws JSONException {
        //creating a json array from the json string
        JSONArray jsonArray = new JSONArray(json);

        //creating a string array for listview
        ArrayList<String> staffName = new ArrayList<String>();
        ArrayList<String> staffID = new ArrayList<String>();
        //String[] appointmentID = new String[jsonArray.length()];

        //looping through all the elements in json array
        for (int i = 0; i < jsonArray.length(); i++) {

            //getting json object from the json array
            JSONObject obj = jsonArray.getJSONObject(i);

            //mBookServiceBinding.radioButton1.setText(obj.getString("id"));
            staffID.add(obj.getString("id"));
            staffName.add(obj.getString("username"));
        }

        mBookServiceBinding.textViewStaffid1.setText(staffID.get(0));
        mBookServiceBinding.textViewStaffid2.setText(staffID.get(1));
        mBookServiceBinding.radioButton3.setText(staffName.get(0));
        mBookServiceBinding.radioButton4.setText(staffName.get(1));
    }

    public void makeServiceAppointment(View view) {
        SharedPreferences mSharedPreferences = this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String restoredCustName = mSharedPreferences.getString("custName", null);

        //String status = "Ongoing";
        if (mBookServiceBinding.radioButton1.isChecked()){
            service = mBookServiceBinding.radioButton1.getText().toString();
        } else if (mBookServiceBinding.radioButton2.isChecked()){
            service = mBookServiceBinding.radioButton2.getText().toString();
        }
        if (mBookServiceBinding.radioButton3.isChecked()){
            staff = mBookServiceBinding.textViewStaffid1.getText().toString();
        } else if (mBookServiceBinding.radioButton4.isChecked()){
            staff = mBookServiceBinding.textViewStaffid2.getText().toString();
        }
        String custphone = mBookServiceBinding.editTextPhone2.getText().toString();
        String date = mBookServiceBinding.editTextDate.getText().toString();
        String time = mBookServiceBinding.editTextTime.getText().toString();
        String location = mBookServiceBinding.editTextTextPostalAddress2.getText().toString();

        Call<ApiResponse> call = ApiClient.getApiClient().create(ApiInterface.class).insertNewAppointment(staff, service, restoredCustName, date, time,  custphone, location);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.code() == 200){
                    Log.d("Getresponsecode", "response.code()");
                    if (response.body().getStatus().equals("ok")){
                        Log.d("Getstatus", response.body().getStatus());
                        if (response.body().getResultCode()==1){
                            Log.d("Getresult", "1");
                            Toast.makeText(CustomerBookServiceActivity.this, "Booking success.", Toast.LENGTH_LONG).show();
                            onBackPressed();
                            finish();
                        } else {
                            Toast.makeText(CustomerBookServiceActivity.this, "Failed, try again", Toast.LENGTH_SHORT).show();
                            Log.d("ERROR1", String.valueOf(response.body().getStatus()));
                            Log.d("Error1", staff);
                            //Log.d("Error1", service);
                            Log.d("Error1", restoredCustName);
                            //Log.d("Error1", status);
                            Log.d("Error1", custphone);
                            Log.d("Error1", location);
                            Log.d("Error1", time);
                            Log.d("Error1", date);
                        }
                    } else {
                        Log.d("ERROR2", String.valueOf(response.code()));
                    }
                } else {
                    Log.d("ERROR3", String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.d("huge error", "connection not established!!!");
            }
        });

    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void onSearchCalled(View view) {
        // Set the fields to specify which types of place data to return.
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME,
                Place.Field.ADDRESS, Place.Field.LAT_LNG);
        // Start the autocomplete intent.
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields).setCountry("MY") //Malaysia
                .build(this);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.i("TAG", "Place: " + place.getName() + ", " + place.getId() + ", "
                        + place.getAddress());
                Toast.makeText(CustomerBookServiceActivity.this, "ID: " + place.getId()
                        + "address:" + place.getAddress() + "Name:" + place.getName() + " latlong: "
                        + place.getLatLng(), Toast.LENGTH_LONG).show();
                String address = place.getAddress();
                mBookServiceBinding.editTextTextPostalAddress2.setText(address);
                // do query with address

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Toast.makeText(CustomerBookServiceActivity.this, "Error: "
                        + status.getStatusMessage(), Toast.LENGTH_LONG).show();
                Log.i("TAG", status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }
}
