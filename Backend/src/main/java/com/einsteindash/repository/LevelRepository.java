package com.einsteindash.repository;

import com.einsteindash.entity.Level;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LevelRepository extends JpaRepository<Level, Long> {

    /**
     * Find levels by creator name
     */
    List<Level> findByCreator(String creator);

    /**
     * Find levels by name containing keyword
     */
    List<Level> findByNameContainingIgnoreCase(String keyword);
}
