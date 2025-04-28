package com.example.teskertievents.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ReservationRequest {
    @SerializedName("eventId")
    private int eventId;
    
    @SerializedName("seats")
    private List<String> seats;
    
    @SerializedName("sessionId")
    private String sessionId;

    // Constructors
    public ReservationRequest() {
    }

    public ReservationRequest(int eventId, List<String> seats, String sessionId) {
        this.eventId = eventId;
        this.seats = seats;
        this.sessionId = sessionId;
    }

    // Getters and Setters
    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public List<String> getSeats() {
        return seats;
    }

    public void setSeats(List<String> seats) {
        this.seats = seats;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
