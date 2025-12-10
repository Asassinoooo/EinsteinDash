package com.EinsteinDash.frontend.utils;

import java.util.HashMap;

/**
 * Session - Menyimpan data user yang sedang login (Singleton Pattern).
 * Data ini tersedia di seluruh aplikasi selama runtime.
 */
public class Session {

    // === SINGLETON ===
    private static Session instance;

    private Session() {} // Private constructor

    public static Session getInstance() {
        if (instance == null) {
            instance = new Session();
        }
        return instance;
    }

    // === USER DATA ===
    private int userId;
    private String username;
    private int totalStars;
    private boolean isLoggedIn = false;

    // Cache progress level (levelId -> coins)
    private HashMap<Integer, Integer> localProgress = new HashMap<>();

    // ==================== SETTERS ====================

    /**
     * Simpan data user setelah login berhasil.
     */
    public void setUserData(int id, String username, int stars) {
        this.userId = id;
        this.username = username;
        this.totalStars = stars;
        this.isLoggedIn = true;
    }

    /**
     * Simpan progress level ke cache lokal.
     * Hanya update jika coins lebih tinggi dari sebelumnya.
     */
    public void saveLocalProgress(int levelId, int coins) {
        int currentBest = localProgress.getOrDefault(levelId, -1);
        if (coins > currentBest) {
            localProgress.put(levelId, coins);
        }
    }

    // ==================== GETTERS ====================

    public int getUserId() { return userId; }
    public String getUsername() { return username; }
    public int getTotalStars() { return totalStars; }
    public boolean isLoggedIn() { return isLoggedIn; }

    /** Cek apakah level sudah pernah diselesaikan */
    public boolean isLevelCompleted(int levelId) {
        return localProgress.containsKey(levelId);
    }

    /** Ambil jumlah koin terbaik untuk level tertentu */
    public int getLevelBestCoins(int levelId) {
        return localProgress.getOrDefault(levelId, 0);
    }
}
