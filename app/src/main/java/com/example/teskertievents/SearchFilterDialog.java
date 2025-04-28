package com.example.teskertievents;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.teskertievents.api.ApiClient;
import com.example.teskertievents.api.ApiService;
import com.example.teskertievents.models.SearchFilter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFilterDialog extends AppCompatActivity {
    private EditText titleInput;
    private Spinner categorySpinner, venueSpinner;
    private TextView startDateText, endDateText;
    private Button applyFiltersButton, clearFiltersButton;
    private ProgressBar progressBar;

    private ApiService apiService;
    private Calendar startDateCalendar, endDateCalendar;
    private SimpleDateFormat dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_search_filter);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Search Events");

        // Initialize views
        titleInput = findViewById(R.id.titleInput);
        categorySpinner = findViewById(R.id.categorySpinner);
        venueSpinner = findViewById(R.id.venueSpinner);
        startDateText = findViewById(R.id.startDateText);
        endDateText = findViewById(R.id.endDateText);
        applyFiltersButton = findViewById(R.id.applyFiltersButton);
        clearFiltersButton = findViewById(R.id.clearFiltersButton);
        progressBar = findViewById(R.id.progressBar);

        // Initialize API service
        apiService = ApiClient.getClient().create(ApiService.class);

        // Initialize date format
        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        startDateCalendar = Calendar.getInstance();
        endDateCalendar = Calendar.getInstance();
        endDateCalendar.add(Calendar.MONTH, 3); // Default end date is 3 months from now

        // Setup date pickers
        setupDatePickers();

        // Load categories and venues
        loadCategories();
        loadVenues();

        // Setup buttons
        applyFiltersButton.setOnClickListener(v -> applyFilters());
        clearFiltersButton.setOnClickListener(v -> clearFilters());
    }

    private void setupDatePickers() {
        startDateText.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (view, year, month, dayOfMonth) -> {
                        startDateCalendar.set(Calendar.YEAR, year);
                        startDateCalendar.set(Calendar.MONTH, month);
                        startDateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        startDateText.setText(dateFormat.format(startDateCalendar.getTime()));
                    },
                    startDateCalendar.get(Calendar.YEAR),
                    startDateCalendar.get(Calendar.MONTH),
                    startDateCalendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });

        endDateText.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (view, year, month, dayOfMonth) -> {
                        endDateCalendar.set(Calendar.YEAR, year);
                        endDateCalendar.set(Calendar.MONTH, month);
                        endDateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        endDateText.setText(dateFormat.format(endDateCalendar.getTime()));
                    },
                    endDateCalendar.get(Calendar.YEAR),
                    endDateCalendar.get(Calendar.MONTH),
                    endDateCalendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });

        // Set initial date texts
        startDateText.setText(dateFormat.format(startDateCalendar.getTime()));
        endDateText.setText(dateFormat.format(endDateCalendar.getTime()));
    }

    private void loadCategories() {
        progressBar.setVisibility(View.VISIBLE);

        apiService.getCategories().enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    List<String> categories = new ArrayList<>();
                    categories.add("All Categories"); // Add default option
                    categories.addAll(response.body());

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            SearchFilterDialog.this,
                            android.R.layout.simple_spinner_item,
                            categories
                    );
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    categorySpinner.setAdapter(adapter);
                } else {
                    Toast.makeText(SearchFilterDialog.this, "Failed to load categories", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(SearchFilterDialog.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadVenues() {
        progressBar.setVisibility(View.VISIBLE);

        apiService.getVenues().enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    List<String> venues = new ArrayList<>();
                    venues.add("All Venues"); // Add default option
                    venues.addAll(response.body());

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            SearchFilterDialog.this,
                            android.R.layout.simple_spinner_item,
                            venues
                    );
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    venueSpinner.setAdapter(adapter);
                } else {
                    Toast.makeText(SearchFilterDialog.this, "Failed to load venues", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(SearchFilterDialog.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void applyFilters() {
        SearchFilter filter = new SearchFilter();

        // Get title
        String title = titleInput.getText().toString().trim();
        if (!title.isEmpty()) {
            filter.setTitle(title);
        }

        // Get category
        String category = categorySpinner.getSelectedItem().toString();
        if (!category.equals("All Categories")) {
            filter.setCategory(category);
        }

        // Get venue
        String venue = venueSpinner.getSelectedItem().toString();
        if (!venue.equals("All Venues")) {
            filter.setPlace(venue);
        }

        // Get dates
        filter.setStartDate(startDateText.getText().toString());
        filter.setEndDate(endDateText.getText().toString());

        // Navigate to search results
        Intent intent = new Intent(this, SearchResultsActivity.class);
        intent.putExtra("filter", filter);
        startActivity(intent);
    }

    private void clearFilters() {
        titleInput.setText("");
        categorySpinner.setSelection(0);
        venueSpinner.setSelection(0);

        // Reset dates
        startDateCalendar = Calendar.getInstance();
        endDateCalendar = Calendar.getInstance();
        endDateCalendar.add(Calendar.MONTH, 3);
        startDateText.setText(dateFormat.format(startDateCalendar.getTime()));
        endDateText.setText(dateFormat.format(endDateCalendar.getTime()));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
