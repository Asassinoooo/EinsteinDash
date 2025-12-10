package com.EinsteinDash.frontend.utils;

import java.util.HashMap;

/**
 * Session - Menyimpan data user yang sedang login (Singleton Pattern).
 * Data ini tersedia di seluruh aplikasi selama runtime.
 */
public class Session {

    // === SINGLETON ===
    private static Session instance;

    private Session() {
    } // Private constructor

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
    private int totalCoins;
    private boolean isLoggedIn = false;
    private boolean isGuest = false;

    // Cache progress level (levelId -> coins)
    private HashMap<Integer, Integer> localProgress = new HashMap<>();

    // ==================== SETTERS ====================

    /** Set Guest Mode yang offline-capable */
    public void setGuestMode() {
        this.userId = -1;
        this.username = "Guest";
        this.totalStars = 0;
        this.totalCoins = 0;
        this.isLoggedIn = true;
        this.isGuest = true;
        this.localProgress.clear();
    }

    /**
     * Simpan data user setelah login berhasil.
     */
    public void setUserData(int id, String username, int stars, int coins) {
        this.userId = id;
        this.username = username;
        this.totalStars = stars;
        this.totalCoins = coins;
        this.isLoggedIn = true;
        this.isGuest = false; // Reset guest flag
    }

    /**
     * Simpan progress level ke cache lokal.
     * Hanya update jika coins lebih tinggi dari sebelumnya.
     */
    /** Tambahkan bintang (untuk Guest Mode / Local update) */
    public void addStars(int amount) {
        this.totalStars += amount;
    }

    /** Tambahkan koin (untuk Guest Mode / Local update) */
    public void addCoins(int amount) {
        this.totalCoins += amount;
    }

    /** Simpan progress level secara lokal (cache) */
    public void saveLocalProgress(int levelId, int coinsCollected) {
        // Jika level ini sudah ada, update jika koin lebih banyak
        if (localProgress.containsKey(levelId)) {
            int currentBest = localProgress.get(levelId);
            if (coinsCollected > currentBest) {
                localProgress.put(levelId, coinsCollected);
            }
        } else {
            // Level baru selesai
            localProgress.put(levelId, coinsCollected);
        }
    }

    // ==================== GETTERS ====================

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public int getTotalStars() {
        return totalStars;
    }

    public int getTotalCoins() {
        return totalCoins;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public boolean isGuest() {
        return isGuest;
    }

    /** Cek apakah level sudah pernah diselesaikan */
    public boolean isLevelCompleted(int levelId) {
        return localProgress.containsKey(levelId);
    }

    /** Ambil jumlah koin terbaik untuk level tertentu */
    public int getLevelBestCoins(int levelId) {
        return localProgress.getOrDefault(levelId, 0);
    }
}
