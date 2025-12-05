package com.EinsteinDash.frontend.utils;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class Player extends Sprite {
    public World world;
    public Body b2body;

    // Konstanta kecepatan karakter (2 meter per detik)
    private static final float MOVEMENT_SPEED = 1.25f;
    // Kekuatan lompatan
    private static final float JUMP_FORCE = 3.3f;

    public Player(World world) {
        this.world = world;
        definePlayer();
    }

    public void update(float dt) {
        // 1. Gerak Otomatis ke Kanan (Auto-Runner)
        // Kita memaksa kecepatan X agar konstan, tapi membiarkan kecepatan Y (gravitasi) bekerja alami.
        // Jika player menabrak dinding dan melambat, dia akan didorong lagi.
        if (b2body.getLinearVelocity().x <= MOVEMENT_SPEED) {
            b2body.setLinearVelocity(new Vector2(MOVEMENT_SPEED, b2body.getLinearVelocity().y));
        }

        // 2. Sinkronisasi Sprite dengan Body Fisika
        setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
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

        shape.dispose();
    }

    public void dispose() {
        if (getTexture() != null) {
            getTexture().dispose();
        }
    }
}
