package com.example.teskertievents;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.teskertievents.adapters.EventAdapter;
import com.example.teskertievents.api.ApiClient;
import com.example.teskertievents.api.ApiService;
import com.example.teskertievents.models.Event;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements EventAdapter.OnEventClickListener {
    private RecyclerView recyclerView;
    private EventAdapter eventAdapter;
    private List<Event> eventList;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("TeskertiEvents");

        // Initialize views
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventList = new ArrayList<>();
        eventAdapter = new EventAdapter(this, eventList, this);
        recyclerView.setAdapter(eventAdapter);

        // Initialize API service
        apiService = ApiClient.getClient().create(ApiService.class);

        // Setup swipe refresh
        swipeRefreshLayout.setOnRefreshListener(this::loadEvents);

        // Load events
        loadEvents();

        // Check if user is logged in
        checkLoginStatus();
    }

    private void loadEvents() {
        progressBar.setVisibility(View.VISIBLE);

        apiService.getAllEvents().enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {
                    eventList.clear();
                    eventList.addAll(response.body());
                    eventAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(MainActivity.this, "Failed to load events", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(MainActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkLoginStatus() {
        SharedPreferences prefs = getSharedPreferences("TeskertiPrefs", MODE_PRIVATE);
        String token = prefs.getString("auth_token", null);
        
        if (token != null) {
            ApiClient.setAuthToken(token);
        }
    }

    @Override
    public void onEventClick(Event event) {
        Intent intent = new Intent(this, EventDetailsActivity.class);
        intent.putExtra("event", event);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        
        if (id == R.id.action_search) {
            startActivity(new Intent(this, SearchFilterDialog.class));
            return true;
        } else if (id == R.id.action_profile) {
            // Check if user is logged in
            SharedPreferences prefs = getSharedPreferences("TeskertiPrefs", MODE_PRIVATE);
            String token = prefs.getString("auth_token", null);
            
            if (token != null) {
                startActivity(new Intent(this, ProfileActivity.class));
            } else {
                startActivity(new Intent(this, LoginActivity.class));
            }
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
}
