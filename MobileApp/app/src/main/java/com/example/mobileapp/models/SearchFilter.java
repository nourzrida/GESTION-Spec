package com.example.mobileapp.models;

import java.io.Serializable;

public class SearchFilter implements Serializable {
    private String title;
    private String place;
    private String category;
    private String startDate;
    private String endDate;

    // Constructors
    public SearchFilter() {
    }

    public SearchFilter(String title, String place, String category, String startDate, String endDate) {
        this.title = title;
        this.place = place;
        this.category = category;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Getters and Setters
    public String getTitle() {
        return title != null ? title : "";
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPlace() {
        return place != null ? place : "";
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getCategory() {
        return category != null ? category : "";
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getStartDate() {
        return startDate != null ? startDate : "";
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate != null ? endDate : "";
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "SearchFilter{" +
                "title='" + title + '\'' +
                ", place='" + place + '\'' +
                ", category='" + category + '\'' +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                '}';
    }
}
