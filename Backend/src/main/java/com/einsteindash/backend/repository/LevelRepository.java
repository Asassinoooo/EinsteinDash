package com.einsteindash.backend.repository;

import com.einsteindash.backend.model.Level;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LevelRepository extends JpaRepository<Level, Long> {
    Optional<Level> findByLevelName(String levelName);
}
