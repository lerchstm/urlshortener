package com.example.urlshortener;

import org.json.JSONArray;

// this interface extends the base interface and can handle a JSONArray respone
public interface VolleyArrayResponseListener extends VolleyResponseListener {
    void handleJSONArray(JSONArray response);
}
