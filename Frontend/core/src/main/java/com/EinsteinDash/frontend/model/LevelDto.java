package com.EinsteinDash.frontend.model;

public class LevelDto {
    private int id;
    private String levelName;
    private String levelData; // JSON string layout level
    private int stars;
    private int audioTrackId;
    private boolean iscompleted;
    private int coinsCollected;

    public LevelDto() {}

    // Getters
    public int getId() { return id; }
    public String getLevelName() { return levelName; }
    public String getLevelData() { return levelData; }
    public int getStars() { return stars; }
    public int getAudioTrackId() { return audioTrackId; }
    public boolean isCompleted() { return iscompleted; }
    public void setCompleted(boolean completed) { this.iscompleted = completed; }
    public int getCoinsCollected() { return coinsCollected; }
    public void setCoinsCollected(int coinsCollected) { this.coinsCollected = coinsCollected; }

    // toString untuk debugging
    @Override
    public String toString() {
        return levelName + " (" + stars + "*)";
    }
}
