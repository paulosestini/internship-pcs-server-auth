package com.poli.internship.domain.models;

public class AuthTokenPayload {
    private String userId;
    private String email;
    private int expiresIn;

    public AuthTokenPayload(String userId, String email, int expiresIn) {
        this.userId = userId;
        this.email = email;
        this.expiresIn = expiresIn;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(int expiresIn) {
        this.expiresIn = expiresIn;
    }
}
