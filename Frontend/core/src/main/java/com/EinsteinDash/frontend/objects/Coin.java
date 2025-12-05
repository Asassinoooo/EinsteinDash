package com.EinsteinDash.frontend.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Pool;
import com.EinsteinDash.frontend.utils.Constants;

public class Coin implements Pool.Poolable {
    public Body body;
    private World world;
    private Texture texture;
    private boolean active;     //  Koin sedang ada di layar
    private boolean isCollected; // Koin sudah diambil player

    private static Texture COIN_TEXTURE;

    public Coin() {
        this.active = false;
        this.isCollected = false;
        if (COIN_TEXTURE == null) {
            COIN_TEXTURE = new Texture("coin.png");
        }
        this.texture = COIN_TEXTURE;
    }

    public void init(World world, float x, float y) {
        this.world = world;
        this.active = true;
        this.isCollected = false;
        defineBody(x, y);
    }

    private void defineBody(float x, float y) {
        BodyDef bdef = new BodyDef();
        bdef.position.set((x * 32 + 16) / Constants.PPM, (y * 32 + 16) / Constants.PPM);
        bdef.type = BodyDef.BodyType.StaticBody;
        body = world.createBody(bdef);

        CircleShape shape = new CircleShape();
        // Ukuran radius 10 pixel (total diameter 20px)
        shape.setRadius(10 / Constants.PPM);

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.isSensor = true;

        // Labeling untuk deteksi
        body.createFixture(fdef).setUserData("COIN");
        body.setUserData(this);

        shape.dispose();
    }

    public void collect() {
        isCollected = true;
        active = false;
    }

    public void draw(SpriteBatch batch) {
        // Jangan gambar jika sudah diambil atau tidak aktif
        if (!active || isCollected) return;

        float size = 24 / Constants.PPM; // Ukuran visual

        batch.draw(texture,
            body.getPosition().x - size/2,
            body.getPosition().y - size/2,
            size, size);
    }

    @Override
    public void reset() {
        if (body != null) {
            world.destroyBody(body);
            body = null;
        }
        this.active = false;
        this.isCollected = false;
    }

    // Getter untuk mengecek status
    public boolean isCollected() {
        return isCollected;
    }
}
