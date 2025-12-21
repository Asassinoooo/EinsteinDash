package com.einsteindash.backend.controller;

import com.einsteindash.backend.dto.*;
import com.einsteindash.backend.model.*;
import com.einsteindash.backend.repository.UserRepository;
import com.einsteindash.backend.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // Agar bisa diakses dari frontend/game engine manapun
public class ApiController {

    @Autowired
    private GameService gameService;

    @Autowired
    private UserRepository userRepository;

    // 1. Register
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthRequest request) {
        try {
            return ResponseEntity.ok(gameService.register(request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 2. Login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        try {
            return ResponseEntity.ok(gameService.login(request));
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    // 2.5 Get User Data
    @GetMapping("/user/{id}")
    public ResponseEntity<?> getUser(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(gameService.getUser(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 3. Get All Levels (List Level)
    @GetMapping("/levels")
    public List<Level> getLevels() {
        return gameService.getAllLevels();
    }

    // 4. Get User Progress (Untuk ditampilkan di menu select level)
    @GetMapping("/progress/{userId}")
    public List<LevelProgress> getProgress(@PathVariable Long userId) {
        return gameService.getUserProgress(userId);
    }

    // 5. Sync Progress (Dipanggil saat mati atau menang)
    @PostMapping("/sync")
    public ResponseEntity<LevelProgress> syncProgress(@RequestBody ProgressRequest request) {
        return ResponseEntity.ok(gameService.syncProgress(request));
    }

    // 6. Get Leaderboard (Top 10 Players)
    @GetMapping("/leaderboard")
    public List<User> getLeaderboard() {
        return userRepository.findTop10ByOrderByTotalStarsDesc();
    }
}