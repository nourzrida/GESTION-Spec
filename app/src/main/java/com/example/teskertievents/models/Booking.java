package com.example.teskertievents.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Booking implements Serializable {
    @SerializedName("bookingReference")
    private String bookingReference;
    
    @SerializedName("eventId")
    private int eventId;
    
    @SerializedName("eventTitle")
    private String eventTitle;
    
    @SerializedName("eventDate")
    private String eventDate;
    
    @SerializedName("eventVenue")
    private String eventVenue;
    
    @SerializedName("seats")
    private List<String> seats;
    
    @SerializedName("tickets")
    private List<BookingTicket> tickets;
    
    @SerializedName("totalPrice")
    private double totalPrice;
    
    @SerializedName("bookingDate")
    private String bookingDate;

    // Constructors
    public Booking() {
    }

    // Getters and Setters
    public String getBookingReference() {
        return bookingReference;
    }

    public void setBookingReference(String bookingReference) {
        this.bookingReference = bookingReference;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventVenue() {
        return eventVenue;
    }

    public void setEventVenue(String eventVenue) {
        this.eventVenue = eventVenue;
    }

    public List<String> getSeats() {
        return seats;
    }

    public void setSeats(List<String> seats) {
        this.seats = seats;
    }

    public List<BookingTicket> getTickets() {
        return tickets;
    }

    public void setTickets(List<BookingTicket> tickets) {
        this.tickets = tickets;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }

    // Inner class for booking tickets
    public static class BookingTicket implements Serializable {
        private String name;
        private String description;
        private int quantity;
        
        @SerializedName("pricePerTicket")
        private double pricePerTicket;

        public BookingTicket() {
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public double getPricePerTicket() {
            return pricePerTicket;
        }

        public void setPricePerTicket(double pricePerTicket) {
            this.pricePerTicket = pricePerTicket;
        }
    }
}
