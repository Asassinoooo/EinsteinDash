package com.EinsteinDash.frontend.strategies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.EinsteinDash.frontend.utils.Player;

public class SpiderStrategy implements MovementStrategy {

    // Kecepatan sangat tinggi untuk simulasi "Teleport"
    // Jangan terlalu tinggi (misal 1000) karena bisa tembus objek (tunneling)
    private static final float SPIDER_SPEED = 100.0f;

    // Timer buffer agar tidak double-click tak sengaja
    private float bufferTimer = 0;

    @Override
    public void update(Player player, float dt) {
        // 1. Gerak Horizontal
        Vector2 vel = player.b2body.getLinearVelocity();
        if (vel.x < player.getMovementSpeed()) {
            player.b2body.setLinearVelocity(player.getMovementSpeed(), vel.y);
        }

        // 2. Buffer Timer
        if (bufferTimer > 0) bufferTimer -= dt;
    }

    @Override
    public void handleInput(Player player) {
        boolean isPressed = Gdx.input.isKeyJustPressed(Input.Keys.SPACE) ||
            Gdx.input.isKeyJustPressed(Input.Keys.UP) ||
            Gdx.input.justTouched();

        if (isPressed) {
            // SYARAT: Harus di permukaan (Lantai/Atap) DAN cooldown habis
            if (player.isOnGround() && bufferTimer <= 0) {

                // Cek gravitasi saat ini
                float currentGravity = player.b2body.getGravityScale();

                // Tentukan arah tembakan
                float targetVelY;

                if (currentGravity > 0) {
                    // Sedang di lantai (gravitasi +), mau ke atap -> Tembak ke ATAS
                    targetVelY = SPIDER_SPEED;
                } else {
                    // Sedang di atap (gravitasi -), mau ke lantai -> Tembak ke BAWAH
                    targetVelY = -SPIDER_SPEED;
                }

                // 1. Balik Gravitasi (agar menempel saat sampai tujuan)
                player.b2body.setGravityScale(currentGravity * -1);

                // 2. Tembakkan Spider (Ubah kecepatan Y instan)
                player.b2body.setLinearVelocity(player.b2body.getLinearVelocity().x, targetVelY);

                // Bangunkan body
                player.b2body.setAwake(true);

                // Set cooldown kecil
                bufferTimer = 0.1f;
            }
        }
    }
}
