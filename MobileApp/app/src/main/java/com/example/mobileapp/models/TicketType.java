package com.example.mobileapp.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TicketType implements Serializable {
    private int id;

    @SerializedName("event_id")
    private int eventId;

    private String name;
    private double price;
    private String description;

    @SerializedName("available_seats")
    private int availableSeats;

    @SerializedName("total_seats")
    private int totalSeats;

    private int selectedQuantity = 0; // For UI tracking

    // Constructors
    public TicketType() {
    }

    public TicketType(int id, int eventId, String name, double price, int availableSeats, int totalSeats, String description) {
        this.id = id;
        this.eventId = eventId;
        this.name = name;
        this.price = price;
        this.availableSeats = availableSeats;
        this.totalSeats = totalSeats;
        this.description = description;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getSelectedQuantity() {
        return selectedQuantity;
    }

    public void setSelectedQuantity(int selectedQuantity) {
        this.selectedQuantity = selectedQuantity;
    }
}
