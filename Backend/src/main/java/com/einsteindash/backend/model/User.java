package com.einsteindash.backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data // Lombok untuk otomatis Getter/Setter
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    private String password; // Disimpan plain-text sesuai request

    private int totalStars = 0;
    private int totalCoins = 0;
}