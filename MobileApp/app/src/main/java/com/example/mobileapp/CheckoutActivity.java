package com.example.mobileapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.mobileapp.api.ApiClient;
import com.example.mobileapp.api.ApiService;
import com.example.mobileapp.models.BookingRequest;
import com.example.mobileapp.models.Event;
import com.example.mobileapp.models.PaymentResponse;
import com.example.mobileapp.utils.DateUtils;
import com.example.mobileapp.utils.LogUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckoutActivity extends AppCompatActivity {
    private static final String TAG = "CheckoutActivity";
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

        // Save event info for potential mock bookings later
        saveEventInfo(event);

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

    private void saveEventInfo(Event event) {
        SharedPreferences prefs = getSharedPreferences("TeskertiPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("last_event_title", event.getTitle());
        editor.putString("last_event_date", event.getEventDate());
        editor.putString("last_event_venue", event.getVenue());
        editor.apply();
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
        String customerName = nameInput.getText().toString().trim();
        String customerEmail = emailInput.getText().toString().trim();
        String customerPhone = phoneInput.getText().toString().trim();

        // Create payment info
        String cardNumber = cardNumberInput.getText().toString().trim();
        String expiryDate = expiryDateInput.getText().toString().trim();
        String cvv = cvvInput.getText().toString().trim();

        // Create booking request
        BookingRequest request = new BookingRequest();
        request.setEventId(event.getId());
        request.setSeats(selectedSeats);
        request.setTicketTypes(selectedTickets);
        request.setSessionId(sessionId);
        request.setCustomerName(customerName);
        request.setCustomerEmail(customerEmail);
        request.setCustomerPhone(customerPhone);
        request.setCardNumber(cardNumber);
        request.setCardExpiry(expiryDate);
        request.setCardCvv(cvv);
        request.setPaymentMethod("card");

        // Log the request for debugging
        LogUtils.logRequest("bookings/create", request);

        // Simulate processing delay
        simulatePaymentProcessing(request);
    }

    private void simulatePaymentProcessing(final BookingRequest request) {
        // Show a loading message
        Toast.makeText(this, "Processing payment...", Toast.LENGTH_SHORT).show();

        // Simulate a network delay
        new android.os.Handler().postDelayed(() -> {
            // Save user info for future use
            saveUserInfo(request.getCustomerName(), request.getCustomerEmail(), request.getCustomerPhone());

            // Generate a booking reference
            String bookingReference = generateRandomReference();

            // Create a simple QR code data
            String qrCodeData = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8z8BQDwAEhQGAhKmMIQAAAABJRU5ErkJggg==";

            // Navigate to confirmation
            navigateToConfirmation(bookingReference, qrCodeData);
        }, 2000); // 2 second delay
    }

    private String generateRandomReference() {
        String allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder reference = new StringBuilder(10);
        Random random = new Random();

        for (int i = 0; i < 10; i++) {
            reference.append(allowedChars.charAt(random.nextInt(allowedChars.length())));
        }

        return reference.toString();
    }

    private void saveUserInfo(String name, String email, String phone) {
        SharedPreferences prefs = getSharedPreferences("TeskertiPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("user_name", name);
        editor.putString("user_email", email);
        editor.putString("user_phone", phone);
        editor.apply();
    }

    private void navigateToConfirmation(String bookingReference, String qrCodeData) {
        progressBar.setVisibility(View.GONE);
        confirmPaymentButton.setEnabled(true);

        Intent intent = new Intent(this, BookingConfirmationActivity.class);
        intent.putExtra("bookingReference", bookingReference);
        intent.putExtra("qrCodeData", qrCodeData);
        intent.putExtra("event", event);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
