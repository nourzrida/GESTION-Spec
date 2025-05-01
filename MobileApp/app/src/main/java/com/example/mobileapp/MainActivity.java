package com.example.mobileapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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

import com.example.mobileapp.adapters.CategoryAdapter;
import com.example.mobileapp.adapters.EventAdapter;
import com.example.mobileapp.adapters.FeaturedEventAdapter;
import com.example.mobileapp.api.ApiClient;
import com.example.mobileapp.api.ApiService;
import com.example.mobileapp.models.Category;
import com.example.mobileapp.models.Event;
import com.example.mobileapp.utils.LogUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements
        EventAdapter.OnEventClickListener,
        FeaturedEventAdapter.OnEventClickListener,
        CategoryAdapter.OnCategoryClickListener {

    private static final String TAG = "MainActivity";
    private RecyclerView recyclerView, featuredRecyclerView, categoriesRecyclerView;
    private EventAdapter eventAdapter;
    private FeaturedEventAdapter featuredEventAdapter;
    private CategoryAdapter categoryAdapter;
    private List<Event> eventList, featuredEventList;
    private List<Category> categoryList;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ApiService apiService;
    private FloatingActionButton searchFab, scheduleFab;

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
        featuredRecyclerView = findViewById(R.id.featuredRecyclerView);
        categoriesRecyclerView = findViewById(R.id.categoriesRecyclerView);
        progressBar = findViewById(R.id.progressBar);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        searchFab = findViewById(R.id.searchFab);
        scheduleFab = findViewById(R.id.scheduleFab);

        // Setup RecyclerViews
        // All events
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventList = new ArrayList<>();
        eventAdapter = new EventAdapter(this, eventList, this);
        recyclerView.setAdapter(eventAdapter);

        // Featured events
        LinearLayoutManager featuredLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        featuredRecyclerView.setLayoutManager(featuredLayoutManager);
        featuredEventList = new ArrayList<>();
        featuredEventAdapter = new FeaturedEventAdapter(this, featuredEventList, this);
        featuredRecyclerView.setAdapter(featuredEventAdapter);

        // Categories
        LinearLayoutManager categoriesLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        categoriesRecyclerView.setLayoutManager(categoriesLayoutManager);
        categoryList = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(this, categoryList, this);
        categoriesRecyclerView.setAdapter(categoryAdapter);

        // Initialize API service
        apiService = ApiClient.getClient().create(ApiService.class);

        // Setup swipe refresh
        swipeRefreshLayout.setOnRefreshListener(this::loadData);

        // Setup search FAB
        searchFab.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, SearchFilterDialog.class));
        });

        // Setup schedule FAB
        scheduleFab.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ScheduleActivity.class));
        });

        // Load data
        loadData();

        // Check if user is logged in
        checkLoginStatus();
    }

    private void loadData() {
        progressBar.setVisibility(View.VISIBLE);

        // Load events
        loadEvents();

        // Load categories
        loadCategoriesManually();

        // Load venues (for filtering)
        loadVenuesManually();
    }

    private void loadEvents() {
        apiService.getAllEvents().enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {
                    List<Event> allEvents = response.body();
                    Log.d(TAG, "Loaded " + allEvents.size() + " events");

                    // Filter featured events
                    featuredEventList.clear();
                    for (Event event : allEvents) {
                        if (event.isFeatured()) {
                            featuredEventList.add(event);
                        }
                    }
                    featuredEventAdapter.notifyDataSetChanged();
                    Log.d(TAG, "Featured events: " + featuredEventList.size());

                    // All events
                    eventList.clear();
                    eventList.addAll(allEvents);
                    eventAdapter.notifyDataSetChanged();
                } else {
                    Log.e(TAG, "Failed to load events. Response code: " + response.code());
                    if (response.errorBody() != null) {
                        try {
                            Log.e(TAG, "Error body: " + response.errorBody().string());
                        } catch (Exception e) {
                            Log.e(TAG, "Error reading error body: " + e.getMessage());
                        }
                    }
                    Toast.makeText(MainActivity.this, "Failed to load events", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                Log.e(TAG, "Network error loading events: " + t.getMessage(), t);
                Toast.makeText(MainActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadCategories() {
        apiService.getCategories().enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categoryList.clear();
                    List<String> categories = response.body();
                    Log.d(TAG, "Loaded categories: " + categories);

                    // Create category objects with icons
                    for (String category : categories) {
                        int iconResId = getCategoryIcon(category);
                        categoryList.add(new Category(category, iconResId));
                    }

                    categoryAdapter.notifyDataSetChanged();
                } else {
                    Log.e(TAG, "Failed to load categories. Response code: " + response.code());
                    if (response.errorBody() != null) {
                        try {
                            Log.e(TAG, "Error body: " + response.errorBody().string());
                        } catch (Exception e) {
                            Log.e(TAG, "Error reading error body: " + e.getMessage());
                        }
                    }
                    Toast.makeText(MainActivity.this, "Failed to load categories", Toast.LENGTH_SHORT).show();

                    // Load default categories as fallback
                    loadCategoriesManually();
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                Log.e(TAG, "Network error loading categories: " + t.getMessage(), t);
                Toast.makeText(MainActivity.this, "Network error loading categories", Toast.LENGTH_SHORT).show();

                // Load default categories as fallback
                loadCategoriesManually();
            }
        });
    }

    private void loadCategoriesManually() {
        // Fallback: Load default categories
        categoryList.clear();
        List<String> defaultCategories = Arrays.asList(
                "Festival", "Musique", "Théâtre", "Danse", "Sport", "Exposition", "Traditionnel"
        );

        for (String category : defaultCategories) {
            int iconResId = getCategoryIcon(category);
            categoryList.add(new Category(category, iconResId));
        }

        categoryAdapter.notifyDataSetChanged();
        Log.d(TAG, "Loaded default categories: " + defaultCategories.size());
    }

    private int getCategoryIcon(String category) {
        // Return appropriate icon based on category name
        switch (category.toLowerCase()) {
            case "musique":
                return R.drawable.ic_music;
            case "théâtre":
                return R.drawable.ic_theater;
            case "festival":
                return R.drawable.ic_festival;
            case "traditionnel":
                return R.drawable.ic_traditional;
            case "danse":
                return R.drawable.ic_dance;
            case "sport":
                return R.drawable.ic_sport;
            default:
                return R.drawable.ic_event;
        }
    }

    private void loadVenues() {
        apiService.getVenues().enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<String> venues = response.body();
                    Log.d(TAG, "Loaded venues: " + venues);

                    // Save venues to shared preferences for use in search filter
                    saveVenuesToPrefs(venues);
                } else {
                    Log.e(TAG, "Failed to load venues. Response code: " + response.code());
                    if (response.errorBody() != null) {
                        try {
                            Log.e(TAG, "Error body: " + response.errorBody().string());
                        } catch (Exception e) {
                            Log.e(TAG, "Error reading error body: " + e.getMessage());
                        }
                    }
                    Toast.makeText(MainActivity.this, "Failed to load venues", Toast.LENGTH_SHORT).show();

                    // Load default venues as fallback
                    loadVenuesManually();
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                Log.e(TAG, "Network error loading venues: " + t.getMessage(), t);
                Toast.makeText(MainActivity.this, "Network error loading venues", Toast.LENGTH_SHORT).show();

                // Load default venues as fallback
                loadVenuesManually();
            }
        });
    }

    private void loadVenuesManually() {
        // Fallback: Load default venues
        List<String> defaultVenues = Arrays.asList(
                "Amphithéâtre de Carthage",
                "Théâtre Municipal de Tunis",
                "Centre Culturel de Hammamet",
                "Basilique de Tabarka",
                "Palais Ennejma Ezzahra",
                "Stade Olympique de Radès",
                "Place de Houmt Souk",
                "Musée d'Art Moderne de Tunis"
        );

        // Save venues to shared preferences for use in search filter
        saveVenuesToPrefs(defaultVenues);
        Log.d(TAG, "Loaded default venues: " + defaultVenues.size());
    }

    private void saveVenuesToPrefs(List<String> venues) {
        SharedPreferences prefs = getSharedPreferences("TeskertiPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // Convert list to comma-separated string
        StringBuilder sb = new StringBuilder();
        for (String venue : venues) {
            sb.append(venue).append(",");
        }

        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1); // Remove last comma
        }

        editor.putString("venues_list", sb.toString());
        editor.apply();
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
    public void onCategoryClick(Category category) {
        // Create a search filter with just the category
        Intent intent = new Intent(this, SearchResultsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("category", category.getName());
        intent.putExtras(bundle);
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
        } else if (id == R.id.action_schedule) {
            startActivity(new Intent(this, ScheduleActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
