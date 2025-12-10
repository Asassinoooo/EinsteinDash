package com.EinsteinDash.frontend.model;

/**
 * ProgressDto - Data Transfer Object untuk progress user pada level.
 * Berisi informasi completed status, coins, dan attempts.
 */
public class ProgressDto {

    // === FIELDS ===
    private int id;
    private int percentage;
    private int attempts;
    private boolean completed;
    private int coinsCollected;
    private LevelPartialDto level;  // Nested level reference

    // ==================== GETTERS ====================

    public int getLevelId() {
        return level != null ? level.getId() : -1;
    }

    public boolean isCompleted() { return completed; }
    public int getCoinsCollected() { return coinsCollected; }
    public int getPercentage() { return percentage; }
    public int getAttempts() { return attempts; }

    // ==================== NESTED CLASS ====================

    /**
     * LevelPartialDto - Subset dari LevelDto yang ada di dalam Progress.
     * Hanya berisi ID untuk referensi.
     */
    public static class LevelPartialDto {
        private int id;
        public int getId() { return id; }
    }
}
