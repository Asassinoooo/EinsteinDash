package com.einsteindash.controller;

import com.einsteindash.entity.Level;
import com.einsteindash.entity.PlayerScore;
import com.einsteindash.repository.LevelRepository;
import com.einsteindash.repository.ScoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * REST Controller for game operations.
 * Handles level retrieval and score submission.
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class GameController {

    private final LevelRepository levelRepository;
    private final ScoreRepository scoreRepository;

    // Level Endpoints

    /*
     * Get a specific level by ID
     * GET /api/levels/{id}
     */
    @GetMapping("/levels/{id}")
    public ResponseEntity<Level> getLevelById(@PathVariable Long id) {
        Optional<Level> level = levelRepository.findById(id);
        return level.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get all available levels
     * GET /api/levels
     */
    @GetMapping("/levels")
    public ResponseEntity<List<Level>> getAllLevels() {
        List<Level> levels = levelRepository.findAll();
        return ResponseEntity.ok(levels);
    }

    /**
     * Create a new level
     * POST /api/levels
     */
    @PostMapping("/levels")
    public ResponseEntity<Level> createLevel(@RequestBody Level level) {
        Level savedLevel = levelRepository.save(level);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedLevel);
    }

    // Score Endpoints

    /**
     * Submit a player's score
     * POST /api/scores
     */
    @PostMapping("/scores")
    public ResponseEntity<PlayerScore> submitScore(@RequestBody PlayerScore score) {
        // Validate percentage is within bounds
        if (score.getPercentage() < 0 || score.getPercentage() > 100) {
            return ResponseEntity.badRequest().build();
        }
        
        PlayerScore savedScore = scoreRepository.save(score);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedScore);
    }

    /**
     * Get all scores for a specific level
     * GET /api/scores/level/{levelId}
     */
    @GetMapping("/scores/level/{levelId}")
    public ResponseEntity<List<PlayerScore>> getScoresByLevel(@PathVariable Long levelId) {
        List<PlayerScore> scores = scoreRepository.findByLevelIdOrderByPercentageDesc(levelId);
        return ResponseEntity.ok(scores);
    }

    /**
     * Get all scores for a specific player
     * GET /api/scores/player/{playerId}
     */
    @GetMapping("/scores/player/{playerId}")
    public ResponseEntity<List<PlayerScore>> getScoresByPlayer(@PathVariable String playerId) {
        List<PlayerScore> scores = scoreRepository.findByPlayerId(playerId);
        return ResponseEntity.ok(scores);
    }

    /**
     * Get leaderboard for a specific level
     * GET /api/leaderboard/{levelId}
     */
    @GetMapping("/leaderboard/{levelId}")
    public ResponseEntity<List<PlayerScore>> getLeaderboard(@PathVariable Long levelId) {
        List<PlayerScore> topScores = scoreRepository.findByLevelIdOrderByPercentageDesc(levelId);
        // Return top 10 scores
        List<PlayerScore> top10 = topScores.stream().limit(10).toList();
        return ResponseEntity.ok(top10);
    }

    /**
     * Health check endpoint
     * GET /api/health
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("EinsteinDash Backend is running!");
    }
}
