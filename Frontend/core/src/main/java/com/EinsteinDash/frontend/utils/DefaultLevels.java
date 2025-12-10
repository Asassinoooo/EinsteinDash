package com.EinsteinDash.frontend.utils;

import com.EinsteinDash.frontend.model.LevelDto;
import java.util.ArrayList;

/**
 * DefaultLevels - Menyediakan level bawaan untuk Guest Mode / Offline.
 * Data diambil dari backend DataSeeder agar konsisten.
 */
public class DefaultLevels {

    public static ArrayList<LevelDto> getDefaults() {
        ArrayList<LevelDto> levels = new ArrayList<>();

        // Level 1: Stereo Madness
        LevelDto l1 = new LevelDto();
        // ID negatif untuk menandakan level lokal (optional)
        // Tapi kita pakai ID positif standar agar logika play tidak error
        l1.setId(1);
        l1.setLevelName("Stereo Madness (Guest)");
        l1.setStars(1);
        l1.setAudioTrackId(1001);
        l1.setCompleted(false);
        l1.setCoinsCollected(0);
        l1.setLevelData("""
                    [
                        {"type": "BLOCK", "x": 10, "y": 0},
                        {"type": "BLOCK", "x": 11, "y": 0},
                        {"type": "SPIKE", "x": 15, "y": 0},
                        {"type": "BLOCK", "x": 20, "y": 1},
                        {"type": "SPIKE", "x": 25, "y": 0},
                        {"type": "COIN",  "x": 30, "y": 2},
                        {"type": "SPIKE", "x": 40, "y": 0},
                        {"type": "SPIKE", "x": 41, "y": 0},
                        {"type": "SPIKE", "x": 42, "y": 0},
                        {"type": "SPIKE", "x": 47, "y": 0},
                        {"type": "PORTAL_SHIP", "x": 40, "y": 2},
                        {"type": "SPIKE", "x": 47, "y": 0},
                        {"type": "SPIKE", "x": 48, "y": 0},
                        {"type": "SPIKE", "x": 49, "y": 0},
                        {"type": "SPIKE", "x": 50, "y": 0},
                        {"type": "PORTAL_BALL", "x": 51, "y": 3},
                        {"type": "BLOCK", "x": 70, "y": 5},
                        {"type": "BLOCK", "x": 71, "y": 5},
                        {"type": "BLOCK", "x": 72, "y": 5},
                        {"type": "BLOCK", "x": 73, "y": 5},
                        {"type": "BLOCK", "x": 74, "y": 5},
                        {"type": "PORTAL_UFO", "x": 80, "y": 3},
                        {"type": "BLOCK", "x": 90, "y": 0},
                        {"type": "BLOCK", "x": 90, "y": 1},
                        {"type": "BLOCK", "x": 90, "y": 2},
                        {"type": "BLOCK", "x": 90, "y": 3},
                        {"type": "SPIKE", "x": 90, "y": 4},
                        {"type": "PORTAL_WAVE", "x": 120, "y": 3},
                        {"type": "BLOCK", "x": 135, "y": 0},
                        {"type": "BLOCK", "x": 135, "y": 1},
                        {"type": "BLOCK", "x": 135, "y": 2},
                        {"type": "BLOCK", "x": 135, "y": 3},
                        {"type": "SPIKE", "x": 135, "y": 4},
                        {"type": "BLOCK", "x": 140, "y": 0},
                        {"type": "BLOCK", "x": 141, "y": 1},
                        {"type": "BLOCK", "x": 142, "y": 2},
                        {"type": "BLOCK", "x": 143, "y": 3},
                        {"type": "SPIKE", "x": 144, "y": 4},
                        {"type": "PORTAL_ROBOT", "x": 150, "y": 3},
                        {"type": "BLOCK", "x": 161, "y": 1},
                        {"type": "BLOCK", "x": 162, "y": 1},
                        {"type": "BLOCK", "x": 163, "y": 1},
                        {"type": "SPIKE", "x": 164, "y": 2},
                        {"type": "PORTAL_SPIDER", "x": 200, "y": 0},
                        {"type": "BLOCK", "x": 161, "y": 1},
                        {"type": "BLOCK", "x": 162, "y": 1},
                        {"type": "BLOCK", "x": 163, "y": 1},
                        {"type": "SPIKE", "x": 164, "y": 2},
                        {"type": "GOAL",  "x": 250, "y": 0}
                    ]
                """);
        levels.add(l1);

        // Level 2: Back on Track
        LevelDto l2 = new LevelDto();
        l2.setId(2);
        l2.setLevelName("Back on Track (Guest)");
        l2.setStars(2);
        l2.setAudioTrackId(102); // Perhatikan ID tracknya, pastikan ada di AudioManager
        l2.setCompleted(false);
        l2.setCoinsCollected(0);
        l2.setLevelData("""
                    [
                        {"type": "SPIKE", "x": 10, "y": 0},
                        {"type": "SPIKE", "x": 11, "y": 0},
                        {"type": "BLOCK", "x": 15, "y": 0},
                        {"type": "BLOCK", "x": 15, "y": 1},
                        {"type": "SPIKE", "x": 20, "y": 0},
                        {"type": "SPIKE", "x": 40, "y": 0},
                        {"type": "SPIKE", "x": 41, "y": 0},
                        {"type": "SPIKE", "x": 47, "y": 0},
                        {"type": "GOAL",  "x": 60, "y": 0}
                    ]
                """);
        levels.add(l2);

        return levels;
    }
}
