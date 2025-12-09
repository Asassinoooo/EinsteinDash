package com.EinsteinDash.frontend.strategies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.EinsteinDash.frontend.utils.Player;

public class CubeStrategy implements MovementStrategy {

    // Timer cooldown agar tidak glitch sesaat setelah lompat
    private float jumpTimer = 0;

    @Override
    public void update(Player player, float dt) {
        Vector2 vel = player.b2body.getLinearVelocity();
        if (vel.x < player.getMovementSpeed()) {
            player.b2body.setLinearVelocity(player.getMovementSpeed(), vel.y);
        }

        player.b2body.setGravityScale(1f);

        // Kurangi timer
        if (jumpTimer > 0) jumpTimer -= dt;
    }

    @Override
    public void handleInput(Player player) {
        // --- RESPONSIVE HOLD JUMP ---
        boolean isJumpPressed = Gdx.input.isKeyPressed(Input.Keys.SPACE) ||
            Gdx.input.isKeyPressed(Input.Keys.UP) ||
            Gdx.input.isTouched();

        if (isJumpPressed) {
            // Syarat: Fisik harus napak tanah DAN cooldown jump sudah habis
            if (player.isOnGround() && jumpTimer <= 0) {

                player.b2body.applyLinearImpulse(
                    new Vector2(0, player.getJumpForce()),
                    player.b2body.getWorldCenter(),
                    true
                );

                // Beri jeda 0.15 detik agar kaki sempat lepas dari tanah
                // sebelum sensor mendeteksi lagi.
                jumpTimer = 0.15f;
            }
        }
    }
}
