package com.example.teskertievents.models;

import com.google.gson.annotations.SerializedName;

public class RegisterResponse {
    private boolean success;
    private String message;
    
    @SerializedName("userId")
    private int userId;

    public RegisterResponse() {
    }

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

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
