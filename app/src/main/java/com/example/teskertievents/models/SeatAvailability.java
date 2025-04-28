package com.example.teskertievents.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class SeatAvailability implements Serializable {
    @SerializedName("availableSeats")
    private List<String> availableSeats;
    
    @SerializedName("reservedSeats")
    private List<String> reservedSeats;

    // Constructors
    public SeatAvailability() {
    }

    public SeatAvailability(List<String> availableSeats, List<String> reservedSeats) {
        this.availableSeats = availableSeats;
        this.reservedSeats = reservedSeats;
    }

    // Getters and Setters
    public List<String> getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(List<String> availableSeats) {
        this.availableSeats = availableSeats;
    }

    public List<String> getReservedSeats() {
        return reservedSeats;
    }

    public void setReservedSeats(List<String> reservedSeats) {
        this.reservedSeats = reservedSeats;
    }

    // Helper method to check if a seat is available
    public boolean isSeatAvailable(String seatId) {
        return availableSeats != null && availableSeats.contains(seatId);
    }

    // Helper method to check if a seat is reserved
    public boolean isSeatReserved(String seatId) {
        return reservedSeats != null && reservedSeats.contains(seatId);
    }
}
