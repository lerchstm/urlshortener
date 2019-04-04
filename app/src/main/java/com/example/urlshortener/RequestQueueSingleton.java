package com.example.urlshortener;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class RequestQueueSingleton {
    // the single RequestQueue that is managed by the RequestQueueSingleton
    private RequestQueue requestQueue;

    // remembered instance to ensure that it cannot be created twice
    private static RequestQueueSingleton requestQueueSingleton;


    /**
     * Hidden private constructor. called from getInstance only and creates one single RequestQueue
     * @param context
     */
    private RequestQueueSingleton(Context context) {
        requestQueue = Volley.newRequestQueue(context.getApplicationContext());
    }


    /**
     * Creates a singleton RequestQueueSingleton object bound to the App Context.
     *
     * @param context
     * @return
     */
    public static synchronized RequestQueueSingleton getInstance(Context context) {

        // the remembered single instance is created once and one-time only
        if (requestQueueSingleton == null) {
            requestQueueSingleton = new RequestQueueSingleton(context);
        }

        return requestQueueSingleton;
    }

    /**
     * returns a RequestQueue object that exists only once per App and is bound to the App itself.
     * @return
     */
    public RequestQueue getRequestQueue() {
        return requestQueue;
    }
}
