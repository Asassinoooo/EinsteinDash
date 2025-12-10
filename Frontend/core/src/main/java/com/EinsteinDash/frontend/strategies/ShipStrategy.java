package com.EinsteinDash.frontend.strategies;

import com.EinsteinDash.frontend.utils.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;

/**
 * ShipStrategy - Mode terbang dengan kontrol continuous.
 * Tahan tombol untuk naik, lepas untuk turun (gravitasi rendah).
 */
public class ShipStrategy implements MovementStrategy {

    private static final float SHIP_GRAVITY_SCALE = 0.5f;
    private static final float SHIP_UPWARD_FORCE = 28f;
    private static final float MAX_VERTICAL_SPEED = 3.5f;

    @Override
    public void update(Player player, float dt) {
        // Auto-run ke kanan
        Vector2 vel = player.b2body.getLinearVelocity();
        if (vel.x < player.getMovementSpeed()) {
            player.b2body.setLinearVelocity(player.getMovementSpeed(), vel.y);
        }

        player.b2body.setGravityScale(GRAVITY_SCALE);

        // Terbang ke atas saat tombol ditahan
        boolean isHolding = Gdx.input.isTouched() ||
            Gdx.input.isKeyPressed(Input.Keys.SPACE) ||
            Gdx.input.isKeyPressed(Input.Keys.UP);

        if (isHolding) {
            player.b2body.applyForceToCenter(0, UPWARD_FORCE, true);
        }

        // Batasi kecepatan vertikal
        float currentVelY = player.b2body.getLinearVelocity().y;
        if (Math.abs(currentVelY) > MAX_VERTICAL_SPEED) {
            float clampedY = Math.signum(currentVelY) * MAX_VERTICAL_SPEED;
            player.b2body.setLinearVelocity(player.getMovementSpeed(), clampedY);
        }
    }

    @Override
    public void handleInput(Player player) {
        // Input ditangani di update() untuk responsivitas
    }
}
