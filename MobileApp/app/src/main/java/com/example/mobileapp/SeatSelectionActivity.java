package com.example.mobileapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.mobileapp.api.ApiClient;
import com.example.mobileapp.api.ApiService;
import com.example.mobileapp.models.Event;
import com.example.mobileapp.models.SeatAvailability;
import com.example.mobileapp.models.TicketType;
import com.example.mobileapp.utils.LogUtils;
import com.example.mobileapp.views.SeatMapView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SeatSelectionActivity extends AppCompatActivity {
    private static final String TAG = "SeatSelectionActivity";

    private SeatMapView seatMapView;
    private TextView instructionsText, selectedSeatsText;
    private Button continueButton;

    private Event event;
    private List<TicketType> ticketTypes;
    private Map<Integer, TicketType> ticketTypeMap = new HashMap<>();
    private double totalAmount = 0.0;
    private String sessionId;

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
        instructionsText = findViewById(R.id.instructionsText);
        selectedSeatsText = findViewById(R.id.selectedSeatsText);
        continueButton = findViewById(R.id.continueButton);

        // Get event and ticket types from intent
        event = (Event) getIntent().getSerializableExtra("event");
        ticketTypes = (List<TicketType>) getIntent().getSerializableExtra("ticketTypes");

        if (event == null) {
            Toast.makeText(this, "Error: Missing event information", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Generate a session ID for this booking
        sessionId = generateSessionId();

        // If ticket types weren't passed, create some default ones
        if (ticketTypes == null || ticketTypes.isEmpty()) {
            ticketTypes = createDefaultTicketTypes();
        }

        // Set event name in instructions
        instructionsText.setText("Please select seats for " + event.getTitle());

        // Create ticket type map for easy lookup
        for (TicketType type : ticketTypes) {
            ticketTypeMap.put(type.getId(), type);
        }

        setupSeatMap();

        // Set seat selection listener
        seatMapView.setOnSeatSelectedListener(new SeatMapView.OnSeatSelectedListener() {
            @Override
            public void onSeatSelected(int row, int col, boolean isSelected, int seatType) {
                updateSelectedSeatsInfo();
            }
        });

        // Set continue button click listener
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<SeatAvailability> selectedSeats = seatMapView.getSelectedSeats();

                if (selectedSeats.isEmpty()) {
                    Toast.makeText(SeatSelectionActivity.this, "Please select at least one seat", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Create selected seats list and ticket types map for checkout
                ArrayList<String> seatsList = new ArrayList<>();
                HashMap<String, Integer> ticketsMap = new HashMap<>();

                for (SeatAvailability seat : selectedSeats) {
                    seatsList.add(seat.getSeatNumber());

                    // Count ticket types
                    int typeId = seat.getTicketTypeId();
                    TicketType ticketType = ticketTypeMap.get(typeId);
                    if (ticketType != null) {
                        String typeName = ticketType.getName();
                        Integer count = ticketsMap.get(typeName);
                        if (count == null) {
                            ticketsMap.put(typeName, 1);
                        } else {
                            ticketsMap.put(typeName, count + 1);
                        }
                    }
                }

                // Proceed to checkout
                Intent intent = new Intent(SeatSelectionActivity.this, CheckoutActivity.class);
                intent.putExtra("event", event);
                intent.putExtra("selectedSeats", seatsList);
                intent.putExtra("selectedTickets", ticketsMap);
                intent.putExtra("totalPrice", totalAmount);
                intent.putExtra("sessionId", sessionId);
                startActivity(intent);
            }
        });

        // Initial update of selected seats info
        updateSelectedSeatsInfo();
    }

    private void updateSelectedSeatsInfo() {
        Map<Integer, Integer> selectedSeatsCount = seatMapView.getSelectedSeatsCount();
        List<SeatAvailability> selectedSeats = seatMapView.getSelectedSeats();

        StringBuilder sb = new StringBuilder();
        totalAmount = 0.0;

        for (Map.Entry<Integer, Integer> entry : selectedSeatsCount.entrySet()) {
            int typeId = entry.getKey();
            int count = entry.getValue();

            if (count > 0) {
                TicketType type = ticketTypeMap.get(typeId);
                if (type != null) {
                    double subtotal = count * type.getPrice();
                    totalAmount += subtotal;

                    sb.append(type.getName())
                            .append(": ")
                            .append(count)
                            .append(" x ")
                            .append(String.format("%.2f TND", type.getPrice()))
                            .append(" = ")
                            .append(String.format("%.2f TND", subtotal))
                            .append("\n");
                }
            }
        }

        if (selectedSeats.isEmpty()) {
            selectedSeatsText.setText("No seats selected");
            continueButton.setEnabled(false);
        } else {
            sb.append("\nTotal: ").append(String.format("%.2f TND", totalAmount));
            selectedSeatsText.setText(sb.toString());
            continueButton.setEnabled(true);
        }
    }

    private void setupSeatMap() {
        // Set up seat map
        seatMapView.setTicketTypes(ticketTypes);
        seatMapView.setMaxSeatsPerType(5); // Allow up to 5 seats per type

        // Check locally reserved seats
        if (event != null) {
            seatMapView.setEventId(event.getId());
        }
    }

    private List<TicketType> createDefaultTicketTypes() {
        List<TicketType> types = new ArrayList<>();

        TicketType vip = new TicketType();
        vip.setId(2);
        vip.setName("VIP");
        vip.setPrice(100.0);
        vip.setAvailableSeats(20);

        TicketType premium = new TicketType();
        premium.setId(1);
        premium.setName("Premium");
        premium.setPrice(60.0);
        premium.setAvailableSeats(30);

        TicketType standard = new TicketType();
        standard.setId(0);
        standard.setName("Standard");
        standard.setPrice(30.0);
        standard.setAvailableSeats(50);

        types.add(vip);
        types.add(premium);
        types.add(standard);

        return types;
    }

    private String generateSessionId() {
        return "session_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 1000);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
