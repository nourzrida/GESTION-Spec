package com.example.mobileapp.models;

import java.io.Serializable;

public class RegisterResponse implements Serializable {
    private boolean success;
    private String message;
    private User user;

    // Constructors
    public RegisterResponse() {
    }

    public RegisterResponse(boolean success, String message, User user) {
        this.success = success;
        this.message = message;
        this.user = user;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
