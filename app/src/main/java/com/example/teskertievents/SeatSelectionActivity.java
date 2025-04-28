package com.example.teskertievents;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.teskertievents.api.ApiClient;
import com.example.teskertievents.api.ApiService;
import com.example.teskertievents.models.Event;
import com.example.teskertievents.models.ReservationRequest;
import com.example.teskertievents.models.SeatAvailability;
import com.example.teskertievents.views.SeatMapView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SeatSelectionActivity extends AppCompatActivity {
    private Event event;
    private double totalPrice;
    private HashMap<String, Integer> selectedTickets;
    private ApiService apiService;
    private String sessionId;

    private SeatMapView seatMapView;
    private Button continueButton;
    private ProgressBar progressBar;
    private TextView selectedSeatsText;
    private TextView instructionsText;

    private List<String> selectedSeats = new ArrayList<>();
    private int requiredSeats = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat_selection);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Select Seats");

        // Initialize views
        seatMapView = findViewById(R.id.seatMapView);
        continueButton = findViewById(R.id.continueButton);
        progressBar = findViewById(R.id.progressBar);
        selectedSeatsText = findViewById(R.id.selectedSeatsText);
        instructionsText = findViewById(R.id.instructionsText);

        // Get data from intent
        event = (Event) getIntent().getSerializableExtra("event");
        totalPrice = getIntent().getDoubleExtra("totalPrice", 0.0);
        selectedTickets = (HashMap<String, Integer>) getIntent().getSerializableExtra("selectedTickets");

        if (event == null || selectedTickets == null) {
            Toast.makeText(this, "Error loading ticket information", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Calculate required seats
        for (Integer quantity : selectedTickets.values()) {
            requiredSeats += quantity;
        }

        instructionsText.setText(String.format("Please select %d seat(s)", requiredSeats));

        // Generate session ID for seat reservation
        sessionId = UUID.randomUUID().toString();

        // Initialize API service
        apiService = ApiClient.getClient().create(ApiService.class);

        // Setup seat map view
        seatMapView.setOnSeatSelectedListener((seatId, isSelected) -> {
            if (isSelected) {
                if (selectedSeats.size() < requiredSeats) {
                    selectedSeats.add(seatId);
                } else {
                    seatMapView.toggleSeat(seatId); // Deselect the seat
                    Toast.makeText(this, "You can only select " + requiredSeats + " seats", Toast.LENGTH_SHORT).show();
                    return;
                }
            } else {
                selectedSeats.remove(seatId);
            }

            updateSelectedSeatsText();
        });

        // Load seat availability
        loadSeatAvailability();

        // Setup continue button
        continueButton.setOnClickListener(v -> {
            if (selectedSeats.size() < requiredSeats) {
                Toast.makeText(this, "Please select " + requiredSeats + " seats", Toast.LENGTH_SHORT).show();
                return;
            }

            // Reserve seats temporarily
            reserveSeats();
        });
    }

    private void loadSeatAvailability() {
        progressBar.setVisibility(View.VISIBLE);

        apiService.getSeatAvailability(event.getId()).enqueue(new Callback<SeatAvailability>() {
            @Override
            public void onResponse(Call<SeatAvailability> call, Response<SeatAvailability> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    SeatAvailability availability = response.body();
                    seatMapView.setSeatAvailability(availability);
                } else {
                    Toast.makeText(SeatSelectionActivity.this, "Failed to load seat availability", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SeatAvailability> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(SeatSelectionActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateSelectedSeatsText() {
        if (selectedSeats.isEmpty()) {
            selectedSeatsText.setText("No seats selected");
        } else {
            selectedSeatsText.setText("Selected: " + String.join(", ", selectedSeats));
        }

        // Enable continue button if enough seats are selected
        continueButton.setEnabled(selectedSeats.size() == requiredSeats);
    }

    private void reserveSeats() {
        progressBar.setVisibility(View.VISIBLE);

        ReservationRequest request = new ReservationRequest(
                event.getId(),
                selectedSeats,
                sessionId
        );

        apiService.reserveSeats(request).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Object> result = response.body();
                    boolean success = (boolean) result.get("success");

                    if (success) {
                        // Navigate to checkout
                        Intent intent = new Intent(SeatSelectionActivity.this, CheckoutActivity.class);
                        intent.putExtra("event", event);
                        intent.putExtra("totalPrice", totalPrice);
                        intent.putExtra("selectedTickets", selectedTickets);
                        intent.putExtra("selectedSeats", new ArrayList<>(selectedSeats));
                        intent.putExtra("sessionId", sessionId);
                        startActivity(intent);
                    } else {
                        String message = (String) result.get("message");
                        Toast.makeText(SeatSelectionActivity.this, message, Toast.LENGTH_SHORT).show();
                        
                        // Reload seat availability
                        loadSeatAvailability();
                    }
                } else {
                    Toast.makeText(SeatSelectionActivity.this, "Failed to reserve seats", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(SeatSelectionActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
