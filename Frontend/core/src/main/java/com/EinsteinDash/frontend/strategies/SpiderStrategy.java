package com.EinsteinDash.frontend.strategies;

import com.EinsteinDash.frontend.utils.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;

/**
 * SpiderStrategy - Mode laba-laba dengan teleport antar permukaan.
 * Tap saat di lantai = teleport ke ceiling, dan sebaliknya.
 */
public class SpiderStrategy implements MovementStrategy {

    private static final float TELEPORT_SPEED = 100.0f; // Kecepatan tinggi (simulasi teleport)
    private static final float BUFFER_COOLDOWN = 0.1f; // Cooldown anti-spam

    private float bufferTimer = 0;

    @Override
    public void update(Player player, float dt) {
        // Auto-run ke kanan
        Vector2 vel = player.b2body.getLinearVelocity();
        if (vel.x < player.getCurrentSpeed()) {
            player.b2body.setLinearVelocity(player.getCurrentSpeed(), vel.y);
        }

        if (bufferTimer > 0)
            bufferTimer -= dt;
    }

    @Override
    public void handleInput(Player player) {
        boolean isPressed = Gdx.input.isKeyJustPressed(Input.Keys.SPACE) ||
                Gdx.input.isKeyJustPressed(Input.Keys.UP) ||
                Gdx.input.justTouched();

        if (isPressed && player.isOnGround() && bufferTimer <= 0) {
            float currentGravity = player.b2body.getGravityScale();

            // Tentukan arah teleport (lantai -> atas, ceiling -> bawah)
            float targetVelY = (currentGravity > 0) ? TELEPORT_SPEED : -TELEPORT_SPEED;

            // Balik gravitasi dan tembak
            player.b2body.setGravityScale(currentGravity * -1);
            player.b2body.setLinearVelocity(player.b2body.getLinearVelocity().x, targetVelY);
            player.b2body.setAwake(true);

            bufferTimer = BUFFER_COOLDOWN;
        }
    }
}
