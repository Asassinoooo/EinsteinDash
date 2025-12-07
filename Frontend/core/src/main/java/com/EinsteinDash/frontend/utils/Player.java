package com.EinsteinDash.frontend.utils;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.EinsteinDash.frontend.strategies.CubeStrategy;
import com.EinsteinDash.frontend.strategies.ShipStrategy;
import com.EinsteinDash.frontend.strategies.MovementStrategy;

public class Player extends Sprite {
    public World world;
    public Body b2body;
    private MovementStrategy movementStrategy;

    // Konstanta kecepatan karakter (2 meter per detik)
    private static final float MOVEMENT_SPEED = 1.25f;
    // Kekuatan lompatan
    private static final float JUMP_FORCE = 3.3f;
    // Variabel Texture
    private Texture playerTexture;

    public Player(World world) {
        this.world = world;
        playerTexture = new Texture("player.png");
        setRegion(playerTexture);

        // Set ukuran Sprite agar sama dengan ukuran Fisika (30px)
        setBounds(0, 0, 30 / Constants.PPM, 30 / Constants.PPM);

        // Set titik tengah rotasi di tengah sprite
        setOrigin(getWidth() / 2, getHeight() / 2);

        definePlayer();
        //UBAH MODE DISINI
        this.movementStrategy = new CubeStrategy();
    }

    public void update(float dt) {
        // DELEGASIKAN UPDATE KE STRATEGY
        movementStrategy.update(this, dt);

        // Update posisi sprite (Sama untuk semua mode)
        setPosition(
            b2body.getPosition().x - getWidth() / 2,
            b2body.getPosition().y - getHeight() / 2
        );
    }

    public void jump() {
        // DELEGASIKAN INPUT KE STRATEGY
        movementStrategy.handleInput(this);
    }

    private void definePlayer() {
        BodyDef bdef = new BodyDef();
        // Posisi Awal: (32, 64) dibagi PPM agar jadi satuan Meter
        bdef.position.set(32 / Constants.PPM, 64 / Constants.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody; // Dynamic = Bisa bergerak & kena gravitasi
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();

        // Ukuran Kotak: 15x15 piksel (Total lebar 30px)
        shape.setAsBox(15 / Constants.PPM, 15 / Constants.PPM);

        fdef.shape = shape;
        fdef.friction = 0;
        fdef.restitution = 0; // Tidak memantul

        b2body.setGravityScale(0.7f);

        b2body.setUserData(this);
        b2body.createFixture(fdef).setUserData("PLAYER");
        //b2body.setUserData("PLAYER");

        shape.dispose();
    }

    @Override
    public void draw(Batch batch) {
        super.draw(batch);
    }

    public void dispose() {
        if (getTexture() != null) {
            getTexture().dispose();
        }
    }

    public void setStrategy(MovementStrategy strategy) {
        this.movementStrategy = strategy;

        // Reset rotasi saat ganti mode agar tidak aneh
        setRotation(0);
    }

    public static float getMovementSpeed() {
        return MOVEMENT_SPEED;
    }

    public static float getJumpForce() {
        return JUMP_FORCE;
    }


}
