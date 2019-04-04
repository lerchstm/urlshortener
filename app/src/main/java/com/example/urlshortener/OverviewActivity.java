package com.example.urlshortener;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.TimeoutError;
import com.example.urlshortener.databinding.ActivityOverviewBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class OverviewActivity extends AppCompatActivity implements VolleyResponseListener, VolleyArrayResponseListener{

    public static final String TAG = OverviewActivity.class.getSimpleName();
    private VolleyRequestHandler volleyRequestHandler;
    private ActivityOverviewBinding binding;
    private ProgressBar pBar;
    private SwipeRefreshLayout swipeContainer;

    private RecyclerView recyclerView;
    private List<URLShortend> urlShortendList;
    private RecyclerView.Adapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this.getApplicationContext(), MainActivity.class));
            return;
        }
        User user = SharedPrefManager.getInstance(this.getApplicationContext()).getUser();

        setContentView(R.layout.activity_overview);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_overview);

        volleyRequestHandler = new VolleyRequestHandler(this.getApplicationContext(),this);

        recyclerView = binding.urlsShortendList;
        pBar = binding.progressBar;
        swipeContainer = binding.swipeContainer;

        swipeContainer.setOnRefreshListener(() -> {
            volleyRequestHandler.getURLsShortend(user);
        });

        urlShortendList = new ArrayList<>();
        adapter = new URLShortendAdapter(getApplicationContext(), urlShortendList, new URLShortendAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(URLShortend urlShortend) {
                volleyRequestHandler.deleteUrlShortend(user, urlShortend.getId());
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);


        pBar.setVisibility(View.VISIBLE);
        volleyRequestHandler.getURLsShortend(user);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_overview, menu);
        return true;
    }

    public void onMenuItemNewClick(MenuItem item) {
        Intent i = new Intent(getApplicationContext(), CreateURLShortendActivity.class);
        startActivity(i);
    }


    @Override
    public void handleError(Exception ex) {
        if (ex != null) {
            if (ex.getMessage() != null) {
                Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
                Log.d(TAG, ex.getMessage());
            }
            else if (ex instanceof AuthFailureError) {
                Toast.makeText(this, "Authenication Failure Error", Toast.LENGTH_LONG).show();
                Log.d(TAG, "Authenication Failure Error");
                SharedPrefManager.getInstance(this.getApplicationContext()).logout();
                finish();
                startActivity(new Intent(this, MainActivity.class));
            }
            else if (ex instanceof TimeoutError) {
                Toast.makeText(this, "Timeout Error", Toast.LENGTH_LONG).show();
                Log.d(TAG, "Timeout Error");
            }
        }

    }

    @Override
    public void handleResponse(JSONObject response) {
        User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();
        volleyRequestHandler.getURLsShortend(user);
    }

    public void onMenuItemRefreshClick(MenuItem item) {
        pBar.setVisibility(View.VISIBLE);
        User user = SharedPrefManager.getInstance(this.getApplicationContext()).getUser();
        volleyRequestHandler.getURLsShortend(user);
    }

    public void onMenuItemLogoutClick(MenuItem item) {
        SharedPrefManager.getInstance(this.getApplicationContext()).logout();
        finish();
        startActivity(new Intent(this, MainActivity.class));

    }

    @Override
    public void handleJSONArray(JSONArray response) {
        pBar.setVisibility(View.INVISIBLE);
        urlShortendList.clear();
        for (int i = 0; i < response.length(); i++) {
            try {
                JSONObject jsonObject = response.getJSONObject(i);

                URLShortend urlShortend = new URLShortend();
                urlShortend.setId(jsonObject.getInt("id"));
                urlShortend.setShortIdentifier(jsonObject.getString("shortIdentifier"));
                urlShortend.setRedirectURL(jsonObject.getString("redirectURL"));
                urlShortend.setRedirectStatus(jsonObject.getInt("redirectStatus"));
                urlShortend.setValidThru(jsonObject.getString("validThru"));
                urlShortendList.add(urlShortend);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        swipeContainer.setRefreshing(false);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        User user = SharedPrefManager.getInstance(this.getApplicationContext()).getUser();
        volleyRequestHandler.getURLsShortend(user);
    }
}
