package com.example.urlshortener;

import org.json.JSONException;
import org.json.JSONObject;

public class User {
    private String email, jwt, firstName, lastName;

    public User(String email, String jwt, String firstName, String lastName){
        this.email = email;
        this.jwt = jwt;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getJwt() {
        return jwt;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}
