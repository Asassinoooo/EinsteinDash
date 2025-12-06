package com.EinsteinDash.frontend.utils;

import java.util.HashMap;

public class Session {
    // Instance tunggal (Singleton)
    private static Session instance;

    // Data User
    private int userId;
    private String username;
    private int totalStars;
    private boolean isLoggedIn = false;
    private HashMap<Integer, Integer> localProgress = new HashMap<>();

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

    // Menyimpan progress ke memori
    public void saveLocalProgress(int levelId, int coins) {
        // Hanya simpan jika lebih besar dari yang sudah ada
        int currentBest = localProgress.getOrDefault(levelId, -1);
        if (coins > currentBest) {
            localProgress.put(levelId, coins);
        }
    }

    // Cek apakah level sudah completed
    public boolean isLevelCompleted(int levelId) {
        return localProgress.containsKey(levelId);
    }

    // Ambil koin terbaik
    public int getLevelBestCoins(int levelId) {
        return localProgress.getOrDefault(levelId, 0);
    }

    // Getters
    public int getUserId() { return userId; }
    public String getUsername() { return username; }
    public int getTotalStars() { return totalStars; }
    public boolean isLoggedIn() { return isLoggedIn; }
}
