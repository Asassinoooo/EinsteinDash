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

        // Level 3: Mechanic Test (Guest)
        LevelDto l3 = new LevelDto();
        l3.setId(3);
        l3.setLevelName("Mechanic Test (Guest)");
        l3.setStars(3);
        l3.setAudioTrackId(102); // Reusing track
        l3.setCompleted(false);
        l3.setCoinsCollected(0);
        l3.setLevelData(
                """
                                                            [
                                {"type": "BLOCK", "x": 0, "y": -1}, {"type": "BLOCK", "x": 1, "y": -1}, {"type": "BLOCK", "x": 2, "y": -1}, {"type": "BLOCK", "x": 3, "y": -1}, {"type": "BLOCK", "x": 4, "y": -1},
                                {"type": "BLOCK", "x": 5, "y": -1}, {"type": "BLOCK", "x": 6, "y": -1}, {"type": "BLOCK", "x": 7, "y": -1}, {"type": "BLOCK", "x": 8, "y": -1}, {"type": "BLOCK", "x": 9, "y": -1},

                                {"type": "BLOCK", "x": 10, "y": 0},

                                // 1. GRAVITY REVERSE (Start at Floor)
                                {"type": "PORTAL_GRAVITY_UP", "x": 15, "y": 1},

                                // ALL NEXT PORTALS AT Y=4.5 (Ceiling Height for 2.15f)

                                // 2. CUBE MODE SPEEDS (Reverse - Ceiling)
                                {"type": "PORTAL_SPEED_0_5", "x": 25, "y": 4.5},
                                {"type": "PORTAL_SPEED_1", "x": 45, "y": 4.5},
                                {"type": "PORTAL_SPEED_2", "x": 65, "y": 4.5},
                                {"type": "PORTAL_SPEED_3", "x": 85, "y": 4.5},
                                {"type": "PORTAL_SPEED_4", "x": 105, "y": 4.5},

                                // 3. SHIP MODE SPEEDS
                                {"type": "PORTAL_SHIP", "x": 125, "y": 4.5},
                                {"type": "PORTAL_SPEED_0_5", "x": 135, "y": 4.5},
                                {"type": "PORTAL_SPEED_1", "x": 155, "y": 4.5},
                                {"type": "PORTAL_SPEED_2", "x": 175, "y": 4.5},
                                {"type": "PORTAL_SPEED_3", "x": 195, "y": 4.5},
                                {"type": "PORTAL_SPEED_4", "x": 215, "y": 4.5},

                                // 4. BALL MODE SPEEDS
                                {"type": "PORTAL_BALL", "x": 235, "y": 4.5},
                                {"type": "PORTAL_SPEED_0_5", "x": 245, "y": 4.5},
                                {"type": "PORTAL_SPEED_1", "x": 265, "y": 4.5},
                                {"type": "PORTAL_SPEED_2", "x": 285, "y": 4.5},
                                {"type": "PORTAL_SPEED_3", "x": 305, "y": 4.5},
                                {"type": "PORTAL_SPEED_4", "x": 325, "y": 4.5},

                                // 5. UFO MODE SPEEDS
                                {"type": "PORTAL_UFO", "x": 345, "y": 4.5},
                                {"type": "PORTAL_SPEED_0_5", "x": 355, "y": 4.5},
                                {"type": "PORTAL_SPEED_1", "x": 375, "y": 4.5},
                                {"type": "PORTAL_SPEED_2", "x": 395, "y": 4.5},
                                {"type": "PORTAL_SPEED_3", "x": 415, "y": 4.5},
                                {"type": "PORTAL_SPEED_4", "x": 435, "y": 4.5},

                                // 6. WAVE MODE SPEEDS
                                {"type": "PORTAL_WAVE", "x": 455, "y": 4.5},
                                {"type": "PORTAL_SPEED_0_5", "x": 465, "y": 4.5},
                                {"type": "PORTAL_SPEED_1", "x": 485, "y": 4.5},
                                {"type": "PORTAL_SPEED_2", "x": 505, "y": 4.5},
                                {"type": "PORTAL_SPEED_3", "x": 525, "y": 4.5},
                                {"type": "PORTAL_SPEED_4", "x": 545, "y": 4.5},

                                // 7. ROBOT MODE SPEEDS
                                {"type": "PORTAL_ROBOT", "x": 565, "y": 4.5},
                                {"type": "PORTAL_SPEED_0_5", "x": 575, "y": 4.5},
                                {"type": "PORTAL_SPEED_1", "x": 595, "y": 4.5},
                                {"type": "PORTAL_SPEED_2", "x": 615, "y": 4.5},
                                {"type": "PORTAL_SPEED_3", "x": 635, "y": 4.5},
                                {"type": "PORTAL_SPEED_4", "x": 655, "y": 4.5},

                                // 8. SPIDER MODE SPEEDS
                                {"type": "PORTAL_SPIDER", "x": 675, "y": 4.5},
                                {"type": "PORTAL_SPEED_0_5", "x": 685, "y": 4.5},
                                {"type": "PORTAL_SPEED_1", "x": 705, "y": 4.5},
                                {"type": "PORTAL_SPEED_2", "x": 725, "y": 4.5},
                                {"type": "PORTAL_SPEED_3", "x": 745, "y": 4.5},
                                {"type": "PORTAL_SPEED_4", "x": 765, "y": 4.5},

                                {"type": "GOAL",  "x": 800, "y": 0}
                            ]
                        """);
        levels.add(l3);

        return levels;
    }
}
