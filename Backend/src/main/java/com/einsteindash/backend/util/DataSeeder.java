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
            if (repository.count() == 0) {
                Level l1 = new Level();
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
                        {"type": "GOAL",  "x": 50, "y": 0},
                        {"type": "PORTAL_SHIP", "x": 40, "y": 2},
                        {"type": "PORTAL_CUBE", "x": 1, "y": 0}
                    ]
                """);
                l1.setStars(1); // Easy
                l1.setAudioTrackId(101);
                repository.save(l1);

                Level l2 = new Level();
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
                        {"type": "SPIKE", "x": 42, "y": 0},
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