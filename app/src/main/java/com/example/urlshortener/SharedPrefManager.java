package com.example.urlshortener;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {
    //the constants
    private static final String SHARED_PREF_NAME = "USER_PREF";
    private static final String KEY_FIRSTNAME = "FIRSTNAME";
    private static final String KEY_LASTNAME = "LASTNAME";
    private static final String KEY_EMAIL = "EMAIL";
    private static final String KEY_JWT = "JWT";

    private static SharedPrefManager instance;
    private static Context context;

    private SharedPrefManager(Context context) {
        this.context = context;
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPrefManager(context);
        }
        return instance;
    }

    //method to let the user login
    //this method will store the user data in shared preferences
    public void userLogin(User user) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_FIRSTNAME, user.getFirstName());
        editor.putString(KEY_LASTNAME, user.getLastName());
        editor.putString(KEY_EMAIL, user.getEmail());
        editor.putString(KEY_JWT, user.getJwt());
        editor.apply();
    }

    //this method will check whether the user is already logged in or not
    public boolean isLoggedIn() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_EMAIL, null) != null;
    }

    //this method will give the logged in user
    public User getUser() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);

        if (sharedPreferences.getString(KEY_EMAIL, null) == null) {
            return null;
        }
        String firstname = sharedPreferences.getString(KEY_FIRSTNAME, null);
        String lastname = sharedPreferences.getString(KEY_LASTNAME, null);
        String email = sharedPreferences.getString(KEY_EMAIL, null);
        String jwt = sharedPreferences.getString(KEY_JWT, null);
        return new User( email, jwt, firstname, lastname);
    }

    //this method will logout the user
    public void logout() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

}
