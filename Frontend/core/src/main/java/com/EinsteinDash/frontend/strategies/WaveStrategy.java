package com.EinsteinDash.frontend.strategies;

import com.EinsteinDash.frontend.utils.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

/**
 * WaveStrategy - Mode gelombang dengan gerakan diagonal.
 * Tahan tombol = naik diagonal, lepas = turun diagonal.
 * Tidak ada gravitasi - gerakan murni berdasarkan input.
 */
public class WaveStrategy implements MovementStrategy {

    // Kecepatan Vertikal Wave
    // Biasanya diset sama atau sedikit lebih tinggi dari movement speed horizontal
    // agar sudutnya tajam (sekitar 45 derajat).
    private static final float WAVE_SPEED = 3f;

    @Override
    public void update(Player player, float dt) {
        // Matikan gravitasi (Wave bergerak murni manual)
        player.b2body.setGravityScale(0f);

        // Tentukan arah berdasarkan input
        boolean isHolding = Gdx.input.isKeyPressed(Input.Keys.SPACE) ||
            Gdx.input.isKeyPressed(Input.Keys.UP) ||
            Gdx.input.isTouched();

        float targetVelocityY = isHolding ? WAVE_SPEED : -WAVE_SPEED;

        // Set velocity langsung (bukan force) untuk kontrol presisi
        player.b2body.setLinearVelocity(player.getMovementSpeed(), targetVelocityY);
    }

    @Override
    public void handleInput(Player player) {
        // Input ditangani di update() untuk responsivitas instan
    }
}
