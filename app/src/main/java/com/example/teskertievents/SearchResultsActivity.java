package com.example.teskertievents;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teskertievents.adapters.EventAdapter;
import com.example.teskertievents.api.ApiClient;
import com.example.teskertievents.api.ApiService;
import com.example.teskertievents.models.Event;
import com.example.teskertievents.models.SearchFilter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchResultsActivity extends AppCompatActivity implements EventAdapter.OnEventClickListener {
    private RecyclerView recyclerView;
    private EventAdapter eventAdapter;
    private List<Event> eventList;
    private ProgressBar progressBar;
    private TextView noResultsText;
    private ApiService apiService;
    private SearchFilter filter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Search Results");

        // Initialize views
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        noResultsText = findViewById(R.id.noResultsText);

        // Get filter from intent
        filter = (SearchFilter) getIntent().getSerializableExtra("filter");
        if (filter == null) {
            finish();
            return;
        }

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventList = new ArrayList<>();
        eventAdapter = new EventAdapter(this, eventList, this);
        recyclerView.setAdapter(eventAdapter);

        // Initialize API service
        apiService = ApiClient.getClient().create(ApiService.class);

        // Search events
        searchEvents();
    }

    private void searchEvents() {
        progressBar.setVisibility(View.VISIBLE);
        noResultsText.setVisibility(View.GONE);

        // Create filter map
        Map<String, String> filterMap = new HashMap<>();
        if (filter.getTitle() != null) filterMap.put("title", filter.getTitle());
        if (filter.getPlace() != null) filterMap.put("place", filter.getPlace());
        if (filter.getCategory() != null) filterMap.put("category", filter.getCategory());
        if (filter.getStartDate() != null) filterMap.put("startDate", filter.getStartDate());
        if (filter.getEndDate() != null) filterMap.put("endDate", filter.getEndDate());

        apiService.searchEvents(filterMap).enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    eventList.clear();
                    eventList.addAll(response.body());
                    eventAdapter.notifyDataSetChanged();

                    if (eventList.isEmpty()) {
                        noResultsText.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(SearchResultsActivity.this, "Failed to search events", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(SearchResultsActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onEventClick(Event event) {
        Intent intent = new Intent(this, EventDetailsActivity.class);
        intent.putExtra("event", event);
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
