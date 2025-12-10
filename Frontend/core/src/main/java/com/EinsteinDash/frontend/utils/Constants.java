package com.EinsteinDash.frontend.utils;

/**
 * Constants - Konfigurasi global untuk game.
 */
public class Constants {

    // === PHYSICS ===
    /** Pixels Per Meter - Konversi antara dunia Box2D dan layar */
    public static final float PPM = 100;

    // === VIEWPORT ===
    /** Lebar layar virtual (dalam pixel) */
    public static final int V_WIDTH = 1280;
    /** Tinggi layar virtual (dalam pixel) */
    public static final int V_HEIGHT = 720;

    // === NETWORK ===
    /** Base URL untuk REST API backend */
    public static final String BASE_URL = "http://localhost:8081/api";

    // === SPEED VALUES (Blocks/s converted to m/s: 1 block = 0.32m) ===
    public static final float SPEED_HALF = 2.688f; // 8.4 blocks/s
    public static final float SPEED_NORMAL = 3.328f; // 10.4 blocks/s
    public static final float SPEED_DOUBLE = 4.128f; // 12.9 blocks/s
    public static final float SPEED_TRIPLE = 4.992f; // 15.6 blocks/s
    public static final float SPEED_QUAD = 6.144f; // 19.2 blocks/s
}
