package com.example.mobileapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.mobileapp.api.ApiClient;
import com.example.mobileapp.api.ApiService;
import com.example.mobileapp.models.Event;
import com.example.mobileapp.models.TicketType;
import com.example.mobileapp.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventDetailsActivity extends AppCompatActivity {
    private Event event;
    private ApiService apiService;
    private List<TicketType> ticketTypes;

    private ImageView eventImage;
    private TextView titleText, dateText, venueText, priceText, descriptionText;
    private Button buyTicketsButton, viewSchedulesButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Event Details");

        // Initialize views
        eventImage = findViewById(R.id.eventImage);
        titleText = findViewById(R.id.titleText);
        dateText = findViewById(R.id.dateText);
        venueText = findViewById(R.id.venueText);
        priceText = findViewById(R.id.priceText);
        descriptionText = findViewById(R.id.descriptionText);
        buyTicketsButton = findViewById(R.id.buyTicketsButton);
        viewSchedulesButton = findViewById(R.id.viewSchedulesButton);

        // Get event from intent
        event = (Event) getIntent().getSerializableExtra("event");
        if (event == null) {
            Toast.makeText(this, "Error loading event details", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize API service
        apiService = ApiClient.getClient().create(ApiService.class);

        // Display event details
        displayEventDetails();

        // Load ticket types
        loadTicketTypes();

        // Setup buy tickets button
        buyTicketsButton.setOnClickListener(v -> {
            if (event.isSoldOut()) {
                Toast.makeText(this, "This event is sold out", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if user is logged in
            SharedPreferences prefs = getSharedPreferences("TeskertiPrefs", MODE_PRIVATE);
            String token = prefs.getString("auth_token", null);

            if (token == null) {
                // User is not logged in, redirect to login
                Toast.makeText(this, "Please login to purchase tickets", Toast.LENGTH_SHORT).show();
                Intent loginIntent = new Intent(this, LoginActivity.class);
                loginIntent.putExtra("redirectToEvent", true);
                loginIntent.putExtra("event", event);
                startActivity(loginIntent);
                return;
            }

            if (ticketTypes != null && !ticketTypes.isEmpty()) {
                Intent intent = new Intent(this, TicketSelectionActivity.class);
                intent.putExtra("event", event);
                intent.putExtra("ticketTypes", new ArrayList<>(ticketTypes));
                startActivity(intent);
            } else {
                Toast.makeText(this, "No tickets available for this event", Toast.LENGTH_SHORT).show();
            }
        });

        // Setup view schedules button
        viewSchedulesButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, EventScheduleActivity.class);
            intent.putExtra("event", event);
            startActivity(intent);
        });
    }

    private void displayEventDetails() {
        titleText.setText(event.getTitle());
        dateText.setText(DateUtils.formatDate(event.getEventDate()));
        venueText.setText(event.getVenue());
        priceText.setText(String.format("Starting at %.2f TND", event.getPrice()));
        descriptionText.setText(event.getDescription());

        // Load image with Glide
        Glide.with(this)
                .load(event.getImageUrl())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(eventImage);

        // Update button state if event is sold out
        if (event.isSoldOut()) {
            buyTicketsButton.setText("SOLD OUT");
            buyTicketsButton.setEnabled(false);
        }
    }

    private void loadTicketTypes() {
        apiService.getTicketTypes(event.getId()).enqueue(new Callback<List<TicketType>>() {
            @Override
            public void onResponse(Call<List<TicketType>> call, Response<List<TicketType>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ticketTypes = response.body();

                    // If no tickets available, disable button
                    if (ticketTypes.isEmpty()) {
                        buyTicketsButton.setText("NO TICKETS AVAILABLE");
                        buyTicketsButton.setEnabled(false);
                    }
                } else {
                    Toast.makeText(EventDetailsActivity.this, "Failed to load ticket information", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<TicketType>> call, Throwable t) {
                Toast.makeText(EventDetailsActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
