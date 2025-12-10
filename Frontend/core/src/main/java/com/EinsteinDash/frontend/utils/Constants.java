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
}
