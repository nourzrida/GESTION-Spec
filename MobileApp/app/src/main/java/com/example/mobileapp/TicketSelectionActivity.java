package com.example.mobileapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileapp.adapters.TicketTypeAdapter;
import com.example.mobileapp.api.ApiClient;
import com.example.mobileapp.api.ApiService;
import com.example.mobileapp.models.Event;
import com.example.mobileapp.models.TicketType;
import com.example.mobileapp.utils.DateUtils;
import com.google.gson.Gson;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TicketSelectionActivity extends AppCompatActivity implements TicketTypeAdapter.OnTicketSelectedListener {
    private Event event;
    private ApiService apiService;
    private List<TicketType> ticketTypes;
    private TicketTypeAdapter adapter;

    private TextView eventTitleText, eventDateText, eventVenueText;
    private RecyclerView recyclerView;
    private Button continueButton;
    private ProgressBar progressBar;
    private TextView totalPriceText;

    private double totalPrice = 0.0;
    private Map<String, Integer> selectedTickets = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_selection);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Select Tickets");

        // Initialize views
        eventTitleText = findViewById(R.id.eventTitleText);
        eventDateText = findViewById(R.id.eventDateText);
        eventVenueText = findViewById(R.id.eventVenueText);
        recyclerView = findViewById(R.id.recyclerView);
        continueButton = findViewById(R.id.continueButton);
        progressBar = findViewById(R.id.progressBar);
        totalPriceText = findViewById(R.id.totalPriceText);

        // Get event from intent
        event = (Event) getIntent().getSerializableExtra("event");
        if (event == null) {
            Toast.makeText(this, "Error loading event details", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Display event info
        eventTitleText.setText(event.getTitle());
        eventDateText.setText(DateUtils.formatDate(event.getEventDate()));
        eventVenueText.setText(event.getVenue());

        // Initialize API service
        apiService = ApiClient.getClient().create(ApiService.class);

        // Setup RecyclerView
        ticketTypes = new ArrayList<>();
        adapter = new TicketTypeAdapter(this, ticketTypes, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Load ticket types
        loadTicketTypes();

        // Setup continue button
        continueButton.setOnClickListener(v -> {
            if (totalPrice <= 0) {
                Toast.makeText(this, "Please select at least one ticket", Toast.LENGTH_SHORT).show();
                return;
            }

            // Navigate to seat selection
            Intent intent = new Intent(this, SeatSelectionActivity.class);
            intent.putExtra("event", event);
            intent.putExtra("totalPrice", totalPrice);
            intent.putExtra("selectedTickets", new HashMap<>(selectedTickets));
            startActivity(intent);
        });
    }

    private void loadTicketTypes() {
        progressBar.setVisibility(View.VISIBLE);

        apiService.getTicketTypes(event.getId()).enqueue(new Callback<List<TicketType>>() {
            @Override
            public void onResponse(Call<List<TicketType>> call, Response<List<TicketType>> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    // Log the response for debugging
                    try {
                        String rawJson = new Gson().toJson(response.body());
                        Log.d("TeskertiAPI", "Ticket types response: " + rawJson);
                    } catch (Exception e) {
                        Log.e("TeskertiAPI", "Error logging ticket types: " + e.getMessage());
                    }

                    ticketTypes.clear();
                    ticketTypes.addAll(response.body());
                    adapter.notifyDataSetChanged();

                    if (ticketTypes.isEmpty()) {
                        Toast.makeText(TicketSelectionActivity.this, "No ticket types available for this event", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        Log.e("TeskertiAPI", "Failed to load ticket types. Code: " + response.code() + ", Message: " + response.errorBody().string());
                    } catch (Exception e) {
                        Log.e("TeskertiAPI", "Error logging ticket types error: " + e.getMessage());
                    }
                    Toast.makeText(TicketSelectionActivity.this, "Failed to load ticket types", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<TicketType>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e("TeskertiAPI", "Ticket types network error: " + t.getMessage());
                Toast.makeText(TicketSelectionActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onTicketSelected(Map<String, Integer> selectedTickets, double totalPrice) {
        this.selectedTickets = selectedTickets;
        this.totalPrice = totalPrice;
        totalPriceText.setText(String.format("Total: %.2f TND", totalPrice));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
