package com.EinsteinDash.frontend.utils;

/**
 * GameObserver - Interface untuk event game (Observer Pattern).
 * Digunakan oleh PlayScreen untuk merespons collision events.
 */
public interface GameObserver {
    /** Dipanggil saat player menyentuh obstacle (spike/tembok) */
    void onPlayerDied();

    /** Dipanggil saat player mencapai finish line (Goal) */
    void onLevelCompleted();

    /** Dipanggil saat player mengambil koin */
    void onCoinCollected();
}
