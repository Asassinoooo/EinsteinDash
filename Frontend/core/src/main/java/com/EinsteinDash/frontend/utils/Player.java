package com.EinsteinDash.frontend.utils;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Player extends Sprite {
    public World world;
    public Body b2body;

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
    }

    public void update(float dt) {
        // Gerak Otomatis ke Kanan (Auto-Runner)
        // Kita memaksa kecepatan X agar konstan, tapi membiarkan kecepatan Y (gravitasi) bekerja alami.
        // Jika player menabrak dinding dan melambat, dia akan didorong lagi.
        if (b2body.getLinearVelocity().x <= MOVEMENT_SPEED) {
            b2body.setLinearVelocity(new Vector2(MOVEMENT_SPEED, b2body.getLinearVelocity().y));
        }

        // Update Posisi Sprite
        // Sprite di LibGDX dihitung dari pojok kiri-bawah, Box2D dari tengah.
        // Posisi Body adalah setengah ukuran sprite
        setPosition(
            b2body.getPosition().x - getWidth() / 2,
            b2body.getPosition().y - getHeight() / 2
        );

        // LOGIKA ROTASI
        // Jika sedang di udara (Velocity Y tidak 0), putar searah jarum jam (-5 derajat per frame)
        if (Math.abs(b2body.getLinearVelocity().y) > 0.01f) {
            rotate(-5f);
        } else {
            // Jika di tanah, paksa rotasi ke kelipatan 90 terdekat agar kotak mendarat rata
            float angle = getRotation() % 360;
            if (angle < 0) angle += 360;
            // Reset ke 0
            setRotation(Math.round(getRotation() / 90f) * 90f);
        }
    }

    public void jump() {
        // Logika Lompat: Hanya boleh lompat jika kecepatan vertikal mendekati 0 (sedang di tanah)
        // Nilai 0.01f adalah toleransi kecil.
        if (Math.abs(b2body.getLinearVelocity().y) < 0.01f) {
            b2body.applyLinearImpulse(new Vector2(0, JUMP_FORCE), b2body.getWorldCenter(), true);
        }
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

        b2body.createFixture(fdef).setUserData("PLAYER");
        b2body.setUserData("PLAYER");

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
}
