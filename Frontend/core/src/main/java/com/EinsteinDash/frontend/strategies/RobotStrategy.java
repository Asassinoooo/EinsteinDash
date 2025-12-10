package com.EinsteinDash.frontend.strategies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.EinsteinDash.frontend.utils.Player;

public class RobotStrategy implements MovementStrategy {

    // Tuning Fisika Robot
    private static final float ROBOT_THRUST = 10f; // Gaya dorong ke atas (harus kuat lawan gravitasi -26)
    private static final float MAX_JUMP_TIME = 0.35f; // Berapa lama bisa menahan tombol (detik)
    private static final float ADD_THRUST_TIME1 = 0.05f; //thrust nya gradual
    private static final float ADD_THRUST_TIME2 = 0.1f;
    private static final float ADD_THRUST_TIME3 = 0.15f;
    private static final float ADD_THRUST_TIME4 = 0.2f;

    private boolean isJumping = false;
    private float jumpTimer = 0;

    @Override
    public void update(Player player, float dt) {
        // 1. Gerak Horizontal
        Vector2 vel = player.b2body.getLinearVelocity();
        if (vel.x < player.getMovementSpeed()) {
            player.b2body.setLinearVelocity(player.getMovementSpeed(), vel.y);
        }

        // 2. Gravitasi Normal
        player.b2body.setGravityScale(1f);

        // 3. LOGIKA THRUSTER
        if (isJumping) {
            // Cek apakah tombol masih ditahan
            boolean isHolding = Gdx.input.isKeyPressed(Input.Keys.SPACE) ||
                Gdx.input.isKeyPressed(Input.Keys.UP) ||
                Gdx.input.isTouched();

            // Lanjut lompat jika tombol ditahan DAN waktu belum habis
            if (isHolding && jumpTimer < MAX_JUMP_TIME) {
                // Apply Force (Dorong terus menerus)
                if (jumpTimer < ADD_THRUST_TIME1) {
                    player.b2body.applyForceToCenter(0, ROBOT_THRUST, true);
                } else if (jumpTimer > ADD_THRUST_TIME4) {
                    player.b2body.applyForceToCenter(0, ROBOT_THRUST * 1.8f, true);
                } else if (jumpTimer > ADD_THRUST_TIME3) {
                    player.b2body.applyForceToCenter(0, ROBOT_THRUST * 1.7f, true);
                } else if (jumpTimer > ADD_THRUST_TIME2) {
                    player.b2body.applyForceToCenter(0, ROBOT_THRUST * 1.5f, true);
                } else if (jumpTimer > ADD_THRUST_TIME1) {
                    player.b2body.applyForceToCenter(0, ROBOT_THRUST * 1f, true);
                }

                jumpTimer += dt;
            } else {
                // Tombol dilepas atau waktu habis -> Stop Thruster
                isJumping = false;
                if (jumpTimer > MAX_JUMP_TIME) {
                    jumpTimer = MAX_JUMP_TIME;
                }
                jumpTimer -= dt;
            }
        }
    }

    @Override
    public void handleInput(Player player) {
        // INPUT AWAL: JUST PRESSED
        boolean jumpStart = Gdx.input.isKeyJustPressed(Input.Keys.SPACE) ||
            Gdx.input.isKeyJustPressed(Input.Keys.UP) ||
            Gdx.input.justTouched();

        // Hanya bisa mulai lompat jika di tanah
        if (jumpStart && player.isOnGround() && jumpTimer == 0) {
            isJumping = true;
            jumpTimer = 0;

            // Berikan hentakan awal kecil agar lepas dari tanah seketika
            // Ini mencegah glitch gesekan di frame pertama
            player.b2body.setLinearVelocity(player.b2body.getLinearVelocity().x, 5f);
        }
    }

    public void setJumpTimer(float value) {
        jumpTimer = value;
    }
}
