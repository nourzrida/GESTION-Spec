package com.example.mobileapp.api;

import com.example.mobileapp.models.Booking;
import com.example.mobileapp.models.BookingRequest;
import com.example.mobileapp.models.Event;
import com.example.mobileapp.models.EventSchedule;
import com.example.mobileapp.models.LoginRequest;
import com.example.mobileapp.models.LoginResponse;
import com.example.mobileapp.models.PaymentResponse;
import com.example.mobileapp.models.RegisterRequest;
import com.example.mobileapp.models.RegisterResponse;
import com.example.mobileapp.models.ReservationRequest;
import com.example.mobileapp.models.SeatAvailability;
import com.example.mobileapp.models.TicketType;
import com.example.mobileapp.models.User;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    // Authentication
    @POST("users/login")
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);

    @POST("users/register")
    Call<RegisterResponse> registerUser(@Body RegisterRequest registerRequest);

    // User
    @GET("users/profile")
    Call<User> getUserProfile();

    // Events
    @GET("events")
    Call<List<Event>> getAllEvents();

    @GET("events/{id}")
    Call<Event> getEvent(@Path("id") int eventId);

    @GET("events/search")
    Call<List<Event>> searchEvents(
            @Query("title") String title,
            @Query("place") String place,
            @Query("category") String category,
            @Query("startDate") String startDate,
            @Query("endDate") String endDate
    );

    // Event Schedules
    @GET("events/{id}/schedules")
    Call<List<EventSchedule>> getEventSchedules(@Path("id") int eventId);

    @GET("venues/{id}/schedules")
    Call<List<EventSchedule>> getVenueSchedules(@Path("id") int venueId);

    @GET("schedules")
    Call<List<EventSchedule>> getAllSchedules();

    @GET("schedules/upcoming")
    Call<List<EventSchedule>> getUpcomingSchedules();

    // Categories and Venues
    @GET("events/categories")
    Call<List<String>> getCategories();

    @GET("venues")
    Call<List<String>> getVenues();

    // Tickets
    @GET("tickets/types/{eventId}")
    Call<List<TicketType>> getTicketTypes(@Path("eventId") int eventId);

    @GET("tickets/seats/{eventId}")
    Call<SeatAvailability> getSeatAvailability(@Path("eventId") int eventId);

    @POST("tickets/reserve")
    Call<Map<String, Object>> reserveSeats(@Body ReservationRequest reservationRequest);

    // Bookings
    @POST("bookings/create")
    Call<PaymentResponse> createBooking(@Body BookingRequest bookingRequest);

    @GET("bookings")
    Call<List<Booking>> getUserBookings(@Query("email") String email);

    @GET("bookings/{id}")
    Call<Booking> getBooking(@Path("id") String bookingId);
}
