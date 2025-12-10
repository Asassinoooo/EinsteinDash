package com.EinsteinDash.frontend.strategies;

import com.EinsteinDash.frontend.utils.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;

/**
 * RobotStrategy - Mode robot dengan lompatan variabel.
 * Semakin lama tombol ditahan, semakin tinggi lompatan (seperti Mario).
 */
public class RobotStrategy implements MovementStrategy {

    // === JUMP CONFIG ===
    private static final float THRUST_FORCE = 10f;     // Gaya dorong dasar
    private static final float MAX_JUMP_TIME = 0.35f;  // Durasi maksimal menahan

    // Threshold untuk gradual thrust
    private static final float THRUST_T1 = 0.05f;
    private static final float THRUST_T2 = 0.1f;
    private static final float THRUST_T3 = 0.15f;
    private static final float THRUST_T4 = 0.2f;

    // === STATE ===
    private boolean isJumping = false;
    private float jumpTimer = 0;

    @Override
    public void update(Player player, float dt) {
        // Auto-run ke kanan
        Vector2 vel = player.b2body.getLinearVelocity();
        if (vel.x < player.getMovementSpeed()) {
            player.b2body.setLinearVelocity(player.getMovementSpeed(), vel.y);
        }

        player.b2body.setGravityScale(1f);

        // Thruster logic (semakin lama ditahan = semakin kuat)
        if (isJumping) {
            boolean isHolding = Gdx.input.isKeyPressed(Input.Keys.SPACE) ||
                Gdx.input.isKeyPressed(Input.Keys.UP) ||
                Gdx.input.isTouched();

            if (isHolding && jumpTimer < MAX_JUMP_TIME) {
                // Gradual thrust (makin lama makin kuat)
                float multiplier = 1.0f;
                if (jumpTimer > THRUST_T4) multiplier = 1.8f;
                else if (jumpTimer > THRUST_T3) multiplier = 1.7f;
                else if (jumpTimer > THRUST_T2) multiplier = 1.5f;
                else if (jumpTimer > THRUST_T1) multiplier = 1.0f;

                player.b2body.applyForceToCenter(0, THRUST_FORCE * multiplier, true);
                jumpTimer += dt;
            } else {
                // Tombol dilepas atau waktu habis
                isJumping = false;
                jumpTimer = Math.max(0, jumpTimer - dt);
            }
        }
    }

    @Override
    public void handleInput(Player player) {
        boolean jumpStart = Gdx.input.isKeyJustPressed(Input.Keys.SPACE) ||
            Gdx.input.isKeyJustPressed(Input.Keys.UP) ||
            Gdx.input.justTouched();

        // Mulai lompat hanya jika di tanah
        if (jumpStart && player.isOnGround() && jumpTimer == 0) {
            isJumping = true;
            jumpTimer = 0;
            // Hentakan awal agar lepas dari tanah
            player.b2body.setLinearVelocity(player.b2body.getLinearVelocity().x, 5f);
        }
    }

    /** Reset jump timer (dipanggil saat mendarat) */
    public void setJumpTimer(float value) {
        jumpTimer = value;
    }
}
