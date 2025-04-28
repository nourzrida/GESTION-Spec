package com.example.teskertievents;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.teskertievents.api.ApiClient;
import com.example.teskertievents.api.ApiService;
import com.example.teskertievents.models.BookingRequest;
import com.example.teskertievents.models.Event;
import com.example.teskertievents.models.PaymentResponse;
import com.example.teskertievents.utils.DateUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckoutActivity extends AppCompatActivity {
    private Event event;
    private double totalPrice;
    private HashMap<String, Integer> selectedTickets;
    private ArrayList<String> selectedSeats;
    private String sessionId;
    private ApiService apiService;

    private TextView eventTitleText, eventDateText, eventVenueText;
    private TextView ticketsText, seatsText, totalPriceText;
    private EditText nameInput, emailInput, phoneInput;
    private EditText cardNumberInput, expiryDateInput, cvvInput;
    private Button confirmPaymentButton;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Checkout");

        // Initialize views
        eventTitleText = findViewById(R.id.eventTitleText);
        eventDateText = findViewById(R.id.eventDateText);
        eventVenueText = findViewById(R.id.eventVenueText);
        ticketsText = findViewById(R.id.ticketsText);
        seatsText = findViewById(R.id.seatsText);
        totalPriceText = findViewById(R.id.totalPriceText);
        nameInput = findViewById(R.id.nameInput);
        emailInput = findViewById(R.id.emailInput);
        phoneInput = findViewById(R.id.phoneInput);
        cardNumberInput = findViewById(R.id.cardNumberInput);
        expiryDateInput = findViewById(R.id.expiryDateInput);
        cvvInput = findViewById(R.id.cvvInput);
        confirmPaymentButton = findViewById(R.id.confirmPaymentButton);
        progressBar = findViewById(R.id.progressBar);

        // Get data from intent
        event = (Event) getIntent().getSerializableExtra("event");
        totalPrice = getIntent().getDoubleExtra("totalPrice", 0.0);
        selectedTickets = (HashMap<String, Integer>) getIntent().getSerializableExtra("selectedTickets");
        selectedSeats = getIntent().getStringArrayListExtra("selectedSeats");
        sessionId = getIntent().getStringExtra("sessionId");

        if (event == null || selectedTickets == null || selectedSeats == null || sessionId == null) {
            Toast.makeText(this, "Error loading booking information", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize API service
        apiService = ApiClient.getClient().create(ApiService.class);

        // Display booking information
        displayBookingInfo();

        // Pre-fill user info if logged in
        prefillUserInfo();

        // Setup confirm payment button
        confirmPaymentButton.setOnClickListener(v -> {
            if (validateInputs()) {
                processPayment();
            }
        });
    }

    private void displayBookingInfo() {
        eventTitleText.setText(event.getTitle());
        eventDateText.setText(DateUtils.formatDate(event.getEventDate()));
        eventVenueText.setText(event.getVenue());

        // Display selected tickets
        StringBuilder ticketsBuilder = new StringBuilder();
        for (String ticketType : selectedTickets.keySet()) {
            int quantity = selectedTickets.get(ticketType);
            ticketsBuilder.append(quantity).append(" x ").append(ticketType).append("\n");
        }
        ticketsText.setText(ticketsBuilder.toString());

        // Display selected seats
        seatsText.setText(String.join(", ", selectedSeats));

        // Display total price
        totalPriceText.setText(String.format("%.2f TND", totalPrice));
    }

    private void prefillUserInfo() {
        SharedPreferences prefs = getSharedPreferences("TeskertiPrefs", MODE_PRIVATE);
        String userName = prefs.getString("user_name", "");
        String userEmail = prefs.getString("user_email", "");
        String userPhone = prefs.getString("user_phone", "");

        if (!userName.isEmpty()) nameInput.setText(userName);
        if (!userEmail.isEmpty()) emailInput.setText(userEmail);
        if (!userPhone.isEmpty()) phoneInput.setText(userPhone);
    }

    private boolean validateInputs() {
        // Validate customer info
        if (nameInput.getText().toString().trim().isEmpty()) {
            nameInput.setError("Name is required");
            return false;
        }

        if (emailInput.getText().toString().trim().isEmpty()) {
            emailInput.setError("Email is required");
            return false;
        }

        if (phoneInput.getText().toString().trim().isEmpty()) {
            phoneInput.setError("Phone is required");
            return false;
        }

        // Validate payment info
        if (cardNumberInput.getText().toString().trim().isEmpty()) {
            cardNumberInput.setError("Card number is required");
            return false;
        }

        if (expiryDateInput.getText().toString().trim().isEmpty()) {
            expiryDateInput.setError("Expiry date is required");
            return false;
        }

        if (cvvInput.getText().toString().trim().isEmpty()) {
            cvvInput.setError("CVV is required");
            return false;
        }

        return true;
    }

    private void processPayment() {
        progressBar.setVisibility(View.VISIBLE);
        confirmPaymentButton.setEnabled(false);

        // Create customer info
        BookingRequest.CustomerInfo customerInfo = new BookingRequest.CustomerInfo(
                nameInput.getText().toString().trim(),
                emailInput.getText().toString().trim(),
                phoneInput.getText().toString().trim()
        );

        // Create payment info
        BookingRequest.PaymentInfo paymentInfo = new BookingRequest.PaymentInfo(
                cardNumberInput.getText().toString().trim(),
                expiryDateInput.getText().toString().trim(),
                cvvInput.getText().toString().trim()
        );

        // Create booking request
        BookingRequest request = new BookingRequest(
                event.getId(),
                selectedSeats,
                selectedTickets,
                sessionId,
                customerInfo,
                paymentInfo
        );

        // Send booking request
        apiService.createBooking(request).enqueue(new Callback<PaymentResponse>() {
            @Override
            public void onResponse(Call<PaymentResponse> call, Response<PaymentResponse> response) {
                progressBar.setVisibility(View.GONE);
                confirmPaymentButton.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    PaymentResponse paymentResponse = response.body();
                    
                    if (paymentResponse.isSuccess()) {
                        // Save user info for future use
                        saveUserInfo(customerInfo);
                        
                        // Navigate to booking confirmation
                        Intent intent = new Intent(CheckoutActivity.this, BookingConfirmationActivity.class);
                        intent.putExtra("bookingReference", paymentResponse.getBookingReference());
                        intent.putExtra("qrCodeData", paymentResponse.getQrCodeData());
                        intent.putExtra("event", event);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(CheckoutActivity.this, paymentResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CheckoutActivity.this, "Payment failed. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }

              Please try again.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PaymentResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                confirmPaymentButton.setEnabled(true);
                Toast.makeText(CheckoutActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUserInfo(BookingRequest.CustomerInfo customerInfo) {
        SharedPreferences prefs = getSharedPreferences("TeskertiPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("user_name", customerInfo.getName());
        editor.putString("user_email", customerInfo.getEmail());
        editor.putString("user_phone", customerInfo.getPhone());
        editor.apply();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
