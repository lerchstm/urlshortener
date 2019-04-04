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

import com.example.urlshortener.databinding.ActivityEditUrlshortendBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class EditURLShortendActivity extends AppCompatActivity implements VolleyResponseListener {

    private ActivityEditUrlshortendBinding binding;
    private ProgressBar pBar;
    private URLShortend urlShortend;

    String dateTime = "";
    int mYear;
    int mMonth;
    int mDay;
    int mHour;
    int mMinute;
    int id;
    TimeZone timezone;

    VolleyRequestHandler volleyRequestHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_urlshortend);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_urlshortend);
        User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();
        pBar = binding.progressBar;

        Intent i = getIntent();
        id = i.getIntExtra("ID", -1);

        if(id != -1){
            pBar.setVisibility(View.VISIBLE);
            volleyRequestHandler = new VolleyRequestHandler(this.getApplicationContext(), this);
            volleyRequestHandler.getURLShortend(user, id);
        }

        binding.editBoxID.setKeyListener(null);
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
        try{
            if(response.has("message")){
                if(response.get("message").equals("updated")){
                    finish();
                }
            }
            else {
                pBar.setVisibility(View.INVISIBLE);
                binding.editBoxID.setText(Integer.toString(response.getInt("id")));
                binding.editBoxShortIdentifier.setText(response.getString("shortIdentifier"));
                binding.editTextRedirectURL.setText(response.getString("redirectURL"));
                binding.editBoxRedirectStatus.setText(Integer.toString(response.getInt("redirectStatus")));

                String responseDateTime = response.getString("validThru");
                String responseDate = responseDateTime.substring(0,10);
                String responseTime = responseDateTime.substring(11,16);
                binding.editBoxValidThru.setText(responseDate + " " + responseTime);
            }


        }catch (JSONException ex){
            Toast.makeText(this, "Message: " + ex.getMessage(), Toast.LENGTH_LONG).show();
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
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

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


    public void updateURLShortcut(View view) {
        try {


            User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();

            int id = Integer.parseInt(binding.editBoxID.getText().toString());
            String shortIdentifier = binding.editBoxShortIdentifier.getText().toString();
            String redirectURL = binding.editTextRedirectURL.getText().toString();
            int redirectStatus = Integer.parseInt(binding.editBoxRedirectStatus.getText().toString());
            String date = binding.editBoxValidThru.getText().toString();

            urlShortend = new URLShortend();
            urlShortend.setId(id);
            urlShortend.setRedirectURL(redirectURL);
            urlShortend.setShortIdentifier(shortIdentifier);
            urlShortend.setRedirectStatus(redirectStatus);
            urlShortend.setValidThru(dateTime);

            volleyRequestHandler.updateURLShortend(user, urlShortend);

        }catch (JSONException ex){
            Toast.makeText(this, "message: " + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
