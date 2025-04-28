package com.example.teskertievents.models;

import com.google.gson.annotations.SerializedName;

public class PaymentResponse {
    @SerializedName("success")
    private boolean success;
    
    @SerializedName("message")
    private String message;
    
    @SerializedName("bookingReference")
    private String bookingReference;
    
    @SerializedName("qrCodeData")
    private String qrCodeData;

    // Constructors
    public PaymentResponse() {
    }

    public PaymentResponse(boolean success, String message, String bookingReference, String qrCodeData) {
        this.success = success;
        this.message = message;
        this.bookingReference = bookingReference;
        this.qrCodeData = qrCodeData;
    }

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getBookingReference() {
        return bookingReference;
    }

    public void setBookingReference(String bookingReference) {
        this.bookingReference = bookingReference;
    }

    public String getQrCodeData() {
        return qrCodeData;
    }

    public void setQrCodeData(String qrCodeData) {
        this.qrCodeData = qrCodeData;
    }
}
