package com.einsteindash.config;

import com.einsteindash.entity.Level;
import com.einsteindash.repository.LevelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Data initializer that runs on application startup.
 * Creates a dummy level if the database is empty.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final LevelRepository levelRepository;

    @Override
    public void run(String... args) {
        // Check if levels table is empty
        if (levelRepository.count() == 0) {
            log.info("No levels found in database. Creating dummy level...");
            
            //create dummy level data
            String dummyLevelData = """
                {
                    "version": "1.0",
                    "settings": {
                        "songId": 1,
                        "gameSpeed": 1.0,
                        "backgroundColor": "#1a1a2e"
                    },
                    "objects": [
                        {
                            "type": "block",
                            "x": 500,
                            "y": 0,
                            "width": 50,
                            "height": 50
                        },
                        {
                            "type": "block",
                            "x": 700,
                            "y": 0,
                            "width": 50,
                            "height": 50
                        },
                        {
                            "type": "spike",
                            "x": 900,
                            "y": 0,
                            "width": 40,
                            "height": 40
                        },
                        {
                            "type": "block",
                            "x": 1100,
                            "y": 0,
                            "width": 50,
                            "height": 50
                        },
                        {
                            "type": "block",
                            "x": 1100,
                            "y": 50,
                            "width": 50,
                            "height": 50
                        },
                        {
                            "type": "spike",
                            "x": 1300,
                            "y": 0,
                            "width": 40,
                            "height": 40
                        },
                        {
                            "type": "block",
                            "x": 1500,
                            "y": 0,
                            "width": 50,
                            "height": 50
                        },
                        {
                            "type": "portal",
                            "portalType": "gravity",
                            "x": 1700,
                            "y": 0,
                            "width": 30,
                            "height": 100
                        },
                        {
                            "type": "finish",
                            "x": 2000,
                            "y": 0,
                            "width": 50,
                            "height": 200
                        }
                    ],
                    "totalLength": 2050
                }
                """;

            Level dummyLevel = new Level();
            dummyLevel.setName("Tutorial Level");
            dummyLevel.setCreator("EinsteinDash Team");
            dummyLevel.setLevelData(dummyLevelData);

            levelRepository.save(dummyLevel);
            log.info("Dummy level 'Tutorial Level' created successfully!");
        } else {
            log.info("Levels already exist in database. Skipping initialization.");
        }
    }
}
