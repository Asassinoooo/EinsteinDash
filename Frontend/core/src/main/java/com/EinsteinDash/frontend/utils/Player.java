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

/**
 * Player - Karakter utama yang dikontrol pemain.
 * Menggunakan Strategy Pattern untuk mengganti mode gerakan (Cube, Ship, Ball, dll).
 */
public class Player extends Sprite {

    // === PHYSICS ===
    public World world;
    public Body b2body;

    // === MOVEMENT CONFIG ===
    private static final float MOVEMENT_SPEED = 3f;  // Kecepatan horizontal (auto-run)
    private static final float JUMP_FORCE = 6.5f;    // Kekuatan lompat

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
    private int footContacts = 0;  // Jumlah objek yang sedang diinjak

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

        this.movementStrategy = new CubeStrategy();
    }

    // ==================== GROUND DETECTION ====================

    /** Dipanggil saat player mulai menyentuh platform */
    public void addFootContact() { footContacts++; }

    /** Dipanggil saat player meninggalkan platform */
    public void removeFootContact() { footContacts--; }

    /** Cek apakah player sedang menyentuh tanah/platform */
    public boolean isOnGround() { return footContacts > 0; }

    // ==================== INTERPOLATION ====================

    /** Reset posisi interpolasi ke posisi fisik saat ini */
    public void resetInterpolation() {
        previousPosition.set(b2body.getPosition());
        interpolatedPosition.set(b2body.getPosition());
        setPosition(b2body.getPosition().x - getWidth()/2, b2body.getPosition().y - getHeight()/2);
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

    public Vector2 getInterpolatedPosition() { return interpolatedPosition; }

    // ==================== UPDATE ====================

    /** Update logic player setiap frame */
    public void update(float dt) {
        movementStrategy.update(this, dt);
        updateVisualRotation(dt);
    }

    /** Update rotasi visual berdasarkan mode dan state */
    private void updateVisualRotation(float dt) {
        float velocityY = b2body.getLinearVelocity().y;

        if (movementStrategy instanceof CubeStrategy) {
            // Cube: Putar saat di udara, snap ke 90° saat mendarat
            if (!isOnGround()) {
                rotate(-450f * dt);
            } else {
                float rotation = getRotation() % 360;
                float targetRotation = Math.round(rotation / 90f) * 90f;
                setRotation(MathUtils.lerp(rotation, targetRotation, 0.2f));
            }
        }
        else if (movementStrategy instanceof ShipStrategy) {
            // Ship: Miring sesuai arah vertikal
            float targetRotation = MathUtils.clamp(velocityY * 3.0f, -45, 45);
            setRotation(MathUtils.lerp(getRotation(), targetRotation, 0.1f));
        }
        else if (movementStrategy instanceof BallStrategy) {
            // Ball: Rolling effect berdasarkan gravitasi
            float rotationSpeed = 600f;
            float gravity = b2body.getGravityScale();
            rotate((gravity > 0 ? -1 : 1) * rotationSpeed * dt);
        }
        else if (movementStrategy instanceof UfoStrategy) {
            // UFO: Sedikit tilt saat bergerak vertikal
            float targetRotation = MathUtils.clamp(velocityY * 2.0f, -20, 20);
            setRotation(MathUtils.lerp(getRotation(), targetRotation, 0.1f));
        }
        else if (movementStrategy instanceof WaveStrategy) {
            // Wave: 45° sesuai arah gerakan
            float targetRotation = 0;
            if (velocityY > 0.1f) targetRotation = 45f;
            else if (velocityY < -0.1f) targetRotation = -45f;
            setRotation(MathUtils.lerp(getRotation(), targetRotation, 0.3f));
        }
        else if (movementStrategy instanceof RobotStrategy) {
            // Robot: Selalu tegak
            setRotation(0);
        }
        else if (movementStrategy instanceof SpiderStrategy) {
            // Spider: Terbalik saat di ceiling
            float targetRotation = (b2body.getGravityScale() > 0) ? 0 : 180;
            setRotation(MathUtils.lerp(getRotation(), targetRotation, 0.3f));
        }
        else {
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
        bdef.fixedRotation = true;  // Rotasi dihandle manual (visual only)

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

    // ==================== STRATEGY ====================

    /** Ganti mode gerakan player */
    public void setStrategy(MovementStrategy strategy) {
        this.movementStrategy = strategy;
        setRotation(0);  // Reset rotasi

        // Setup fisika dan texture berdasarkan mode
        if (strategy instanceof CubeStrategy) {
            b2body.setGravityScale(1f);
            setRegion(cubeTexture);
        } else if (strategy instanceof ShipStrategy) {
            b2body.setGravityScale(0.5f);
            setRegion(shipTexture);
        } else if (strategy instanceof BallStrategy) {
            b2body.setGravityScale(1f);
            setRegion(ballTexture);
        } else if (strategy instanceof UfoStrategy) {
            setRegion(ufoTexture);
        } else if (strategy instanceof WaveStrategy) {
            b2body.setGravityScale(0f);
            setRegion(waveTexture);
        } else if (strategy instanceof RobotStrategy) {
            b2body.setGravityScale(1f);
            setRegion(robotTexture);
        } else if (strategy instanceof SpiderStrategy) {
            setRegion(spiderTexture);
        }
    }

    public MovementStrategy getMovementStrategy() { return movementStrategy; }

    // ==================== GETTERS ====================

    public static float getMovementSpeed() { return MOVEMENT_SPEED; }
    public static float getJumpForce() { return JUMP_FORCE; }

    // ==================== RENDER & DISPOSE ====================

    @Override
    public void draw(Batch batch) { super.draw(batch); }

    public void dispose() {
        if (getTexture() != null) getTexture().dispose();
    }
}
