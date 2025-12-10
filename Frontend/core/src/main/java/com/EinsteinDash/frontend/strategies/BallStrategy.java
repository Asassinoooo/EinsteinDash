package com.EinsteinDash.frontend.strategies;

import com.EinsteinDash.frontend.utils.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;

/**
 * BallStrategy - Mode bola yang dapat flip gravitasi.
 * Tap saat di permukaan untuk membalik gravitasi (lantai <-> langit-langit).
 */
public class BallStrategy implements MovementStrategy {

    // Cooldown untuk mencegah double-flip instan
    private float cooldownTimer = 0;

    // Buffer input (agar responsif saat mendarat)
    private float inputBufferTimer = 0;

    // Tuning
    private static final float COOLDOWN_TIME = 0.2f; // Naikkan sedikit agar aman
    private static final float BUFFER_TIME = 0.1f; // Jendela buffer

    @Override
    public void update(Player player, float dt) {
        // 1. Gerak Horizontal
        Vector2 vel = player.b2body.getLinearVelocity();
        if (vel.x < player.getCurrentSpeed()) {
            player.b2body.setLinearVelocity(player.getCurrentSpeed(), vel.y);
        }

        // 2. Kurangi Timer
        if (cooldownTimer > 0)
            cooldownTimer -= dt;
        if (inputBufferTimer > 0)
            inputBufferTimer -= dt;
    }

    @Override
    public void handleInput(Player player) {
        boolean isPressed = Gdx.input.isKeyJustPressed(Input.Keys.SPACE) ||
                Gdx.input.isKeyJustPressed(Input.Keys.UP) ||
                Gdx.input.justTouched();

        if (isPressed) {
            inputBufferTimer = BUFFER_TIME;
        }

        // SYARAT GANTI GRAVITASI:
        // 1. Buffer Input Aktif
        // 2. Cooldown Habis
        // 3. Sensor Kaki Menyentuh Tanah (isOnGround)
        // 4. [FIX UTAMA] Kecepatan Y mendekati 0.
        // Jika bola sedang terbang naik/turun kencang (Vel Y > 0.5),
        // berarti dia di udara (apapun kata sensor kontak).
        boolean isPhysicallyOnGround = Math.abs(player.b2body.getLinearVelocity().y) < 1.0f;

        if (inputBufferTimer > 0 && cooldownTimer <= 0 && player.isOnGround() && isPhysicallyOnGround) {

            // Flip Gravitasi
            float currentGravity = player.b2body.getGravityScale();
            player.b2body.setGravityScale(currentGravity * -1);
            player.b2body.setAwake(true);

            // Set Cooldown
            cooldownTimer = COOLDOWN_TIME;
            inputBufferTimer = 0;
        }
    }
}
