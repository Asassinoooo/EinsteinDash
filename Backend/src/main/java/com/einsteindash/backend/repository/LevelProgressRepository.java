package com.einsteindash.backend.repository;

import com.einsteindash.backend.model.LevelProgress;
import com.einsteindash.backend.model.User;
import com.einsteindash.backend.model.Level;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface LevelProgressRepository extends JpaRepository<LevelProgress, Long> {
    Optional<LevelProgress> findByUserAndLevel(User user, Level level);
    List<LevelProgress> findByUser(User user);
}