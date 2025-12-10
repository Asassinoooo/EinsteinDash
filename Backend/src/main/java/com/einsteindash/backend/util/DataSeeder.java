package com.einsteindash.backend.util;

import com.einsteindash.backend.model.Level;
import com.einsteindash.backend.repository.LevelRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner initDatabase(LevelRepository repository) {
        return args -> {
            if (true) {
                Level l1 = repository.findByLevelName("Stereo Madness").orElse(new Level());
                l1.setLevelName("Stereo Madness");
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
                l1.setStars(1); // Easy
                l1.setAudioTrackId(101);
                repository.save(l1);

                Level l2 = repository.findByLevelName("Back on Track").orElse(new Level());
                l2.setLevelName("Back on Track");
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
                l2.setStars(2); // Normal
                l2.setAudioTrackId(102);
                repository.save(l2);

                System.out.println("Dummy levels created!");
            }
        };
    }
}