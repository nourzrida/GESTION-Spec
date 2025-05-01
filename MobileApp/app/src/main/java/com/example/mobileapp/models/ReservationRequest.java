package com.example.mobileapp.models;

import java.io.Serializable;
import java.util.List;

public class ReservationRequest implements Serializable {
    private int eventId;
    private List<String> seats;
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
