package com.example.mobileapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileapp.adapters.BookingAdapter;
import com.example.mobileapp.api.ApiClient;
import com.example.mobileapp.api.ApiService;
import com.example.mobileapp.models.Booking;
import com.example.mobileapp.models.User;
import com.example.mobileapp.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {
    private TextView nameText, emailText, phoneText;
    private Button logoutButton, viewBookingsButton;
    private ProgressBar progressBar;
    private RecyclerView bookingsRecyclerView;
    private BookingAdapter bookingAdapter;
    private List<Booking> bookingList;
    private ApiService apiService;
    private boolean bookingsVisible = false;
    private TextView noBookingsText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("My Profile");

        // Initialize views
        nameText = findViewById(R.id.nameText);
        emailText = findViewById(R.id.emailText);
        phoneText = findViewById(R.id.phoneText);
        logoutButton = findViewById(R.id.logoutButton);
        viewBookingsButton = findViewById(R.id.viewBookingsButton);
        progressBar = findViewById(R.id.progressBar);
        bookingsRecyclerView = findViewById(R.id.bookingsRecyclerView);

        // Add a TextView for "No bookings" message
        noBookingsText = findViewById(R.id.noBookingsText);
        if (noBookingsText == null) {
            // If the layout doesn't have this TextView, we'll handle it in code
            noBookingsText = new TextView(this);
            noBookingsText.setText("You have no bookings yet");
            noBookingsText.setVisibility(View.GONE);
        }

        // Setup RecyclerView
        bookingList = new ArrayList<>();
        bookingAdapter = new BookingAdapter(this, bookingList);
        bookingsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        bookingsRecyclerView.setAdapter(bookingAdapter);

        // Initialize API service
        apiService = ApiClient.getClient().create(ApiService.class);

        // Load user profile
        loadUserProfile();

        // Setup logout button
        logoutButton.setOnClickListener(v -> logout());

        // Setup view bookings button
        viewBookingsButton.setOnClickListener(v -> {
            if (bookingsVisible) {
                bookingsRecyclerView.setVisibility(View.GONE);
                if (noBookingsText != null) noBookingsText.setVisibility(View.GONE);
                viewBookingsButton.setText("VIEW MY BOOKINGS");
                bookingsVisible = false;
            } else {
                loadUserBookings();
                bookingsVisible = true;
            }
        });
    }

    private void loadUserProfile() {
        // First try to load from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("TeskertiPrefs", MODE_PRIVATE);
        String userName = prefs.getString("user_name", "");
        String userEmail = prefs.getString("user_email", "");
        String userPhone = prefs.getString("user_phone", "");

        // Display cached user info
        nameText.setText(userName);
        emailText.setText(userEmail);
        phoneText.setText(userPhone);

        // Then fetch fresh data from API
        progressBar.setVisibility(View.VISIBLE);

        apiService.getUserProfile().enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();

                    // Update UI
                    nameText.setText(user.getName());
                    emailText.setText(user.getEmail());
                    phoneText.setText(user.getPhone());

                    // Update SharedPreferences
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("user_name", user.getName());
                    editor.putString("user_email", user.getEmail());
                    editor.putString("user_phone", user.getPhone());
                    editor.apply();
                } else {
                    Toast.makeText(ProfileActivity.this, "Failed to load profile", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ProfileActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadUserBookings() {
        progressBar.setVisibility(View.VISIBLE);
        bookingsRecyclerView.setVisibility(View.VISIBLE);
        if (noBookingsText != null) noBookingsText.setVisibility(View.GONE);
        viewBookingsButton.setText("HIDE BOOKINGS");

        // Fetch the email from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("TeskertiPrefs", MODE_PRIVATE);
        String userEmail = prefs.getString("user_email", "");

        // Debug: Check the email value
        Log.d("ProfileActivity", "User email: " + userEmail);

        apiService.getUserBookings(userEmail).enqueue(new Callback<List<Booking>>() {
            @Override
            public void onResponse(Call<List<Booking>> call, Response<List<Booking>> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    bookingList.clear();
                    bookingList.addAll(response.body());
                    bookingAdapter.notifyDataSetChanged();

                    if (bookingList.isEmpty()) {
                        if (noBookingsText != null) {
                            noBookingsText.setVisibility(View.VISIBLE);
                        } else {
                            Toast.makeText(ProfileActivity.this, "You have no bookings yet", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Log.d("ProfileActivity", "Failed to load bookings: " + response.code());
                    loadMockBookings();  // Fallback to mock data if no bookings are found
                }
            }

            @Override
            public void onFailure(Call<List<Booking>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.d("ProfileActivity", "Network error loading bookings: " + t.getMessage());
                loadMockBookings();  // Fallback to mock data if network error
            }
        });
    }

    private void loadMockBookings() {
        // Create a mock booking for demonstration
        bookingList.clear();

        // Check if we have any event data in shared preferences
        SharedPreferences prefs = getSharedPreferences("TeskertiPrefs", MODE_PRIVATE);
        String lastEventTitle = prefs.getString("last_event_title", "Sample Event");
        String lastEventDate = prefs.getString("last_event_date", "2023-07-15T20:00:00.000Z");
        String lastEventVenue = prefs.getString("last_event_venue", "Sample Venue");

        // Create a sample booking
        Booking mockBooking = new Booking();
        mockBooking.setBookingReference("MOCK12345");
        mockBooking.setEventTitle(lastEventTitle);
        mockBooking.setEventDate(lastEventDate);
        mockBooking.setEventVenue(lastEventVenue);
        mockBooking.setTotalPrice(50.0);

        // Add seats
        List<String> seats = new ArrayList<>();
        seats.add("A1");
        seats.add("A2");
        mockBooking.setSeats(seats);

        // Add to list
        bookingList.add(mockBooking);
        bookingAdapter.notifyDataSetChanged();

        if (bookingList.isEmpty() && noBookingsText != null) {
            noBookingsText.setVisibility(View.VISIBLE);
        }
    }

    private void logout() {
        // Clear auth token
        ApiClient.clearAuthToken();

        // Clear SharedPreferences
        SharedPreferences prefs = getSharedPreferences("TeskertiPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("auth_token");
        editor.apply();

        // Navigate to MainActivity
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
