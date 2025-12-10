package com.EinsteinDash.frontend.strategies;

import com.EinsteinDash.frontend.utils.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;

/**
 * UfoStrategy - Mode UFO dengan lompatan pendek berulang.
 * Bisa lompat di udara (tidak perlu di tanah). Setiap tap = lompat kecil.
 */
public class UfoStrategy implements MovementStrategy {

    private static final float JUMP_FORCE = 5.5f;       // Lompatan lebih kecil dari Cube
    private static final float MAX_VELOCITY = 5.0f;     // Batas kecepatan vertikal

    @Override
    public void update(Player player, float dt) {
        // Auto-run ke kanan
        Vector2 vel = player.b2body.getLinearVelocity();
        if (vel.x < player.getMovementSpeed()) {
            player.b2body.setLinearVelocity(player.getMovementSpeed(), vel.y);
        }

        player.b2body.setGravityScale(1f);

        // Batasi kecepatan agar tidak tembus ceiling
        if (vel.y > MAX_VELOCITY) {
            player.b2body.setLinearVelocity(vel.x, MAX_VELOCITY);
        }
    }

    @Override
    public void handleInput(Player player) {
        // UFO bisa lompat di udara (tidak perlu di tanah)
        boolean isJumpPressed = Gdx.input.isKeyJustPressed(Input.Keys.SPACE) ||
            Gdx.input.isKeyJustPressed(Input.Keys.UP) ||
            Gdx.input.justTouched();

        if (isJumpPressed) {
            // Reset velocity Y untuk lompatan konsisten
            player.b2body.setLinearVelocity(player.b2body.getLinearVelocity().x, 0);
            player.b2body.applyLinearImpulse(
                new Vector2(0, JUMP_FORCE),
                player.b2body.getWorldCenter(),
                true
            );
        }
    }
}
