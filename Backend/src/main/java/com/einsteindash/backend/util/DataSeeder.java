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
                                {"type": "SPIKE", "x": 18, "y": 0},
                                
                                {"type": "SPIKE", "x": 38, "y": 0},
                                {"type": "SPIKE", "x": 39, "y": 0},
                                
                                {"type": "SPIKE", "x": 65, "y": 0},
                                
                                {"type": "SPIKE", "x": 72, "y": 0},
                                {"type": "SPIKE", "x": 73, "y": 0},

                                {"type": "SPIKE", "x": 90, "y": 0},
                                
                                {"type": "SPIKE", "x": 109, "y": 0},
                                {"type": "SPIKE", "x": 110 , "y": 0},
                                
                                {"type": "SPIKE", "x": 135, "y": 0},
                                
                                {"type": "PORTAL_SPEED_2", "x": 142, "y": 0},
                                {"type": "PORTAL_SHIP", "x": 143, "y": 0},
                                
                                {"type": "BLOCK", "x": 150, "y": 0}, {"type": "SPIKE", "x": 150, "y": 1},
                                {"type": "BLOCK", "x": 150, "y": 5}, {"type": "SPIKE_UPSIDE", "x": 150, "y": 4},
                                {"type": "BLOCK", "x": 151, "y": 0},
                                {"type": "BLOCK", "x": 151, "y": 5},
                                {"type": "BLOCK", "x": 152, "y": 0},
                                {"type": "BLOCK", "x": 152, "y": 5},
                                {"type": "BLOCK", "x": 153, "y": 0},
                                {"type": "BLOCK", "x": 153, "y": 5},
                                {"type": "BLOCK", "x": 154, "y": 0},
                                {"type": "BLOCK", "x": 154, "y": 5},
                                {"type": "BLOCK", "x": 155, "y": 0}, {"type": "BLOCK", "x": 155, "y": 1},
                                
                                {"type": "BLOCK", "x": 156, "y": 0}, {"type": "BLOCK", "x": 156, "y": 1},
                                
                                {"type": "BLOCK", "x": 157, "y": 0}, {"type": "BLOCK", "x": 157, "y": 1}, {"type": "BLOCK", "x": 157, "y": 2},
                                
                                {"type": "BLOCK", "x": 158, "y": 0}, {"type": "BLOCK", "x": 158, "y": 1}, {"type": "BLOCK", "x": 158, "y": 2},
                                
                                {"type": "BLOCK", "x": 159, "y": 0}, {"type": "BLOCK", "x": 159, "y": 1}, {"type": "BLOCK", "x": 159, "y": 2},
                                
                                {"type": "BLOCK", "x": 160, "y": 0}, {"type": "BLOCK", "x": 160, "y": 1}, {"type": "BLOCK", "x": 160, "y": 2},
                                
                                {"type": "BLOCK", "x": 161, "y": 0}, {"type": "BLOCK", "x": 161, "y": 1}, {"type": "BLOCK", "x": 161, "y": 2}
                                
                            ]
                        """);
                l1.setStars(1); // Easy
                l1.setAudioTrackId(1001);
                repository.save(l1);

                Level l2 = repository.findByLevelName("Back on Track").orElse(new Level());
                l2.setLevelName("Back on Track");
                l2.setLevelData("""
                            [
                                {"type": "SPIKE", "x": 18, "y": 0},
                                {"type": "SPIKE", "x": 33, "y": 0},
                                {"type": "SPIKE", "x": 34, "y": 0},
                                
                                {"type": "SPIKE", "x": 49, "y": 0},
                                {"type": "SPIKE", "x": 50, "y": 0},
                                {"type": "BLOCK", "x": 51, "y": 0},
                                {"type": "SPIKE", "x": 52, "y": 0},
                                {"type": "SPIKE", "x": 53, "y": 0},
                                {"type": "SPIKE", "x": 54, "y": 0},
                                {"type": "SPIKE", "x": 55, "y": 0},
                                {"type": "SPIKE", "x": 55, "y": 1},
                                {"type": "SPIKE", "x": 56, "y": 0},
                                {"type": "SPIKE", "x": 57, "y": 0},
                                {"type": "SPIKE", "x": 58, "y": 0},
                                {"type": "BLOCK", "x": 59, "y": 0},
                                {"type": "BLOCK", "x": 59, "y": 1},
                                {"type": "BLOCK", "x": 59, "y": 2}
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