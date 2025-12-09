package com.EinsteinDash.frontend.strategies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.EinsteinDash.frontend.utils.Player;

public class ShipStrategy implements MovementStrategy {

    private static final float SHIP_GRAVITY_SCALE = 0.4f;
    private static final float SHIP_UPWARD_FORCE = 27f;
    private static final float MAX_VERTICAL_SPEED = 3.5f;

    @Override
    public void update(Player player, float dt) {
        Vector2 vel = player.b2body.getLinearVelocity();
        if (vel.x < player.getMovementSpeed()) {
            player.b2body.setLinearVelocity(player.getMovementSpeed(), vel.y);
        }

        player.b2body.setGravityScale(SHIP_GRAVITY_SCALE);

        if (Gdx.input.isTouched() || Gdx.input.isKeyPressed(Input.Keys.SPACE) || Gdx.input.isKeyPressed(Input.Keys.UP)) {
            player.b2body.applyForceToCenter(0, SHIP_UPWARD_FORCE, true);
        }

        float currentVelY = player.b2body.getLinearVelocity().y;
        if (currentVelY > MAX_VERTICAL_SPEED) {
            player.b2body.setLinearVelocity(player.getMovementSpeed(), MAX_VERTICAL_SPEED);
        } else if (currentVelY < -MAX_VERTICAL_SPEED) {
            player.b2body.setLinearVelocity(player.getMovementSpeed(), -MAX_VERTICAL_SPEED);
        }
    }

    @Override
    public void handleInput(Player player) {}
}
