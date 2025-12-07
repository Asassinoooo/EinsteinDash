package com.einsteindash.backend.service;

import com.einsteindash.backend.dto.AuthRequest;
import com.einsteindash.backend.dto.ProgressRequest;
import com.einsteindash.backend.model.*;
import com.einsteindash.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class GameService {

    @Autowired private UserRepository userRepository;
    @Autowired private LevelRepository levelRepository;
    @Autowired private LevelProgressRepository progressRepository;

    // --- AUTHENTICATION ---

    public User register(AuthRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setPassword(request.getPassword()); // Plain text
        return userRepository.save(newUser);
    }

    public User login(AuthRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("Wrong password");
        }
        return user;
    }

    // --- LEVEL MANAGEMENT ---

    public List<Level> getAllLevels() {
        return levelRepository.findAll();
    }

    public Level getLevelDetail(Long levelId) {
        return levelRepository.findById(levelId)
                .orElseThrow(() -> new RuntimeException("Level not found"));
    }

    @Transactional(readOnly = true)
    public List<LevelProgress> getUserProgress(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        return progressRepository.findByUser(user);
    }

    // --- SYNC PROGRESS (LOGIKA BINTANG) ---

    public LevelProgress syncProgress(ProgressRequest request) {
        User user = userRepository.findById(request.getUserId()).orElseThrow();
        Level level = levelRepository.findById(request.getLevelId()).orElseThrow();

        // Cari progress lama, atau buat baru jika belum ada
        LevelProgress progress = progressRepository.findByUserAndLevel(user, level)
                .orElse(new LevelProgress());

        if (progress.getId() == null) {
            progress.setUser(user);
            progress.setLevel(level);
            progress.setCoinsCollected(0);
            progress.setPercentage(0);
        }

        // Update total attempts (akumulasi)
        progress.setAttempts(progress.getAttempts() + request.getAttemptsToAdd());

        // Update percentage hanya jika lebih tinggi dari sebelumnya
        if (request.getPercentage() > progress.getPercentage()) {
            progress.setPercentage(request.getPercentage());
        }

        // Logika Reward Bintang
        // Jika user mencapai 100% DAN sebelumnya belum completed
        if (request.getPercentage() >= 100 && !progress.isCompleted()) {
            progress.setCompleted(true);
            progress.setPercentage(100); // Pastikan mentok 100

            // Tambahkan bintang ke user sesuai difficulty level
            user.setTotalStars(user.getTotalStars() + level.getStars());
            userRepository.save(user); // Simpan update user
        }

        // Logika Coin
        int oldCoins = progress.getCoinsCollected();
        int newCoins = request.getCoinsCollected();
        if (newCoins > oldCoins) {
            // Update progress level
            progress.setCoinsCollected(newCoins);
            // Hitung selisih
            int coinsDiff = newCoins - oldCoins;
            int currentTotal = user.getTotalCoins();
            user.setTotalCoins(currentTotal + coinsDiff);
            // Simpan perubahan user ke database
            userRepository.save(user);
        }

        return progressRepository.save(progress);
    }
}