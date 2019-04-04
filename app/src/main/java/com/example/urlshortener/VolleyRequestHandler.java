package com.example.urlshortener;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class VolleyRequestHandler {
        // set 10s response timeout
        private static final int MY_SOCKET_TIMEOUT_MS = 10000;

        // the RequestQueue is used to put and perform the VolleyRequests
        private static RequestQueue requestQueue;

        // the response listener implements the response methods of the requests
        private VolleyResponseListener listener;

        // define a unique name as a tag for logging etc.
        private static final String TAG = VolleyRequestHandler.class.getSimpleName();


        /**
         * creates the RequestQueue with the App Context
         *
         * @param context  the application context to bind the RequestQueue
         * @param listener the Listener to handle all the responses of the different requests
         */
        public VolleyRequestHandler(Context context, VolleyResponseListener listener) {
            // get the RequestQueue
            // remember: you can create as many Controllers as you like but
            // - the RequestQueueSingleton always returns the same RequestQueue object
            // - and the RequestQueueSingleton exists only once
            requestQueue = RequestQueueSingleton.getInstance(context).getRequestQueue();
            this.listener = listener;
            Log.d(TAG, "Listener set to " + listener.getClass());
        }


        public void registerUser(String email, String firstName, String lastName, String password) throws JSONException {
            Log.d(TAG, "Building JSONObject ...");

            JSONObject json = new JSONObject();

            json.put("email", email);
            json.put("firstName", firstName);
            json.put("lastName", lastName);
            json.put("password", password);

            JsonObjectRequest request = createRequest(Request.Method.POST, URLs.URL_REGISTER, json, listener, null);

            requestQueue.add(request);
            Log.d(TAG, "JSON Request started ...");
        }


        public void loginUser(String email, String password) throws JSONException {
            Log.d(TAG, "Building JSONObject ...");

            JSONObject json = new JSONObject();
            json.put("email", email);
            json.put("password", password);

            JsonObjectRequest request = createRequest(Request.Method.POST, URLs.URL_LOGIN, json, listener, null);

            requestQueue.add(request);
            Log.d(TAG, "JSON Request started ...");
        }


        /*public void logoutUser(User user) throws JSONException {
            JsonObjectRequest request = createRequest(Request.Method.POST, URLs.URL_LOGOUT, null, listener, user);
            requestQueue.add(request);
            Log.d(TAG, "JSON Request started ...");
        }*/

        // the createRequest method creates JsonObjectRequest incl. assigned response listener, authentication parameters and other settings
        private JsonObjectRequest createRequest(int method, String url, JSONObject json, VolleyResponseListener listener, User user) {
            VolleyLog.DEBUG = true; // in the terminal turn on verbose logging with: "adb shell setprop log.tag.Volley VERBOSE"
            if (json != null) {
                Log.d(TAG, "JSONObject: " + json.toString());
            }
            JsonObjectRequest request = new JsonObjectRequest(method, url, json,
                    response -> {
                        Log.d(TAG, "JSON received: " + response);
                        listener.handleResponse(response);
                    },
                    error -> {
                        Log.d(TAG, "Error: " + error.getMessage());
                        listener.handleError(error);
                    }
            )
            {
                // define the http header entries for json and authentication
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    headers.put("Accept", "application/json");
                    if (user != null) {
                        headers.put("Authorization", "Bearer " + user.getJwt());
                    }
                    return headers;
                }


                // change the response parsing to allow empty response strings
                @Override
                protected Response parseNetworkResponse(NetworkResponse response) {
                    try {
                        String jsonString = new String(response.data,
                                HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));
                        // allow null (=empty response)
                        if (jsonString == null || jsonString.length() == 0) {
                            return Response.success(null, HttpHeaderParser.parseCacheHeaders(response));
                        }
                        return Response.success(new JSONObject(jsonString),
                                HttpHeaderParser.parseCacheHeaders(response));
                    } catch (UnsupportedEncodingException e) {
                        return Response.error(new ParseError(e));
                    } catch (JSONException je) {
                        return Response.error(new ParseError(je));
                    }
                }
            };

            // set the retry timeout to MY_SOCKET_TIMEOUT_MS (=10000ms)
            request.setRetryPolicy(new DefaultRetryPolicy(
                    MY_SOCKET_TIMEOUT_MS,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            return request;
        }

        private JsonArrayRequest createArrayRequest(int method, String url, JSONArray json, VolleyArrayResponseListener listener, User user){
            JsonArrayRequest request = new JsonArrayRequest(method, url, json,
                    response -> {
                        Log.d(TAG, "JSON received: " + response);
                        listener.handleJSONArray(response);
                    },
                    error -> {
                        Log.d(TAG, "Error: " + error.getMessage());
                        listener.handleError(error);
                    }
            )
            {
                // define the http header entries for json and authentication
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    headers.put("Accept", "application/json");
                    if (user != null) {
                        headers.put("Authorization", "Bearer " + user.getJwt());
                    }
                    return headers;
                }
            };

            // set the retry timeout to MY_SOCKET_TIMEOUT_MS (=10000ms)
            request.setRetryPolicy(new DefaultRetryPolicy(
                    MY_SOCKET_TIMEOUT_MS,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            return request;
        }

        // gets all Shortend URLs objects as JSON array
        public void getURLsShortend(User user) {
            JsonArrayRequest request = new JsonArrayRequest(URLs.URL_SHORTCUT,
                    response -> {
                        Log.d(TAG, "JSONArray received: " + response.toString());
                        if (listener instanceof VolleyArrayResponseListener) {
                            // use the second, derived listener interface with the handleJSONArray method
                            VolleyArrayResponseListener entryListener = (VolleyArrayResponseListener) listener;
                            entryListener.handleJSONArray(response);
                        } else {
                            VolleyLog.d(TAG, "Error: Wrong listener class");
                            listener.handleError(new Exception("Error: Wrong listener class"));
                        }
                    },
                    error -> {
                        VolleyLog.d(TAG, "getLogEntries Error: " + error.getMessage());
                        listener.handleError(error);
                    }
            ) {

                // set the http header
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    headers.put("Accept", "application/json");
                    if (user != null) {
                        headers.put("Authorization", "Bearer " + user.getJwt());
                    }
                    return headers;
                }

            };

            // set the timeout
            request.setRetryPolicy(new DefaultRetryPolicy(
                    MY_SOCKET_TIMEOUT_MS,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            requestQueue.add(request);
            VolleyLog.d(TAG, "JSONArray Request started ...");
        }

        public void deleteUrlShortend(User user, int id) {
            JsonObjectRequest request = createRequest(Request.Method.DELETE, URLs.URL_SHORTCUT + "/" + id, null, listener, user);

            requestQueue.add(request);
            Log.d(TAG, "JSON Delete Request started ...");
        }

        public void getURLShortend(User user, int id) {
            JsonObjectRequest request = createRequest(Request.Method.GET, URLs.URL_SHORTCUT + "/" + id, null, listener, user);

            requestQueue.add(request);
            Log.d(TAG, "JSON Get Request started ...");
        }

        public void updateURLShortend(User user, URLShortend urlShortend) throws JSONException {
            Log.d(TAG, "Building JSONObject ...");

            JSONObject json = new JSONObject();
            json.put("shortIdentifier", urlShortend.getShortIdentifier());
            json.put("redirectURL", urlShortend.getRedirectURL());
            json.put("redirectStatus", urlShortend.getRedirectStatus());
            json.put("validThru", urlShortend.getValidThru());

            JsonObjectRequest request = createRequest(Request.Method.PUT, URLs.URL_SHORTCUT + "/" + urlShortend.getId(), json, listener, user);
            requestQueue.add(request);
            Log.d(TAG, "JSON Update Request started ...");
        }

        public void storeURLShortend(User user, URLShortend urlShortend) throws JSONException {
            Log.d(TAG, "Building JSONObject ...");

            JSONObject json = new JSONObject();
            json.put("shortIdentifier", urlShortend.getShortIdentifier());
            json.put("redirectURL", urlShortend.getRedirectURL());
            json.put("redirectStatus", urlShortend.getRedirectStatus());
            json.put("validThru", urlShortend.getValidThru());

            JsonObjectRequest request = createRequest(Request.Method.POST, URLs.URL_SHORTCUT, json, listener, user);
            requestQueue.add(request);
            Log.d(TAG, "JSON Save Request started ...");
        }
    }
