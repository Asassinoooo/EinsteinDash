package com.EinsteinDash.frontend.utils;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.EinsteinDash.frontend.strategies.CubeStrategy;
import com.EinsteinDash.frontend.strategies.MovementStrategy;
import com.EinsteinDash.frontend.strategies.ShipStrategy;

public class Player extends Sprite {
    public World world;
    public Body b2body;
    private MovementStrategy movementStrategy;

    private static final float MOVEMENT_SPEED = 3f;
    private static final float JUMP_FORCE = 6.5f;

    private Texture playerTexture;
    private Vector2 previousPosition = new Vector2();
    private Vector2 interpolatedPosition = new Vector2();

    // --- LOGIC BARU: SENSOR TANAH ---
    private int footContacts = 0; // Berapa banyak objek yang diinjak

    public Player(World world) {
        this.world = world;
        playerTexture = new Texture("player.png");
        setRegion(playerTexture);

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
        } else {
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
        setRotation(0);
    }
    public static float getMovementSpeed() { return MOVEMENT_SPEED; }
    public static float getJumpForce() { return JUMP_FORCE; }
}
