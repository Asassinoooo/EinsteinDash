package com.einsteindash.backend.repository;

import com.einsteindash.backend.model.Level;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LevelRepository extends JpaRepository<Level, Long> {
}
