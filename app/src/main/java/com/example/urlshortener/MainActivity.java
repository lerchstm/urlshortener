package com.example.urlshortener;

import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.urlshortener.databinding.ActivityMainBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;

public class MainActivity extends AppCompatActivity implements VolleyResponseListener{

    public static final String TAG = MainActivity.class.getSimpleName();

    private ActivityMainBinding binding;
    private ProgressBar pBar;

    VolleyRequestHandler volleyRequestHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (SharedPrefManager.getInstance(this.getApplicationContext()).isLoggedIn()) {
            finish();
            startActivity(new Intent(this.getApplicationContext(), OverviewActivity.class));
        }


        //bind Layout so you don't need to use findViewById
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        volleyRequestHandler = new VolleyRequestHandler(this.getApplicationContext(), this);

        binding.buttonLogin.setOnClickListener(view -> {
            try {
                String email = binding.editTextEmail.getText().toString();
                String password = binding.editTextPassword.getText().toString();

                pBar = binding.progressBar;
                pBar.setVisibility(View.VISIBLE);
                volleyRequestHandler.loginUser(email, password);
            }catch (JSONException ex){
                Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
            }catch (Exception ex){
                Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
            }

        });

        binding.textViewRegister.setOnClickListener( view -> {
            //open register activity
            finish();
            startActivity(new Intent(this.getApplicationContext(), RegisterActivity.class));
        });


    }

    @Override
    public void handleError(Exception ex) {

    }

    @Override
    public void handleResponse(JSONObject response) {
        Log.d(TAG, response.toString());
        pBar.setVisibility(View.INVISIBLE);

        try{
            if (response.getBoolean("success")){
                String email = response.getString("email");
                String firstName = response.getString("firstName");
                String jwt = response.getString("jwt");
                String lastName = response.getString("lastName");

                User user = new User(email, jwt, firstName, lastName);
                SharedPrefManager.getInstance(this.getApplicationContext()).userLogin(user);
                finish();
                startActivity(new Intent(this.getApplicationContext(), OverviewActivity.class));
            }
            else
            {
                Toast.makeText(this, "Code: " + response.getInt("code") + "\n" + response.getString("message") , Toast.LENGTH_LONG).show();
            }

        } catch (Exception ex) {
            Toast.makeText(this, "Error login:" + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
