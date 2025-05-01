package com.example.mobileapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileapp.adapters.EventAdapter;
import com.example.mobileapp.api.ApiClient;
import com.example.mobileapp.api.ApiService;
import com.example.mobileapp.models.Event;
import com.example.mobileapp.models.SearchFilter;
import com.example.mobileapp.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchResultsActivity extends AppCompatActivity implements EventAdapter.OnEventClickListener {
    private static final String TAG = "SearchResultsActivity";
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
            // Check if we have individual filter parameters
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                filter = new SearchFilter();
                if (extras.containsKey("title")) {
                    filter.setTitle(extras.getString("title"));
                }
                if (extras.containsKey("place")) {
                    filter.setPlace(extras.getString("place"));
                }
                if (extras.containsKey("category")) {
                    filter.setCategory(extras.getString("category"));
                }
                if (extras.containsKey("startDate")) {
                    filter.setStartDate(extras.getString("startDate"));
                }
                if (extras.containsKey("endDate")) {
                    filter.setEndDate(extras.getString("endDate"));
                }
            } else {
                Toast.makeText(this, "No search criteria provided", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
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

    // Update the searchEvents method to better handle errors and provide more robust fallback
    private void searchEvents() {
        progressBar.setVisibility(View.VISIBLE);
        noResultsText.setVisibility(View.GONE);

        // Log the search parameters
        LogUtils.debug("Searching with parameters: " +
                "title=" + filter.getTitle() +
                ", place=" + filter.getPlace() +
                ", category=" + filter.getCategory() +
                ", startDate=" + filter.getStartDate() +
                ", endDate=" + filter.getEndDate());

        // Check if we're searching by category only
        if (filter.getCategory() != null && !filter.getCategory().isEmpty() &&
                filter.getTitle().isEmpty() && filter.getPlace().isEmpty() &&
                filter.getStartDate().isEmpty() && filter.getEndDate().isEmpty()) {

            // For category-only searches, use getAllEvents and filter locally
            // This is more reliable than the search endpoint for simple category filtering
            apiService.getAllEvents().enqueue(new Callback<List<Event>>() {
                @Override
                public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                    progressBar.setVisibility(View.GONE);

                    if (response.isSuccessful() && response.body() != null) {
                        List<Event> allEvents = response.body();
                        eventList.clear();

                        // Filter by category
                        for (Event event : allEvents) {
                            if (event.getCategory().equalsIgnoreCase(filter.getCategory())) {
                                eventList.add(event);
                            }
                        }

                        eventAdapter.notifyDataSetChanged();

                        if (eventList.isEmpty()) {
                            noResultsText.setVisibility(View.VISIBLE);
                        }

                        LogUtils.debug("Category search returned " + eventList.size() + " results");
                    } else {
                        LogUtils.error("Failed to get events for category search");
                        createMockSearchResults();
                    }
                }

                @Override
                public void onFailure(Call<List<Event>> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    LogUtils.error("Network error in category search: " + t.getMessage());
                    createMockSearchResults();
                }
            });
        } else {
            // Use the search endpoint for more complex searches
            apiService.searchEvents(
                    filter.getTitle(),
                    filter.getPlace(),
                    filter.getCategory(),
                    filter.getStartDate(),
                    filter.getEndDate()
            ).enqueue(new Callback<List<Event>>() {
                @Override
                public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                    progressBar.setVisibility(View.GONE);

                    if (response.isSuccessful() && response.body() != null) {
                        eventList.clear();
                        eventList.addAll(response.body());
                        eventAdapter.notifyDataSetChanged();

                        LogUtils.debug("Search returned " + eventList.size() + " results");

                        if (eventList.isEmpty()) {
                            noResultsText.setVisibility(View.VISIBLE);
                        }
                    } else {
                        LogUtils.error("Failed to search events. Response code: " + response.code());
                        if (response.errorBody() != null) {
                            try {
                                LogUtils.error("Error body: " + response.errorBody().string());
                            } catch (Exception e) {
                                LogUtils.error("Error reading error body: " + e.getMessage());
                            }
                        }

                        // Fall back to mock search results
                        createMockSearchResults();
                    }
                }

                @Override
                public void onFailure(Call<List<Event>> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    LogUtils.error("Network error searching events: " + t.getMessage());

                    // Fall back to mock search results
                    createMockSearchResults();
                }
            });
        }
    }

    private void createMockSearchResults() {
        // Create mock events based on the search filter
        eventList.clear();

        // Get all events from MainActivity's data
        apiService.getAllEvents().enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Event> allEvents = response.body();

                    // Filter events based on search criteria
                    for (Event event : allEvents) {
                        boolean matches = true;

                        if (filter.getTitle() != null && !filter.getTitle().isEmpty()) {
                            if (!event.getTitle().toLowerCase().contains(filter.getTitle().toLowerCase())) {
                                matches = false;
                            }
                        }

                        if (filter.getPlace() != null && !filter.getPlace().isEmpty() && !filter.getPlace().equals("All Venues")) {
                            if (!event.getVenue().toLowerCase().contains(filter.getPlace().toLowerCase())) {
                                matches = false;
                            }
                        }

                        if (filter.getCategory() != null && !filter.getCategory().isEmpty() && !filter.getCategory().equals("All Categories")) {
                            if (!event.getCategory().equalsIgnoreCase(filter.getCategory())) {
                                matches = false;
                            }
                        }

                        // Add date filtering if needed

                        if (matches) {
                            eventList.add(event);
                        }
                    }

                    eventAdapter.notifyDataSetChanged();

                    if (eventList.isEmpty()) {
                        noResultsText.setVisibility(View.VISIBLE);
                    }

                    Log.d(TAG, "Mock search returned " + eventList.size() + " results");
                } else {
                    noResultsText.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                noResultsText.setVisibility(View.VISIBLE);
                Log.e(TAG, "Failed to get mock search results: " + t.getMessage());
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
