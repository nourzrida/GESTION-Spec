package com.example.mobileapp.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Event implements Serializable {
    private int id;
    private String title;
    private String description;

    @SerializedName("date")
    private String eventDate;

    private String venue;

    @SerializedName("venue_id")
    private int venueId;

    @SerializedName("image_url")
    private String imageUrl;

    private double price;
    private String category;

    @SerializedName("is_featured")
    private boolean isFeatured;

    @SerializedName("is_sold_out")
    private boolean isSoldOut;

    // Constructors
    public Event() {
    }

    public Event(int id, String title, String description, String eventDate, String venue,
                 int venueId, String imageUrl, double price, String category,
                 boolean isFeatured, boolean isSoldOut) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.eventDate = eventDate;
        this.venue = venue;
        this.venueId = venueId;
        this.imageUrl = imageUrl;
        this.price = price;
        this.category = category;
        this.isFeatured = isFeatured;
        this.isSoldOut = isSoldOut;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public int getVenueId() {
        return venueId;
    }

    public void setVenueId(int venueId) {
        this.venueId = venueId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isFeatured() {
        return isFeatured;
    }

    public void setFeatured(boolean featured) {
        this.isFeatured = featured;
    }

    public boolean isSoldOut() {
        return isSoldOut;
    }

    public void setSoldOut(boolean soldOut) {
        this.isSoldOut = soldOut;
    }
}
