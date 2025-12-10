package com.EinsteinDash.frontend.utils;

import com.EinsteinDash.frontend.strategies.*;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.graphics.g2d.Batch;

public class Player extends Sprite {
    public World world;
    public Body b2body;
    private MovementStrategy movementStrategy;

    private static final float MOVEMENT_SPEED = 3f;
    private static final float JUMP_FORCE = 6.5f;

    private Vector2 previousPosition = new Vector2();
    private Vector2 interpolatedPosition = new Vector2();

    private Texture cubeTexture;
    private Texture shipTexture;
    private Texture ballTexture;
    private Texture ufoTexture;
    private Texture waveTexture;
    private Texture robotTexture;
    private Texture spiderTexture;

    // --- LOGIC BARU: SENSOR TANAH ---
    private int footContacts = 0; // Berapa banyak objek yang diinjak

    public Player(World world) {
        this.world = world;

        cubeTexture = new Texture("player_cube.png");
        shipTexture = new Texture("player_ship.png");
        ballTexture = new Texture("player_ball.png");
        ufoTexture = new Texture("player_ufo.png");
        waveTexture = new Texture("player_wave.png");
        robotTexture = new Texture("player_robot.png");
        spiderTexture = new Texture("player_spider.png");

        setRegion(cubeTexture);

        setBounds(0, 0, 30 / Constants.PPM, 30 / Constants.PPM);
        setOrigin(getWidth() / 2, getHeight() / 2);

        definePlayer();
        resetInterpolation();

        this.movementStrategy = new CubeStrategy();
    }

    // --- GROUND DETECTION METHODS ---
    public void addFootContact() { footContacts++; }
    public void removeFootContact() { footContacts--; }
    public boolean isOnGround() { return footContacts > 0; }

    // --- INTERPOLATION ---
    public void resetInterpolation() {
        previousPosition.set(b2body.getPosition());
        interpolatedPosition.set(b2body.getPosition());
        setPosition(b2body.getPosition().x - getWidth()/2, b2body.getPosition().y - getHeight()/2);
    }

    public void capturePreviousPosition() { previousPosition.set(b2body.getPosition()); }

    public void updateVisual(float alpha) {
        Vector2 currentPosition = b2body.getPosition();
        float x = previousPosition.x * (1 - alpha) + currentPosition.x * alpha;
        float y = previousPosition.y * (1 - alpha) + currentPosition.y * alpha;
        interpolatedPosition.set(x, y);
        setPosition(x - getWidth() / 2, y - getHeight() / 2);
    }

    public Vector2 getInterpolatedPosition() { return interpolatedPosition; }

    // --- UPDATE ---
    public void update(float dt) {
        movementStrategy.update(this, dt);
        updateVisualRotation(dt);
    }

    private void updateVisualRotation(float dt) {
        float velocityY = b2body.getLinearVelocity().y;
        if (movementStrategy instanceof CubeStrategy) {
            // Gunakan sensor ground, bukan kecepatan
            if (!isOnGround()) {
                rotate(-450f * dt);
            } else {
                float rotation = getRotation() % 360;
                float targetRotation = Math.round(rotation / 90f) * 90f;
                setRotation(MathUtils.lerp(rotation, targetRotation, 0.2f));
            }
        }
        else if (movementStrategy instanceof ShipStrategy) {
            float targetRotation = velocityY * 3.0f;
            targetRotation = MathUtils.clamp(targetRotation, -45, 45);
            setRotation(MathUtils.lerp(getRotation(), targetRotation, 0.1f));
        }
            // --- STRATEGI BALL (BARU) ---
        else if (movementStrategy instanceof BallStrategy) {
            // Cek arah gravitasi
            float gravity = b2body.getGravityScale();

            // Kecepatan putar (Visual rolling)
            float rotationSpeed = 600f;

            if (gravity > 0) {
                // Di Lantai: Putar Clockwise (Negatif)
                rotate(-rotationSpeed * dt);
            } else {
                // Di Atap: Putar Counter-Clockwise (Positif)
                rotate(rotationSpeed * dt);
            }
        } else if (movementStrategy instanceof UfoStrategy) {
            // UFO: Sedikit miring saat lompat (seperti flappy bird)
            // Tapi biasanya di Geometry Dash UFO tidak berputar, atau rotasi statis.
            // Kita buat sedikit tilt saat naik.
            float targetRotation = velocityY * 2.0f;
            targetRotation = MathUtils.clamp(targetRotation, -20, 20); // Tilt dikit aja
            setRotation(MathUtils.lerp(getRotation(), targetRotation, 0.1f));
        } else if (movementStrategy instanceof WaveStrategy) {
            float targetRotation = 0;
            if (velocityY > 0.1f) targetRotation = 45f;
            else if (velocityY < -0.1f) targetRotation = -45f;
            setRotation(MathUtils.lerp(getRotation(), targetRotation, 0.3f));
        }
        else if (movementStrategy instanceof RobotStrategy) {
            // Robot biasanya tetap tegak (tidak berputar)
            // Kecuali Anda punya animasi lari. Untuk sekarang set 0.
            setRotation(0);
        }
        else if (movementStrategy instanceof SpiderStrategy) {
            // Jika gravitasi positif (lantai), rotasi 0
            // Jika gravitasi negatif (atap), rotasi 180 (terbalik)
            float targetRotation = (b2body.getGravityScale() > 0) ? 0 : 180;

            // Lerp cepat atau set langsung
            setRotation(MathUtils.lerp(getRotation(), targetRotation, 0.3f));
        }
        else {
            setRotation(0);
        }
    }

    public void jump() { movementStrategy.handleInput(this); }

    private void definePlayer() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(32 / Constants.PPM, 64 / Constants.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        bdef.fixedRotation = true;

        b2body = world.createBody(bdef);
        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox((float)14 / Constants.PPM, (float)14 / Constants.PPM);
        fdef.shape = shape;
        fdef.friction = 0;
        fdef.restitution = 0;
        b2body.setUserData(this);
        b2body.createFixture(fdef).setUserData("PLAYER");
        shape.dispose();
    }

    @Override public void draw(Batch batch) { super.draw(batch); }
    public void dispose() { if (getTexture() != null) getTexture().dispose(); }

    public void setStrategy(MovementStrategy strategy) {
        this.movementStrategy = strategy;

        // Reset Rotasi Visual
        setRotation(0);

        // Reset Fisika berdasarkan Strategy
        if (strategy instanceof CubeStrategy) {
            b2body.setGravityScale(1f);
            setRegion(cubeTexture);
        } else if (strategy instanceof ShipStrategy) {
            setRegion(shipTexture);
            b2body.setGravityScale(0.5f);
        } else if (strategy instanceof BallStrategy) {
            // Default Ball Gravity start normal
            setRegion(ballTexture);
            b2body.setGravityScale(1f);
        } else if (strategy instanceof UfoStrategy) {
            setRegion(ufoTexture);
            // UFO gravity 1f (normal)
        } else if (strategy instanceof WaveStrategy) {
            setRegion(waveTexture);
            b2body.setGravityScale(0f);
        } else if (strategy instanceof RobotStrategy) {
            setRegion(robotTexture);
            b2body.setGravityScale(1f);
        } else if (strategy instanceof SpiderStrategy) {
            setRegion(spiderTexture);
            // Gravity normal 1f
        }
    }
    public static float getMovementSpeed() { return MOVEMENT_SPEED; }
    public static float getJumpForce() { return JUMP_FORCE; }

    public MovementStrategy getMovementStrategy() {
        return movementStrategy;
    }
}
