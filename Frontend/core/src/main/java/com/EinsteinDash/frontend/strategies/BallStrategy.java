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

    private float bufferTimer = 0;  // Cooldown untuk mencegah spam

    @Override
    public void update(Player player, float dt) {
        // Auto-run ke kanan
        Vector2 vel = player.b2body.getLinearVelocity();
        if (vel.x < player.getMovementSpeed()) {
            player.b2body.setLinearVelocity(player.getMovementSpeed(), vel.y);
        }

        if (bufferTimer > 0) bufferTimer -= dt;
    }

    @Override
    public void handleInput(Player player) {
        boolean isJustPressed = Gdx.input.isKeyJustPressed(Input.Keys.SPACE) ||
            Gdx.input.isKeyJustPressed(Input.Keys.UP) ||
            Gdx.input.justTouched();

        // Flip gravitasi saat di permukaan
        if (isJustPressed && player.isOnGround() && bufferTimer <= 0) {
            float currentGravity = player.b2body.getGravityScale();
            player.b2body.setGravityScale(currentGravity * -1);  // Balik gravitasi
            player.b2body.setAwake(true);  // Aktifkan physics
            bufferTimer = 0f;
        }
    }
}
