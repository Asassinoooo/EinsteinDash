package com.EinsteinDash.frontend.strategies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.EinsteinDash.frontend.utils.Player;

public class WaveStrategy implements MovementStrategy {

    // Kecepatan Vertikal Wave
    // Biasanya diset sama atau sedikit lebih tinggi dari movement speed horizontal
    // agar sudutnya tajam (sekitar 45 derajat).
    private static final float WAVE_SPEED = 3f;

    @Override
    public void update(Player player, float dt) {
        // 1. Matikan Gravitasi (Wave bergerak murni berdasarkan input velocity)
        player.b2body.setGravityScale(0f);

        // 2. Tentukan Arah Berdasarkan Input (Tahan = Naik, Lepas = Turun)
        boolean isHolding = Gdx.input.isKeyPressed(Input.Keys.SPACE) ||
            Gdx.input.isKeyPressed(Input.Keys.UP) ||
            Gdx.input.isTouched();

        float targetVelocityY;

        if (isHolding) {
            targetVelocityY = WAVE_SPEED;  // Naik
        } else {
            targetVelocityY = -WAVE_SPEED; // Turun
        }

        // 3. Set Linear Velocity Secara Langsung
        // Pertahankan kecepatan X (auto-run), ubah kecepatan Y instan
        player.b2body.setLinearVelocity(player.getMovementSpeed(), targetVelocityY);
    }

    @Override
    public void handleInput(Player player) {
        // Input Continuous ditangani di method update() agar responsif (instant turn).
        // Tidak perlu logic 'Just Pressed' di sini.
    }
}
