package com.EinsteinDash.frontend.strategies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.EinsteinDash.frontend.utils.Player;

public class BallStrategy implements MovementStrategy {

    // Timer buffer kecil agar tidak spam tombol
    private float bufferTimer = 0;

    @Override
    public void update(Player player, float dt) {
        // 1. Gerak Horizontal Konstan
        Vector2 vel = player.b2body.getLinearVelocity();
        if (vel.x < player.getMovementSpeed()) {
            player.b2body.setLinearVelocity(player.getMovementSpeed(), vel.y);
        }

        // 2. Buffer Timer (Cooldown sangat kecil)
        if (bufferTimer > 0) bufferTimer -= dt;
    }

    @Override
    public void handleInput(Player player) {
        // Input: Sekali Tekan (Just Pressed)
        boolean isPressed = Gdx.input.isKeyJustPressed(Input.Keys.SPACE) ||
            Gdx.input.isKeyJustPressed(Input.Keys.UP) ||
            Gdx.input.justTouched();

        if (isPressed) {
            // SYARAT: Harus menyentuh permukaan (isOnGround) DAN cooldown habis
            // Catatan: isOnGround() akan return true baik di lantai maupun di atap,
            // selama atap tersebut adalah object "BLOCK" atau "FLOOR".
            if (player.isOnGround() && bufferTimer <= 0) {

                // --- LOGIKA FLIP GRAVITASI ---

                // Ambil gravitasi saat ini (bisa 1 atau -1)
                float currentGravity = player.b2body.getGravityScale();

                // Balik nilainya (1 jadi -1, -1 jadi 1)
                player.b2body.setGravityScale(currentGravity * -1);

                // PENTING: Bangunkan body Box2D agar perubahan fisika langsung terasa
                player.b2body.setAwake(true);

                // Set cooldown kecil (0.1 detik)
                bufferTimer = 0f;
            }
        }
    }
}
