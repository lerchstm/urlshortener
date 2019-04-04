package com.example.urlshortener;

import com.android.volley.VolleyError;

import org.json.JSONObject;

interface VolleyResponseListener {
    void handleError(Exception ex);
    void handleResponse(JSONObject response);
}
