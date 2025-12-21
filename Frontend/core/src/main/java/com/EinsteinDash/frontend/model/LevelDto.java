package com.EinsteinDash.frontend.model;

/**
 * LevelDto - Data Transfer Object untuk informasi level.
 * Digunakan untuk transfer data dari Backend ke Frontend.
 */
public class LevelDto {

    // === FIELDS ===
    private int id;
    private String levelName;
    private String levelData; // JSON string layout level
    private int stars; // Difficulty rating
    private int audioTrackId;
    private boolean iscompleted;
    private int coinsCollected;


    /** Default constructor untuk JSON parsing */
    public LevelDto() {
    }

    // ==================== GETTERS ====================

    public int getId() {
        return id;
    }

    public String getLevelName() {
        return levelName;
    }

    public String getLevelData() {
        return levelData;
    }

    public int getStars() {
        return stars;
    }

    public int getAudioTrackId() {
        return audioTrackId;
    }

    public boolean isCompleted() {
        return iscompleted;
    }

    public int getCoinsCollected() {
        return coinsCollected;
    }

    // ==================== SETTERS ====================

    public void setCompleted(boolean completed) {
        this.iscompleted = completed;
    }

    public void setCoinsCollected(int coinsCollected) {
        this.coinsCollected = coinsCollected;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public void setLevelData(String levelData) {
        this.levelData = levelData;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public void setAudioTrackId(int audioTrackId) {
        this.audioTrackId = audioTrackId;
    }

    @Override
    public String toString() {
        return levelName + " (" + stars + "*)";
    }
}
