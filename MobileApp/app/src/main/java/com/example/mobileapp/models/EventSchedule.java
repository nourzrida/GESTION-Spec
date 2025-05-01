package com.example.mobileapp.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class EventSchedule implements Serializable {
    private int id;

    @SerializedName("event_id")
    private int eventId;

    @SerializedName("venue_id")
    private int venueId;

    private String venue;

    private String date;

    @SerializedName("start_time")
    private String startTime;

    @SerializedName("end_time")
    private String endTime;

    @SerializedName("available_seats")
    private int availableSeats;

    @SerializedName("total_seats")
    private int totalSeats;

    @SerializedName("is_sold_out")
    private boolean isSoldOut;

    // Constructors
    public EventSchedule() {
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public int getVenueId() {
        return venueId;
    }

    public void setVenueId(int venueId) {
        this.venueId = venueId;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }

    public boolean isSoldOut() {
        return isSoldOut;
    }

    public void setSoldOut(boolean soldOut) {
        this.isSoldOut = soldOut;
    }

    // Helper method to get formatted time
    public String getFormattedTime() {
        return startTime + " - " + endTime;
    }

    // Helper method to get seat availability percentage
    public int getAvailabilityPercentage() {
        if (totalSeats == 0) return 0;
        return (int) ((availableSeats * 100.0f) / totalSeats);
    }
}
