package com.EinsteinDash.frontend.model;

/**
 * LevelDto - Data Transfer Object untuk informasi level.
 * Digunakan untuk transfer data dari Backend ke Frontend.
 */
public class LevelDto {

    // === FIELDS ===
    private int id;
    private String levelName;
    private String levelData;     // JSON string layout level
    private int stars;            // Difficulty rating
    private int audioTrackId;
    private boolean iscompleted;
    private int coinsCollected;

    /** Default constructor untuk JSON parsing */
    public LevelDto() {}

    // ==================== GETTERS ====================

    public int getId() { return id; }
    public String getLevelName() { return levelName; }
    public String getLevelData() { return levelData; }
    public int getStars() { return stars; }
    public int getAudioTrackId() { return audioTrackId; }
    public boolean isCompleted() { return iscompleted; }
    public int getCoinsCollected() { return coinsCollected; }

    // ==================== SETTERS ====================

    public void setCompleted(boolean completed) { this.iscompleted = completed; }
    public void setCoinsCollected(int coinsCollected) { this.coinsCollected = coinsCollected; }

    @Override
    public String toString() {
        return levelName + " (" + stars + "*)";
    }
}
