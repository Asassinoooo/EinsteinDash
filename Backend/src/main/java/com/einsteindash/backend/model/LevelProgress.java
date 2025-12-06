package com.einsteindash.backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "level_progress", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "level_id"})
})
public class LevelProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "level_id")
    private Level level;

    private int percentage; // Best percentage (0-100)
    private int attempts;
    private boolean isCompleted;
    private int coinsCollected = 0;
}