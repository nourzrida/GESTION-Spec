package com.example.mobileapp.models;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class BookingRequest implements Serializable {
    private int eventId;
    private List<String> seats;
    private Map<String, Integer> ticketTypes;
    private String sessionId;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private String paymentMethod;
    private String cardNumber;
    private String cardExpiry;
    private String cardCvv;
    private double totalPrice;

    // Nested classes for customer and payment info
    public static class CustomerInfo implements Serializable {
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

    public static class PaymentInfo implements Serializable {
        private String cardNumber;
        private String expiryDate;
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

    // Constructors
    public BookingRequest() {
    }

    public BookingRequest(int eventId, List<String> seats, Map<String, Integer> ticketTypes,
                          String sessionId, CustomerInfo customerInfo, PaymentInfo paymentInfo, double totalPrice) {
        this.eventId = eventId;
        this.seats = seats;
        this.ticketTypes = ticketTypes;
        this.sessionId = sessionId;
        this.customerName = customerInfo.getName();
        this.customerEmail = customerInfo.getEmail();
        this.customerPhone = customerInfo.getPhone();
        this.cardNumber = paymentInfo.getCardNumber();
        this.cardExpiry = paymentInfo.getExpiryDate();
        this.cardCvv = paymentInfo.getCvv();
        this.paymentMethod = "card"; // Default payment method
        this.totalPrice = totalPrice;
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

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardExpiry() {
        return cardExpiry;
    }

    public void setCardExpiry(String cardExpiry) {
        this.cardExpiry = cardExpiry;
    }

    public String getCardCvv() {
        return cardCvv;
    }

    public void setCardCvv(String cardCvv) {
        this.cardCvv = cardCvv;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
}
