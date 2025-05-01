package com.example.mobileapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.mobileapp.api.ApiClient;
import com.example.mobileapp.api.ApiService;
import com.example.mobileapp.models.Booking;
import com.example.mobileapp.models.BookingRequest;
import com.example.mobileapp.models.PaymentResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

/**
 * Helper class to create sample bookings in the database
 */
public class DatabaseHelper {
    private static final String TAG = "DatabaseHelper";

    /**
     * Creates a sample booking in the database
     */
    public static void createSampleBooking(Context context) {
        // Get user info from SharedPreferences
        SharedPreferences prefs = context.getSharedPreferences("TeskertiPrefs", MODE_PRIVATE);
        String userName = prefs.getString("user_name", "Sample User");
        String userEmail = prefs.getString("user_email", "sample@example.com");
        String userPhone = prefs.getString("user_phone", "21612345678");

        // Create a sample booking request
        BookingRequest request = new BookingRequest();
        request.setEventId(1); // Use the first event

        // Add some sample seats
        List<String> seats = new ArrayList<>();
        seats.add("A1");
        seats.add("A2");
        request.setSeats(seats);

        // Add ticket types
        Map<String, Integer> ticketTypes = new HashMap<>();
        ticketTypes.put("VIP", 2);
        request.setTicketTypes(ticketTypes);

        // Set session ID
        request.setSessionId(UUID.randomUUID().toString());

        // Set customer info
        request.setCustomerName(userName);
        request.setCustomerEmail(userEmail);
        request.setCustomerPhone(userPhone);

        // Set payment info
        request.setCardNumber("4111111111111111");
        request.setCardExpiry("12/25");
        request.setCardCvv("123");
        request.setPaymentMethod("card");

        // Create the booking via API
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.createBooking(request).enqueue(new Callback<PaymentResponse>() {
            @Override
            public void onResponse(Call<PaymentResponse> call, Response<PaymentResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PaymentResponse paymentResponse = response.body();
                    if (paymentResponse.isSuccess()) {
                        Log.d(TAG, "Sample booking created successfully: " + paymentResponse.getBookingReference());
                    } else {
                        Log.e(TAG, "Failed to create sample booking: " + paymentResponse.getMessage());
                    }
                } else {
                    Log.e(TAG, "API error creating sample booking: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<PaymentResponse> call, Throwable t) {
                Log.e(TAG, "Network error creating sample booking: " + t.getMessage());
            }
        });
    }

    /**
     * Creates multiple sample bookings for testing
     */
    public static void createMultipleSampleBookings(Context context, int count) {
        for (int i = 0; i < count; i++) {
            createSampleBooking(context);
        }
    }
}
