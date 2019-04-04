package com.example.urlshortener;

import android.app.ProgressDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.urlshortener.databinding.ActivityRegisterBinding;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity implements VolleyResponseListener{

    public static final String TAG = RegisterActivity.class.getSimpleName();

    private ActivityRegisterBinding binding;
    ProgressBar pBar;

    VolleyRequestHandler volleyRequestHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_register);

        volleyRequestHandler = new VolleyRequestHandler(this.getApplicationContext(), this);

        binding.btnRegister.setOnClickListener(view -> {
            try {
                String firstName = binding.editTextfirstName.getText().toString();
                String lastName = binding.editTextlastName.getText().toString();
                String email = binding.editTextEmail.getText().toString();
                String password = binding.editTextPassword.getText().toString();

                pBar = binding.progressBar;
                pBar.setVisibility(View.VISIBLE);
               volleyRequestHandler.registerUser(email, firstName, lastName, password);
            }catch (JSONException ex) {
                Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
            } catch (Exception ex){
                Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
            }

        });

        binding.textViewLogin.setOnClickListener( view -> {
            //open login activity
            finish();
            startActivity(new Intent(this.getApplicationContext(), MainActivity.class));
        });
    }

    @Override
    public void handleError(Exception ex) {
        pBar.setVisibility(View.INVISIBLE);

    }

    @Override
    public void handleResponse(JSONObject response) {
        Log.d(TAG, response.toString());
        pBar.setVisibility(View.INVISIBLE);

        try{
            if (response.getBoolean("success") && response.getString("message").equals("registerd")){
                String email = response.getString("email");
                String firstName = response.getString("firstName");
                String jwt = response.getString("jwt");
                String lastName = response.getString("lastName");

                User user = new User(email, jwt, firstName, lastName);
                SharedPrefManager.getInstance(this.getApplicationContext()).userLogin(user);
                finish();
                startActivity(new Intent(this.getApplicationContext(), OverviewActivity.class));
            }
            else{
                Toast.makeText(this,"Couldn't register", Toast.LENGTH_LONG).show();
            }

        }catch (Exception ex){
            Toast.makeText(this,"Error receiving registration result: " + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
