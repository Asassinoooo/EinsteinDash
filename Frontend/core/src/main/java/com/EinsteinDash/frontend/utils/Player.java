package com.EinsteinDash.frontend.utils;

import com.EinsteinDash.frontend.strategies.BallStrategy;
import com.EinsteinDash.frontend.strategies.CubeStrategy;
import com.EinsteinDash.frontend.strategies.MovementStrategy;
import com.EinsteinDash.frontend.strategies.RobotStrategy;
import com.EinsteinDash.frontend.strategies.ShipStrategy;
import com.EinsteinDash.frontend.strategies.SpiderStrategy;
import com.EinsteinDash.frontend.strategies.UfoStrategy;
import com.EinsteinDash.frontend.strategies.WaveStrategy;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
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
    private static final float MOVEMENT_SPEED = 1.0f; // Unit scalar, actual speed in multiplier
    private static final float JUMP_FORCE = 6.5f; // Kekuatan lompat

    // === STRATEGY PATTERN ===
    private MovementStrategy movementStrategy;

    // === INTERPOLATION (untuk smooth rendering) ===
    private Vector2 previousPosition = new Vector2();
    private Vector2 interpolatedPosition = new Vector2();

    // === VISUAL EFFECTS ===
    private com.badlogic.gdx.utils.Array<Vector2> waveTrail;
    private static final int MAX_TRAIL_LENGTH = 30;

    // === TEXTURES (untuk setiap mode) ===
    private Texture cubeTexture;
    private Texture shipTexture;
    private Texture ballTexture;
    private Texture ufoTexture;
    private Texture waveTexture;
    private Texture robotTexture;

    // === SPIDER ANIMATION ===
    private Texture spiderTexture1;
    private Texture spiderTexture2;
    private Texture spiderTexture3;
    private Animation<TextureRegion> spiderAnimation;
    private float spiderAnimTimer = 0f;
    private static final float SPIDER_FRAME_DURATION = 0.1f; // 10 FPS animasi

    // === GROUND DETECTION ===
    private int footContacts = 0; // Jumlah objek yang sedang diinjak

    // === GRAVITY & SPEED STATE ===
    private boolean isGravityReversed = false;
    private float currentSpeedMultiplier = Constants.SPEED_NORMAL;

    // ==================== CONSTRUCTOR ====================

    public Player(World world) {
        this.world = world;

        // Init Effects
        waveTrail = new com.badlogic.gdx.utils.Array<>();

        // Load semua texture
        cubeTexture = new Texture("player/player_cube.png");
        shipTexture = new Texture("player/player_ship.png");
        ballTexture = new Texture("player/player_ball.png");
        ufoTexture = new Texture("player/player_ufo.png");
        waveTexture = new Texture("player/player_wave.png");
        robotTexture = new Texture("player/player_robot.png");

        // Load 3 frame animasi spider
        spiderTexture1 = new Texture("player/player_spider1.png");
        spiderTexture2 = new Texture("player/player_spider2.png");
        spiderTexture3 = new Texture("player/player_spider3.png");

        // Buat Animation dari 3 frame
        TextureRegion[] spiderFrames = new TextureRegion[] {
            new TextureRegion(spiderTexture1),
            new TextureRegion(spiderTexture2),
            new TextureRegion(spiderTexture3)
        };
        spiderAnimation = new Animation<>(SPIDER_FRAME_DURATION, spiderFrames);
        spiderAnimation.setPlayMode(Animation.PlayMode.LOOP);

        // Default: mode Cube
        setRegion(cubeTexture);

        float defaultSize = 30 / Constants.PPM;
        setBounds(0, 0, defaultSize, defaultSize);
        setOrigin(getWidth() / 2, getHeight() / 2);

        this.targetWidth = defaultSize;
        this.targetHeight = defaultSize;
        this.needsFixtureUpdate = false; // Initial body defined manually below

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

        // DEFAULT SIZE (SQUARE) for HITBOX
        float defaultSize = 30 / Constants.PPM;

        // Sprite Dimensions (default to square)
        float spriteWidth = defaultSize;
        float spriteHeight = defaultSize;

        // Ganti Texture & Setting Khusus
        if (strategy instanceof CubeStrategy) {
            setRegion(cubeTexture);
        } else if (strategy instanceof ShipStrategy) {
            setRegion(shipTexture);
            // Ship Sprite: Pressed Rectangular (1.6x ratio)
            // Hitbox: Stays Square (defaultSize)
            spriteWidth = spriteHeight * 1.6f;
        } else if (strategy instanceof BallStrategy) {
            setRegion(ballTexture);
        } else if (strategy instanceof UfoStrategy) {
            setRegion(ufoTexture);
            // UFO Sprite: Scaled up slightly (1.3x)
            spriteWidth = defaultSize * 1.3f;
            spriteHeight = defaultSize * 1.3f;
        } else if (strategy instanceof WaveStrategy) {
            setRegion(waveTexture);
        } else if (strategy instanceof RobotStrategy) {
            setRegion(robotTexture);
            // Robot Sprite: Scaled up slightly (1.25x)
            spriteWidth = defaultSize * 1.25f;
            spriteHeight = defaultSize * 1.25f;
        } else if (strategy instanceof SpiderStrategy) {
            // Reset timer animasi saat masuk mode Spider
            spiderAnimTimer = 0f;
            setRegion(spiderAnimation.getKeyFrame(0));
            // Spider Ratio: 625x368 ~ 1.7
            spriteWidth = spriteHeight * 1.7f;
        }

        // Update Sprite Size (Visual Only)
        setSize(spriteWidth, spriteHeight);
        setOrigin(spriteWidth / 2, spriteHeight / 2);

        // Queue Physics Update (Force Square Hitbox)
        // Ensure hitbox is always standard square size
        if (this.targetWidth != defaultSize || this.targetHeight != defaultSize) {
             this.targetWidth = defaultSize;
             this.targetHeight = defaultSize;
             this.needsFixtureUpdate = true;
        } else {
             // If already square, no need to update
             this.needsFixtureUpdate = false;
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
        if (needsFixtureUpdate) {
            redefineBodyShape();
            needsFixtureUpdate = false;
        }

        movementStrategy.update(this, dt);

        // --- TRAIL LOGIC ---
        if (movementStrategy instanceof WaveStrategy) {
            // Record center position
            waveTrail.add(new Vector2(getX() + getWidth() / 2, getY() + getHeight() / 2));
            if (waveTrail.size > MAX_TRAIL_LENGTH) {
                waveTrail.removeIndex(0);
            }
        } else {
            if (waveTrail.size > 0)
                waveTrail.clear();
        }

        // Update animasi spider jika sedang dalam mode Spider
        // NOTE: Must be before updateVisualRotation because setRegion resets flip state!
        if (movementStrategy instanceof SpiderStrategy) {
            spiderAnimTimer += dt;
            setRegion(spiderAnimation.getKeyFrame(spiderAnimTimer, true));
        }

        updateVisualRotation(dt);
    }

    // ==================== RENDERING EFFECTS ====================

    public void drawTrail(com.badlogic.gdx.graphics.glutils.ShapeRenderer sr) {
        if (waveTrail.size < 2) return;

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        // Layer 1: Outer Glow (Cyan/Blue)
        sr.setColor(0f, 1f, 1f, 0.4f); // Cyan transparent
        for (int i = 0; i < waveTrail.size - 1; i++) {
            Vector2 p1 = waveTrail.get(i);
            Vector2 p2 = waveTrail.get(i+1);
            sr.rectLine(p1, p2, 8 / Constants.PPM); // Tebal
        }

        // Layer 2: Inner Core (White)
        sr.setColor(1f, 1f, 1f, 0.8f);
        for (int i = 0; i < waveTrail.size - 1; i++) {
            Vector2 p1 = waveTrail.get(i);
            Vector2 p2 = waveTrail.get(i+1);
            sr.rectLine(p1, p2, 3 / Constants.PPM); // Tipis
        }

        Gdx.gl.glDisable(GL20.GL_BLEND);
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
            // User request: Adjust rotation/flip logic
            boolean isCeiling = b2body.getGravityScale() < 0;
            flipY = isCeiling;
            flipX = isCeiling; // Flip X too when on ceiling
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
            float targetRotation = MathUtils.clamp(velocityY * 3.0f, -30, 30); // Reduced rotation for ship
            setRotation(MathUtils.lerp(getRotation(), targetRotation, 0.1f));
        } else if (movementStrategy instanceof BallStrategy) {
            float gravity = b2body.getGravityScale();
            float rotationSpeed = 600f;
            if (gravity > 0)
                rotate(-rotationSpeed * dt); // Reverted: Ground = Clockwise (Negative)
            else
                rotate(rotationSpeed * dt); // Reverted: Ceiling = CCW (Positive)
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
             // Redundant but keeping consistent
             // Logic already handled by setFlip above, but this block ensures rotation is 0
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

        // Initial shape (Box)
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

    // === DYNAMIC SHAPE HANDLING ===
    private boolean needsFixtureUpdate = false;
    private float targetWidth;
    private float targetHeight;

    /**
     * Recreates the fixture with new dimensions.
     * Must be called during update step, NOT during world step.
     */
    private void redefineBodyShape() {
        // Destroy old fixture
        if (b2body.getFixtureList().size > 0) {
            b2body.destroyFixture(b2body.getFixtureList().first());
        }

        // Create new shape
        PolygonShape shape = new PolygonShape();
        // Slightly smaller than sprite to allow for padding
        float hx = (targetWidth / 2) - (1 / Constants.PPM);
        float hy = (targetHeight / 2) - (1 / Constants.PPM);
        shape.setAsBox(hx, hy);

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.friction = 0;
        fdef.restitution = 0;

        b2body.createFixture(fdef).setUserData("PLAYER");
        shape.dispose();

        // Update Sprite Origin to Center
        setOrigin(targetWidth / 2, targetHeight / 2);
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
        if (spiderTexture1 != null)
            spiderTexture1.dispose();
        if (spiderTexture2 != null)
            spiderTexture2.dispose();
        if (spiderTexture3 != null)
            spiderTexture3.dispose();
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
