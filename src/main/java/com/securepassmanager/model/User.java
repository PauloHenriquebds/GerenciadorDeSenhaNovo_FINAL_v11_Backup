package com.securepassmanager.model;

public class User {
    private String username;
    private String secretKey;

    public User(String username, String secretKey) {
        this.username = username;
        this.secretKey = secretKey;
    }

    public String getUsername() {
        return username;
    }

    public String getSecretKey() {
        return secretKey;
    }
}