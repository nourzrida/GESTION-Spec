package com.example.teskertievents.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TicketType implements Serializable {
    private int id;
    private String name;
    private String description;
    private double price;
    
    @SerializedName("maxQuantity")
    private int maxQuantity;
    
    // For UI purposes
    private transient int selectedQuantity = 0;

    // Constructors
    public TicketType() {
    }

    public TicketType(int id, String name, String description, double price, int maxQuantity) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.maxQuantity = maxQuantity;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getMaxQuantity() {
        return maxQuantity;
    }

    public void setMaxQuantity(int maxQuantity) {
        this.maxQuantity = maxQuantity;
    }

    public int getSelectedQuantity() {
        return selectedQuantity;
    }

    public void setSelectedQuantity(int selectedQuantity) {
        this.selectedQuantity = selectedQuantity;
    }

    public void incrementQuantity() {
        if (selectedQuantity < maxQuantity) {
            selectedQuantity++;
        }
    }

    public void decrementQuantity() {
        if (selectedQuantity > 0) {
            selectedQuantity--;
        }
    }
}
