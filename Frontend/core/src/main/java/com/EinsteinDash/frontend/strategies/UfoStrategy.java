package com.EinsteinDash.frontend.strategies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.EinsteinDash.frontend.utils.Player;

public class UfoStrategy implements MovementStrategy {

    // Tuning Fisika UFO
    private static final float UFO_JUMP_FORCE = 5.5f; // Hentakan lebih kecil dari Cube (karena bisa spam)
    private static final float MAX_UFO_VELOCITY = 5.0f; // Batas kecepatan agar tidak tembus atap saat spam

    @Override
    public void update(Player player, float dt) {
        // 1. Gerak Horizontal
        Vector2 vel = player.b2body.getLinearVelocity();
        if (vel.x < player.getMovementSpeed()) {
            player.b2body.setLinearVelocity(player.getMovementSpeed(), vel.y);
        }

        // 2. Gravitasi Normal
        player.b2body.setGravityScale(1f);

        // 3. Batasi Kecepatan Jatuh/Naik (Optional, biar lebih terkontrol)
        if (vel.y > MAX_UFO_VELOCITY) {
            player.b2body.setLinearVelocity(vel.x, MAX_UFO_VELOCITY);
        }
    }

    @Override
    public void handleInput(Player player) {
        // INPUT: JUST PRESSED (Sekali tekan = Sekali hentak)
        // Tidak perlu cek isOnGround() karena UFO bisa lompat di udara.

        boolean isJumpPressed = Gdx.input.isKeyJustPressed(Input.Keys.SPACE) ||
            Gdx.input.isKeyJustPressed(Input.Keys.UP) ||
            Gdx.input.justTouched();

        if (isJumpPressed) {
            // Reset velocity Y sedikit agar lompatan selalu konsisten,
            // tidak peduli sedang jatuh kencang atau tidak.
            // Ini membuat kontrol UFO terasa "tajam" dan responsif.
            player.b2body.setLinearVelocity(player.b2body.getLinearVelocity().x, 0);

            // Dorong ke atas
            player.b2body.applyLinearImpulse(
                new Vector2(0, UFO_JUMP_FORCE),
                player.b2body.getWorldCenter(),
                true
            );
        }
    }
}
