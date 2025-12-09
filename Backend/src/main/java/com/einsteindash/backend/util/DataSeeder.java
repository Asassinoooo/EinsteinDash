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
                        {"type": "GOAL",  "x": 100, "y": 0},
                        {"type": "PORTAL_SHIP", "x": 40, "y": 2},
                        {"type": "PORTAL_CUBE", "x": 1, "y": 0},
                        {"type": "SPIKE", "x": 51, "y": 0},
                        {"type": "SPIKE", "x": 52, "y": 0},
                        {"type": "SPIKE", "x": 53, "y": 0},
                        {"type": "SPIKE", "x": 54, "y": 0},
                        {"type": "SPIKE", "x": 55, "y": 0},
                        {"type": "SPIKE", "x": 56, "y": 0},
                        {"type": "SPIKE", "x": 57, "y": 0},
                        {"type": "SPIKE", "x": 58, "y": 0},
                        {"type": "SPIKE", "x": 51, "y": 2},
                        {"type": "SPIKE", "x": 52, "y": 2},
                        {"type": "SPIKE", "x": 53, "y": 2},
                        {"type": "SPIKE", "x": 54, "y": 2},
                        {"type": "SPIKE", "x": 55, "y": 2},
                        {"type": "SPIKE", "x": 56, "y": 2},
                        {"type": "SPIKE", "x": 57, "y": 2},
                        {"type": "SPIKE", "x": 58, "y": 2},
                        {"type": "COIN",  "x": 50, "y": 1},
                        {"type": "PORTAL_CUBE", "x": 60, "y": 0},
                        {"type": "SPIKE", "x": 65, "y": 0},
                        {"type": "SPIKE", "x": 66, "y": 0},
                        {"type": "SPIKE", "x": 69, "y": 0},
                        {"type": "SPIKE", "x": 70, "y": 0},
                        {"type": "SPIKE", "x": 73, "y": 0},
                        {"type": "SPIKE", "x": 74, "y": 0},
                        {"type": "SPIKE", "x": 77, "y": 0},
                        {"type": "SPIKE", "x": 78, "y": 0},
                        {"type": "SPIKE", "x": 81, "y": 0},
                        {"type": "SPIKE", "x": 82, "y": 0},
                        {"type": "SPIKE", "x": 85, "y": 0},
                        {"type": "SPIKE", "x": 86, "y": 0}]
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