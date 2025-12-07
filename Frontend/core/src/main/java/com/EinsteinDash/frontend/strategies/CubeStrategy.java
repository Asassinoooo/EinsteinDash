package com.EinsteinDash.frontend.strategies;

import com.badlogic.gdx.math.Vector2;
import com.EinsteinDash.frontend.utils.Player;

public class CubeStrategy implements MovementStrategy {

    @Override
    public void update(Player player, float dt) {
        // 1. Gerak Horizontal Konstan
        if (player.b2body.getLinearVelocity().x <= player.getMovementSpeed()) {
            player.b2body.setLinearVelocity(new Vector2(player.getMovementSpeed(), player.b2body.getLinearVelocity().y));
        }

        // 2. Set Gravitasi Normal (Agar lompatan terasa pas)
        player.b2body.setGravityScale(0.7f);

        // 3. Logika Rotasi (Berputar saat lompat, Snap saat di tanah)
        if (Math.abs(player.b2body.getLinearVelocity().y) > 0.01f) {
            player.rotate(-5f); // Putar di udara
        } else {
            // Snap ke 90 derajat terdekat saat mendarat
            float angle = player.getRotation() % 360;
            if (angle < 0) angle += 360;
            player.setRotation(Math.round(player.getRotation() / 90f) * 90f);
        }
    }

    @Override
    public void handleInput(Player player) {
        // Logika Lompat (Hanya bisa jika di tanah)
        if (Math.abs(player.b2body.getLinearVelocity().y) < 0.01f) {
            player.b2body.applyLinearImpulse(new Vector2(0, player.getJumpForce()), player.b2body.getWorldCenter(), true);
        }
    }
}
