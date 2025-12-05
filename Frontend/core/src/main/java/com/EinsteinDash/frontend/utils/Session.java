package com.EinsteinDash.frontend.utils;

public class Session {
    // Instance tunggal (Singleton)
    private static Session instance;

    // Data User
    private int userId;
    private String username;
    private int totalStars;
    private boolean isLoggedIn = false;

    // Constructor Private
    private Session() {}

    // Method untuk akses instance
    public static Session getInstance() {
        if (instance == null) {
            instance = new Session();
        }
        return instance;
    }

    public void setUserData(int id, String username, int stars) {
        this.userId = id;
        this.username = username;
        this.totalStars = stars;
        this.isLoggedIn = true;
    }

    // Getters
    public int getUserId() { return userId; }
    public String getUsername() { return username; }
    public int getTotalStars() { return totalStars; }
    public boolean isLoggedIn() { return isLoggedIn; }
}
