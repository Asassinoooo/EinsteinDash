package com.EinsteinDash.frontend.utils;

import com.EinsteinDash.frontend.strategies.BallStrategy;
import com.EinsteinDash.frontend.strategies.CubeStrategy;
import com.EinsteinDash.frontend.strategies.MovementStrategy;
import com.EinsteinDash.frontend.strategies.RobotStrategy;
import com.EinsteinDash.frontend.strategies.ShipStrategy;
import com.EinsteinDash.frontend.strategies.SpiderStrategy;
import com.EinsteinDash.frontend.strategies.UfoStrategy;
import com.EinsteinDash.frontend.strategies.WaveStrategy;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class Player extends Sprite {

    // === PHYSICS ===
    public World world;
    public Body b2body;

    // === MOVEMENT CONFIG ===
    private static final float MOVEMENT_SPEED = 3f; // Kecepatan horizontal (auto-run)
    private static final float JUMP_FORCE = 6.5f; // Kekuatan lompat

    // === STRATEGY PATTERN ===
    private MovementStrategy movementStrategy;

    // === INTERPOLATION (untuk smooth rendering) ===
    private Vector2 previousPosition = new Vector2();
    private Vector2 interpolatedPosition = new Vector2();

    // === TEXTURES (untuk setiap mode) ===
    private Texture cubeTexture;
    private Texture shipTexture;
    private Texture ballTexture;
    private Texture ufoTexture;
    private Texture waveTexture;
    private Texture robotTexture;
    private Texture spiderTexture;

    // === GROUND DETECTION ===
    private int footContacts = 0; // Jumlah objek yang sedang diinjak

    // === GRAVITY & SPEED STATE ===
    private boolean isGravityReversed = false;
    private float currentSpeedMultiplier = 1.0f;

    // ==================== CONSTRUCTOR ====================

    public Player(World world) {
        this.world = world;

        // Load semua texture
        cubeTexture = new Texture("player_cube.png");
        shipTexture = new Texture("player_ship.png");
        ballTexture = new Texture("player_ball.png");
        ufoTexture = new Texture("player_ufo.png");
        waveTexture = new Texture("player_wave.png");
        robotTexture = new Texture("player_robot.png");
        spiderTexture = new Texture("player_spider.png");

        // Default: mode Cube
        setRegion(cubeTexture);
        setBounds(0, 0, 30 / Constants.PPM, 30 / Constants.PPM);
        setOrigin(getWidth() / 2, getHeight() / 2);

        definePlayer();
        resetInterpolation();

        // Strategy Awal
        this.movementStrategy = new CubeStrategy();
    }

    // ==================== GROUND DETECTION ====================

    /** Dipanggil saat player mulai menyentuh platform */
    // --- LOGIC GANTI STRATEGY ---
    public void setStrategy(MovementStrategy strategy) {
        this.movementStrategy = strategy;

        // Reset Fisika & Visual Dasar
        setRotation(0);

        // FIX: Reset Flip (Mirroring) agar mode lain tidak ikut terbalik
        setFlip(false, false);

        // REMOVED manual b2body.setGravityScale(1f);

        // Ganti Texture & Setting Khusus
        if (strategy instanceof CubeStrategy) {
            setRegion(cubeTexture);
        } else if (strategy instanceof ShipStrategy) {
            setRegion(shipTexture);
            // REMOVED hardcoded gravity
        } else if (strategy instanceof BallStrategy) {
            setRegion(ballTexture);
        } else if (strategy instanceof UfoStrategy) {
            setRegion(ufoTexture);
        } else if (strategy instanceof WaveStrategy) {
            setRegion(waveTexture);
            // REMOVED hardcoded gravity
        } else if (strategy instanceof RobotStrategy) {
            setRegion(robotTexture);
        } else if (strategy instanceof SpiderStrategy) {
            setRegion(spiderTexture);
        }

        // Apply correct gravity based on new strategy and current reverse state
        updateGravityScale();
    }

    // --- SENSOR TANAH & INTERPOLASI ---
    public void addFootContact() {
        footContacts++;
    }

    /** Dipanggil saat player meninggalkan platform */
    public void removeFootContact() {
        footContacts--;
    }

    /** Cek apakah player sedang menyentuh tanah/platform */
    public boolean isOnGround() {
        return footContacts > 0;
    }

    // ==================== INTERPOLATION ====================

    public void resetInterpolation() {
        previousPosition.set(b2body.getPosition());
        interpolatedPosition.set(b2body.getPosition());
        setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
    }

    /** Simpan posisi sebelum physics step */
    public void capturePreviousPosition() {
        previousPosition.set(b2body.getPosition());
    }

    /** Update posisi visual dengan interpolasi (smooth movement) */
    public void updateVisual(float alpha) {
        Vector2 currentPosition = b2body.getPosition();
        float x = previousPosition.x * (1 - alpha) + currentPosition.x * alpha;
        float y = previousPosition.y * (1 - alpha) + currentPosition.y * alpha;
        interpolatedPosition.set(x, y);
        setPosition(x - getWidth() / 2, y - getHeight() / 2);
    }

    public Vector2 getInterpolatedPosition() {
        return interpolatedPosition;
    }

    // ==================== UPDATE ====================

    /** Update logic player setiap frame */
    public void update(float dt) {
        movementStrategy.update(this, dt);
        updateVisualRotation(dt);
    }

    // --- VISUAL ROTATION & FLIP ---
    private void updateVisualRotation(float dt) {
        float velocityY = b2body.getLinearVelocity().y;

        // Reset flip untuk semua mode KECUALI Spider (karena Spider punya logic
        // sendiri)
        // Update: Sekarang support Global Gravity Flip.
        boolean flipY = isGravityReversed; // Default flip if gravity reversed
        boolean flipX = false;

        if (movementStrategy instanceof SpiderStrategy) {
            // Logic khusus Spider digabung dengan Global Gravity
            boolean isCeiling = b2body.getGravityScale() < 0; // Ini akan konsisten dengan isGravityReversed
            flipY = isCeiling;
        }

        setFlip(flipX, flipY);

        if (movementStrategy instanceof CubeStrategy) {
            if (!isOnGround()) {
                rotate(-450f * dt);
            } else {
                float rotation = getRotation() % 360;
                float targetRotation = Math.round(rotation / 90f) * 90f;
                setRotation(MathUtils.lerp(rotation, targetRotation, 0.2f));
            }
        } else if (movementStrategy instanceof ShipStrategy) {
            // Ship: Miring sesuai arah vertikal
            float targetRotation = MathUtils.clamp(velocityY * 3.0f, -45, 45);
            setRotation(MathUtils.lerp(getRotation(), targetRotation, 0.1f));
        } else if (movementStrategy instanceof BallStrategy) {
            float gravity = b2body.getGravityScale();
            float rotationSpeed = 600f;
            if (gravity > 0)
                rotate(-rotationSpeed * dt);
            else
                rotate(rotationSpeed * dt);
        } else if (movementStrategy instanceof UfoStrategy) {
            float targetRotation = velocityY * 2.0f;
            targetRotation = MathUtils.clamp(targetRotation, -20, 20);
            setRotation(MathUtils.lerp(getRotation(), targetRotation, 0.1f));
        } else if (movementStrategy instanceof WaveStrategy) {
            float targetRotation = 0;
            if (velocityY > 0.1f)
                targetRotation = 45f;
            else if (velocityY < -0.1f)
                targetRotation = -45f;
            setRotation(MathUtils.lerp(getRotation(), targetRotation, 0.3f));
        } else if (movementStrategy instanceof RobotStrategy) {
            // Robot: Selalu tegak
            setRotation(0);
        }
        // --- LOGIKA FIX SPIDER ---
        else if (movementStrategy instanceof SpiderStrategy) {
            // Cek apakah gravitasi negatif (sedang di atap/ceiling)
            boolean isCeiling = b2body.getGravityScale() < 0;

            // Lakukan FLIP Y (Vertikal) jika di atap.
            // X tetap false agar tidak terbalik kiri-kanan.
            setFlip(false, isCeiling);

            // Pastikan rotasi 0 agar berdiri tegak
            setRotation(0);
        } else {
            setRotation(0);
        }
    }

    // ==================== INPUT ====================

    /** Handle input lompat/aksi (delegasi ke strategy) */
    public void jump() {
        movementStrategy.handleInput(this);
    }

    // ==================== PHYSICS SETUP ====================

    /** Buat Box2D body untuk player */
    private void definePlayer() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(32 / Constants.PPM, 64 / Constants.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        bdef.fixedRotation = true; // Rotasi dihandle manual (visual only)

        b2body = world.createBody(bdef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(14 / Constants.PPM, 14 / Constants.PPM);

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.friction = 0;
        fdef.restitution = 0;

        b2body.setUserData(this);
        b2body.createFixture(fdef).setUserData("PLAYER");
        shape.dispose();
    }

    @Override
    public void draw(Batch batch) {
        super.draw(batch);
    }

    public void dispose() {
        if (cubeTexture != null)
            cubeTexture.dispose();
        if (shipTexture != null)
            shipTexture.dispose();
        if (ballTexture != null)
            ballTexture.dispose();
        if (ufoTexture != null)
            ufoTexture.dispose();
        if (waveTexture != null)
            waveTexture.dispose();
        if (robotTexture != null)
            robotTexture.dispose();
        if (spiderTexture != null)
            spiderTexture.dispose();
    }

    public MovementStrategy getStrategy() {
        return movementStrategy;
    }

    public static float getMovementSpeed() {
        return MOVEMENT_SPEED;
    }

    public float getCurrentSpeed() {
        return MOVEMENT_SPEED * currentSpeedMultiplier;
    }

    public void setSpeedMultiplier(float multiplier) {
        this.currentSpeedMultiplier = multiplier;
        // Force update velocity instantly to handle slow-down case (since friction is
        // 0)
        if (b2body != null) {
            Vector2 vel = b2body.getLinearVelocity();
            b2body.setLinearVelocity(getCurrentSpeed(), vel.y);
        }
    }

    public boolean isGravityReversed() {
        return isGravityReversed;
    }

    public void setGravityReversed(boolean reversed) {
        this.isGravityReversed = reversed;
        updateGravityScale();
    }

    private void updateGravityScale() {
        float baseScale = Math.abs(b2body.getGravityScale());
        // Jika mode Wave, gravity 0
        if (movementStrategy instanceof WaveStrategy) {
            baseScale = 0;
        } else if (movementStrategy instanceof ShipStrategy) {
            baseScale = 0.5f;
        } else if (movementStrategy instanceof UfoStrategy) {
            baseScale = 0.5f;
        } else {
            baseScale = 1.0f;
        }

        if (isGravityReversed) {
            b2body.setGravityScale(-baseScale);
        } else {
            b2body.setGravityScale(baseScale);
        }
    }

    public static float getJumpForce() {
        return JUMP_FORCE;
    }
}
