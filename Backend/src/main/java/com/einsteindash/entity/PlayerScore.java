package com.einsteindash.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing a player's score on a specific level.
 */
@Entity
@Table(name = "player_scores")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Player identifier
     */
    @Column(nullable = false)
    private String playerId;

    /**
     * Reference to the level this score belongs to
     */
    @Column(nullable = false)
    private Long levelId;

    /**
     * Completion percentage (0-100)
     */
    @Column(nullable = false)
    private int percentage;
}
