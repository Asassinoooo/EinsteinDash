package com.EinsteinDash.frontend.model;

public class ProgressDto {
    private int id;
    private int percentage;
    private int attempts;
    private boolean completed; // atau 'completed' tergantung JSON backend
    private int coinsCollected;


    private LevelPartialDto level;

    // Getter
    public int getLevelId() {
        return level != null ? level.getId() : -1;
    }

    public boolean isCompleted() { return completed; }
    public int getCoinsCollected() { return coinsCollected; }

    // Helper class untuk nested object "level" inside progress
    public static class LevelPartialDto {
        private int id;
        public int getId() { return id; }
    }
}
