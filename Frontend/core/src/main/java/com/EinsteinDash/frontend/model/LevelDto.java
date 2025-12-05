package com.EinsteinDash.frontend.model;

public class LevelDto {
    private int id;
    private String levelName;
    private String levelData; // JSON string layout level
    private int stars;
    private int audioTrackId;

    public LevelDto() {}

    // Getters
    public int getId() { return id; }
    public String getLevelName() { return levelName; }
    public String getLevelData() { return levelData; }
    public int getStars() { return stars; }
    public int getAudioTrackId() { return audioTrackId; }

    // toString untuk debugging
    @Override
    public String toString() {
        return levelName + " (" + stars + "*)";
    }
}
