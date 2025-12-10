package com.EinsteinDash.frontend.strategies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.EinsteinDash.frontend.utils.Player;

public class RobotStrategy implements MovementStrategy {

    // --- TUNING PENTING ---
    private static final float BASE_THRUST = 24f;
    private static final float TIME_MULTIPLIER = 50f;
    private static final float MAX_JUMP_TIME = 0.25f;
    private static final float INITIAL_VELOCITY = 3.0f;

    // Logic Variables
    private boolean isJumping = false; // Sedang dalam fase thruster aktif
    private float jumpTimer = 0;

    // FIX SPAM: Variable baru untuk memastikan tombol harus dilepas dulu
    private boolean hasReleasedJump = true;

    // INPUT BUFFER
    private float inputBufferTimer = 0;
    private static final float BUFFER_TIME = 0.1f;

    // COOLDOWN
    private float jumpCooldown = 0;
    private static final float COOLDOWN_TIME = 0.2f;

    @Override
    public void update(Player player, float dt) {
        // 1. Gerak Horizontal
        Vector2 vel = player.b2body.getLinearVelocity();
        if (vel.x < player.getMovementSpeed()) {
            player.b2body.setLinearVelocity(player.getMovementSpeed(), vel.y);
        }

        // 2. Gravitasi Normal
        player.b2body.setGravityScale(1f);

        // 3. Update Timers
        if (inputBufferTimer > 0) inputBufferTimer -= dt;
        if (jumpCooldown > 0) jumpCooldown -= dt;

        // Cek Input Hold
        boolean isHolding = Gdx.input.isKeyPressed(Input.Keys.SPACE) ||
            Gdx.input.isKeyPressed(Input.Keys.UP) ||
            Gdx.input.isTouched();

        // Update status pelepasan tombol
        if (!isHolding) {
            hasReleasedJump = true;
        }

        // 4. LOGIKA THRUSTER
        if (isJumping) {
            // Lanjut dorong HANYA jika tombol ditahan & waktu belum habis
            if (isHolding && jumpTimer < MAX_JUMP_TIME) {
                float currentForce = BASE_THRUST + (jumpTimer * TIME_MULTIPLIER);
                player.b2body.applyForceToCenter(0, currentForce, true);
                jumpTimer += dt;
            } else {
                // Stop dorong jika tombol dilepas atau bensin habis
                isJumping = false;
            }
        }
    }

    @Override
    public void handleInput(Player player) {
        boolean jumpStart = Gdx.input.isKeyJustPressed(Input.Keys.SPACE) ||
            Gdx.input.isKeyJustPressed(Input.Keys.UP) ||
            Gdx.input.justTouched();

        if (jumpStart) {
            inputBufferTimer = BUFFER_TIME;
            hasReleasedJump = false; // Tombol sedang ditekan
        }

        // SAFETY CHECK FISIK:
        // Robot dianggap di tanah HANYA jika kecepatan vertikalnya sangat rendah.
        // Toleransi diperkecil jadi 0.1f (sangat ketat).
        // Jika robot sedang naik (vel > 0.1) atau jatuh (vel < -0.1), dia DI UDARA.
        boolean isPhysicallyGrounded = Math.abs(player.b2body.getLinearVelocity().y) < 0.1f;

        // EKSEKUSI JIKA:
        // 1. Ada Buffer Input
        // 2. Sensor kaki bilang di tanah
        // 3. Cooldown habis
        // 4. Tidak sedang jumping
        // 5. Secara fisik DIAM vertikal (PENTING!)
        if (inputBufferTimer > 0 && player.isOnGround() && jumpCooldown <= 0 && !isJumping && isPhysicallyGrounded) {

            isJumping = true;
            jumpTimer = 0;
            inputBufferTimer = 0;
            jumpCooldown = COOLDOWN_TIME;

            // Hentakan Awal
            player.b2body.setLinearVelocity(player.b2body.getLinearVelocity().x, INITIAL_VELOCITY);
        }
    }

    public void setJumpTimer(float value) {
        jumpTimer = value;
    }
}
