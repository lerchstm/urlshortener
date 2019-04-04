package com.example.urlshortener;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.urlshortener.databinding.ActivityCreateUrlshortendBinding;
import com.example.urlshortener.databinding.ActivityOverviewBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class CreateURLShortendActivity extends AppCompatActivity implements VolleyResponseListener {

    private ActivityCreateUrlshortendBinding binding;
    private ProgressBar pBar;

    String dateTime = "";
    int mYear;
    int mMonth;
    int mDay;
    int mHour;
    int mMinute;
    TimeZone timezone;

    URLShortend urlShortend;

    VolleyRequestHandler volleyRequestHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_urlshortend);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_urlshortend);
        User user = SharedPrefManager.getInstance(this.getApplicationContext()).getUser();


        volleyRequestHandler = new VolleyRequestHandler(this.getApplicationContext(), this);



        binding.buttonCreate.setOnClickListener(view -> {
            try{
                String shortIdentifier = binding.editBoxShortIdentifier.getText().toString();
                String redirectURL = binding.editTextRedirectURL.getText().toString();
                int redirectStatus = Integer.parseInt(binding.editBoxRedirectStatus.getText().toString());

                urlShortend = new URLShortend();
                urlShortend.setRedirectURL(redirectURL);
                urlShortend.setShortIdentifier(shortIdentifier);
                urlShortend.setRedirectStatus(redirectStatus);
                urlShortend.setValidThru(dateTime);

                pBar = binding.progressBar;
                pBar.setVisibility(View.VISIBLE);

                volleyRequestHandler.storeURLShortend(user, urlShortend);
            }catch (JSONException ex) {
                Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
            }
            catch (Exception ex){
                Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        binding.editBoxValidThru.setKeyListener(null);
        binding.editBoxValidThru.setOnClickListener(v -> {
            datePicker();
        });

    }

    @Override
    public void handleError(Exception ex) {

    }

    @Override
    public void handleResponse(JSONObject response) {
        pBar.setVisibility(View.INVISIBLE);

        try{
            if (response.getBoolean("success") && response.getString("message").equals("created")){
                finish();
            }
            else {
                Toast.makeText(this, "Code: " + response.getInt("code") + " message: " + response.getString("message"), Toast.LENGTH_LONG).show();
            }
        }catch (JSONException ex){
            Toast.makeText(this, "Error login:" + ex.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    private void datePicker(){
        final Calendar myCalendar = Calendar.getInstance();
        mYear = myCalendar.get(Calendar.YEAR);
        mMonth = myCalendar.get(Calendar.MONTH);
        mDay = myCalendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,int monthOfYear, int dayOfMonth) {

                        dateTime = year + "-" + String.format("%02d",(monthOfYear + 1)) + "-" + String.format("%02d", dayOfMonth);
                        //*************Call Time Picker Here ********************
                        timePicker();
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private void timePicker(){
        final Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT"), Locale.getDefault());
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        mHour = hourOfDay;
                        mMinute = minute;

                        Date currentLocalTime = c.getTime();
                        DateFormat date = new SimpleDateFormat("ZZZZZ",Locale.getDefault());
                        String localTime = date.format(currentLocalTime);

                        binding.editBoxValidThru.setText(dateTime + " " + String.format("%02d", hourOfDay) + ":" + String.format("%02d",minute));
                        dateTime += "T"+ String.format("%02d", hourOfDay) + ":" + String.format("%02d",minute) + ":00.000" + localTime;
                    }
                }, mHour, mMinute, true);
        timePickerDialog.show();

    }
}
