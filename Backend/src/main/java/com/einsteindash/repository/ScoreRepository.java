package com.einsteindash.repository;

import com.einsteindash.entity.PlayerScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for PlayerScore entity operations.
 */
@Repository
public interface ScoreRepository extends JpaRepository<PlayerScore, Long> {

    /**
     * Find all scores for a specific level
     */
    List<PlayerScore> findByLevelId(Long levelId);

    /**
     * Find all scores for a specific player
     */
    List<PlayerScore> findByPlayerId(String playerId);

    /**
     * Find scores for a specific player on a specific level
     */
    List<PlayerScore> findByPlayerIdAndLevelId(String playerId, Long levelId);

    /**
     * Find top scores for a level, ordered by percentage descending
     */
    List<PlayerScore> findByLevelIdOrderByPercentageDesc(Long levelId);
}
