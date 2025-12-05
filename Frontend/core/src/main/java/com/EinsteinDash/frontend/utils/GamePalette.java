package com.EinsteinDash.frontend.utils;

import com.badlogic.gdx.graphics.Color;

public class GamePalette {

    // 1. Singleton Instance
    private static GamePalette instance;

    // Private Constructor
    private GamePalette() {}

    // Method untuk mendapatkan instance (jika butuh inisialisasi berat di masa depan)
    public static GamePalette getInstance() {
        if (instance == null) {
            instance = new GamePalette();
        }
        return instance;
    }

    /* ==================================================================
       KATEGORI 1: NEON
       Warna dengan saturasi tinggi, cocok untuk Spike, Orb, Portal,
       dan rintangan yang harus dihindari.
       ================================================================== */
    public static class Neon {
        public static final Color CYAN      = Color.valueOf("#00FFFF"); // Electric Cyan
        public static final Color MAGENTA   = Color.valueOf("#FF00FF"); // Hot Pink
        public static final Color LIME      = Color.valueOf("#39FF14"); // Radioactive Green
        public static final Color ORANGE    = Color.valueOf("#FF5E00"); // Lava Orange
        public static final Color RED       = Color.valueOf("#FF0044"); // Danger Red (Spike)
        public static final Color YELLOW    = Color.valueOf("#EAFF00"); // High-Vis Yellow
        public static final Color PURPLE    = Color.valueOf("#BC13FE"); // Laser Purple
        public static final Color BLUE      = Color.valueOf("#2979FF"); // Plasma Blue
    }

    /* ==================================================================
       KATEGORI 2: DARK (Gelap)
       Warna dengan brightness rendah, cocok untuk Background, Floor,
       dan elemen dekorasi belakang agar tidak mengganggu mata.
       ================================================================== */
    public static class Dark {
        public static final Color CHARCOAL  = Color.valueOf("#121212"); // Hampir Hitam
        public static final Color INDIGO    = Color.valueOf("#121222"); // Deep Space (Main BG)
        public static final Color SLATE     = Color.valueOf("#1F1F38"); // Floor Color
        public static final Color MAROON    = Color.valueOf("#2D000F"); // Dark Red bg
        public static final Color OBSIDIAN  = Color.valueOf("#0F0F1A"); // Void
        public static final Color MIDNIGHT  = Color.valueOf("#050A30"); // Dark Ocean
        public static final Color FOREST    = Color.valueOf("#001A05"); // Dark Jungle
    }

    /* ==================================================================
       KATEGORI 3: BRIGHT (Cerah/Vivid)
       Warna solid yang tidak menyakitkan mata, cocok untuk Platform,
       Blok pijakan (Safe Zone), atau Ikon Player.
       ================================================================== */
    public static class Bright {
        public static final Color GOLD      = Color.valueOf("#FFD700"); // Player Icon Standard
        public static final Color SKY       = Color.valueOf("#00A8FF"); // Platform Blue
        public static final Color EMERALD   = Color.valueOf("#2ECC71"); // Platform Green
        public static final Color CORAL     = Color.valueOf("#FF7675"); // Platform Red/Pink
        public static final Color WHITE     = Color.valueOf("#FFFFFF"); // Pure White
        public static final Color SILVER    = Color.valueOf("#DFE6E9"); // Metallic decoration
    }

    /* ==================================================================
       KATEGORI 4: PASTEL
       Warna lembut, cocok untuk elemen UI sekunder, background layer 2
       (Parallax), atau dekorasi awan.
       ================================================================== */
    public static class Pastel {
        public static final Color MINT      = Color.valueOf("#55E6C1");
        public static final Color LAVENDER  = Color.valueOf("#D6A2E8");
        public static final Color PEACH     = Color.valueOf("#FAD390");
        public static final Color ICE       = Color.valueOf("#74B9FF");
        public static final Color CREAM     = Color.valueOf("#F8EFBA");
    }

    /* ==================================================================
       KATEGORI 5: UI (User Interface)
       Warna fungsional untuk tombol, teks notifikasi, health bar,
       atau loading bar.
       ================================================================== */
    public static class UI {
        public static final Color SUCCESS   = Color.valueOf("#44BD32"); // Green Button
        public static final Color DANGER    = Color.valueOf("#C23616"); // Red/Exit Button
        public static final Color INFO      = Color.valueOf("#0984E3"); // Blue Info
        public static final Color TEXT_MAIN = Color.valueOf("#F5F6FA"); // White-ish Text
        public static final Color TEXT_DIM  = Color.valueOf("#7F8FA6"); // Grey Text
        public static final Color PRIMARY   = Color.valueOf("#2E86DE"); // Main Theme Blue
    }
}
