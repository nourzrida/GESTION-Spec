package com.example.teskertievents.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

public class BookingRequest {
    @SerializedName("eventId")
    private int eventId;
    
    @SerializedName("selectedSeats")
    private List<String> selectedSeats;
    
    @SerializedName("ticketTypes")
    private Map<String, Integer> ticketTypes;
    
    @SerializedName("sessionId")
    private String sessionId;
    
    @SerializedName("customerInfo")
    private CustomerInfo customerInfo;
    
    @SerializedName("paymentInfo")
    private PaymentInfo paymentInfo;

    // Constructors
    public BookingRequest() {
    }

    public BookingRequest(int eventId, List<String> selectedSeats, Map<String, Integer> ticketTypes,
                         String sessionId, CustomerInfo customerInfo, PaymentInfo paymentInfo) {
        this.eventId = eventId;
        this.selectedSeats = selectedSeats;
        this.ticketTypes = ticketTypes;
        this.sessionId = sessionId;
        this.customerInfo = customerInfo;
        this.paymentInfo = paymentInfo;
    }

    // Getters and Setters
    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public List<String> getSelectedSeats() {
        return selectedSeats;
    }

    public void setSelectedSeats(List<String> selectedSeats) {
        this.selectedSeats = selectedSeats;
    }

    public Map<String, Integer> getTicketTypes() {
        return ticketTypes;
    }

    public void setTicketTypes(Map<String, Integer> ticketTypes) {
        this.ticketTypes = ticketTypes;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public CustomerInfo getCustomerInfo() {
        return customerInfo;
    }

    public void setCustomerInfo(CustomerInfo customerInfo) {
        this.customerInfo = customerInfo;
    }

    public PaymentInfo getPaymentInfo() {
        return paymentInfo;
    }

    public void setPaymentInfo(PaymentInfo paymentInfo) {
        this.paymentInfo = paymentInfo;
    }

    // Inner classes for customer and payment info
    public static class CustomerInfo {
        private String name;
        private String email;
        private String phone;

        public CustomerInfo() {
        }

        public CustomerInfo(String name, String email, String phone) {
            this.name = name;
            this.email = email;
            this.phone = phone;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }
    }

    public static class PaymentInfo {
        @SerializedName("cardNumber")
        private String cardNumber;
        
        @SerializedName("expiryDate")
        private String expiryDate;
        
        @SerializedName("cvv")
        private String cvv;

        public PaymentInfo() {
        }

        public PaymentInfo(String cardNumber, String expiryDate, String cvv) {
            this.cardNumber = cardNumber;
            this.expiryDate = expiryDate;
            this.cvv = cvv;
        }

        public String getCardNumber() {
            return cardNumber;
        }

        public void setCardNumber(String cardNumber) {
            this.cardNumber = cardNumber;
        }

        public String getExpiryDate() {
            return expiryDate;
        }

        public void setExpiryDate(String expiryDate) {
            this.expiryDate = expiryDate;
        }

        public String getCvv() {
            return cvv;
        }

        public void setCvv(String cvv) {
            this.cvv = cvv;
        }
    }
}
