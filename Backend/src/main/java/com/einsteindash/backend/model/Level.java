package com.einsteindash.backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "levels")
public class Level {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String levelName;

    @Lob // Large Object untuk string layout yang panjang
    private String levelData;

    // Range 1-10 (Difficulty sekaligus reward bintang)
    private int stars;

    private int audioTrackId;
}