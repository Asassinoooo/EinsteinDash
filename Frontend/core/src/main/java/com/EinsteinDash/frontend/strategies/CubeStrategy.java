package com.EinsteinDash.frontend.strategies;

import com.EinsteinDash.frontend.utils.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;

/**
 * CubeStrategy - Mode default seperti Geometry Dash asli.
 * Lompat sekali saat di tanah, tidak bisa double jump.
 */
public class CubeStrategy implements MovementStrategy {

    private static final float JUMP_COOLDOWN = 0.15f; // Delay antar lompatan
    private float jumpTimer = 0;

    @Override
    public void update(Player player, float dt) {
        // Auto-run ke kanan
        Vector2 vel = player.b2body.getLinearVelocity();
        if (vel.x < player.getCurrentSpeed()) {
            player.b2body.setLinearVelocity(player.getCurrentSpeed(), vel.y);
        }

        // REMOVED: player.b2body.setGravityScale(1f); -> Handled by Player state

        // Kurangi cooldown timer
        if (jumpTimer > 0)
            jumpTimer -= dt;
    }

    @Override
    public void handleInput(Player player) {
        boolean isJumpPressed = Gdx.input.isKeyPressed(Input.Keys.SPACE) ||
                Gdx.input.isKeyPressed(Input.Keys.UP) ||
                Gdx.input.isTouched();

        // Lompat jika di tanah dan cooldown habis
        if (isJumpPressed && player.isOnGround() && jumpTimer <= 0) {
            float jumpForce = player.isGravityReversed() ? -Player.getJumpForce() : Player.getJumpForce();

            player.b2body.applyLinearImpulse(
                    new Vector2(0, jumpForce),
                    player.b2body.getWorldCenter(),
                    true);
            jumpTimer = JUMP_COOLDOWN;
        }
    }
}
