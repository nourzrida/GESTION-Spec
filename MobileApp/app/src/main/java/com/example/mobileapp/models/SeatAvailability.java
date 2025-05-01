package com.example.mobileapp.models;

import java.io.Serializable;
import java.util.List;

public class SeatAvailability implements Serializable {
    private int eventId;
    private List<String> reservedSeats;
    private int row;
    private int column;
    private String seatNumber;
    private int ticketTypeId;

    // Constructors
    public SeatAvailability() {
    }

    public SeatAvailability(int eventId, List<String> reservedSeats) {
        this.eventId = eventId;
        this.reservedSeats = reservedSeats;
    }

    // Getters and Setters for existing fields
    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public List<String> getReservedSeats() {
        return reservedSeats;
    }

    public void setReservedSeats(List<String> reservedSeats) {
        this.reservedSeats = reservedSeats;
    }

    // New getters and setters for row and column
    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    // New getters and setters for seatNumber
    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    // New getters and setters for ticketTypeId
    public int getTicketTypeId() {
        return ticketTypeId;
    }

    public void setTicketTypeId(int ticketTypeId) {
        this.ticketTypeId = ticketTypeId;
    }

    // Helper method to check if a seat is reserved
    public boolean isSeatReserved(String seatId) {
        return reservedSeats != null && reservedSeats.contains(seatId);
    }
}
