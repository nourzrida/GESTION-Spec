package com.example.teskertievents.api;

import com.example.teskertievents.models.Booking;
import com.example.teskertievents.models.BookingRequest;
import com.example.teskertievents.models.Event;
import com.example.teskertievents.models.LoginRequest;
import com.example.teskertievents.models.LoginResponse;
import com.example.teskertievents.models.PaymentResponse;
import com.example.teskertievents.models.RegisterRequest;
import com.example.teskertievents.models.RegisterResponse;
import com.example.teskertievents.models.ReservationRequest;
import com.example.teskertievents.models.SeatAvailability;
import com.example.teskertievents.models.TicketType;
import com.example.teskertievents.models.User;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface ApiService {
    // Events
    @GET("events")
    Call<List<Event>> getAllEvents();

    @GET("events/search")
    Call<List<Event>> searchEvents(@QueryMap Map<String, String> filters);

    @GET("events/{eventId}")
    Call<Event> getEventById(@Path("eventId") int eventId);

    @GET("events/categories")
    Call<List<String>> getCategories();

    @GET("events/venues")
    Call<List<String>> getVenues();

    // Tickets
    @GET("tickets/types/{eventId}")
    Call<List<TicketType>> getTicketTypes(@Path("eventId") int eventId);

    @GET("tickets/seats/{eventId}")
    Call<SeatAvailability> getSeatAvailability(@Path("eventId") int eventId);

    @POST("tickets/reserve")
    Call<Map<String, Object>> reserveSeats(@Body ReservationRequest request);

    // Bookings
    @POST("bookings/create")
    Call<PaymentResponse> createBooking(@Body BookingRequest request);

    @GET("bookings/{reference}")
    Call<Booking> getBookingByReference(@Path("reference") String reference);

    // Users
    @POST("users/register")
    Call<RegisterResponse> registerUser(@Body RegisterRequest request);

    @POST("users/login")
    Call<LoginResponse> loginUser(@Body LoginRequest request);

    @GET("users/profile")
    Call<User> getUserProfile();

    @GET("users/{userId}/bookings")
    Call<List<Booking>> getUserBookings(@Path("userId") String userId);
}
