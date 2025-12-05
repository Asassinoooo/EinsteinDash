package com.einsteindash.backend.dto;
import lombok.Data;

@Data
public class ProgressRequest {
    private Long userId;
    private Long levelId;
    private int percentage;
    private int attemptsToAdd; // Jumlah attempt baru sesi ini
}