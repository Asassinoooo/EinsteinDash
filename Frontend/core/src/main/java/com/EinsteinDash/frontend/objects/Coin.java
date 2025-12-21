package com.EinsteinDash.frontend.objects;

import com.EinsteinDash.frontend.utils.Constants;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Pool;

/**
 * Coin - Collectible yang bisa diambil player.
 * Saat diambil, coin dihapus dari game dan menambah skor.
 */
public class Coin implements Pool.Poolable {

    // === PHYSICS ===
    public Body body;
    private World world;

    // === STATE ===
    private Texture texture;
    private boolean active;
    private boolean isCollected;
    private static Texture COIN_TEXTURE;

    // ==================== CONSTRUCTOR ====================

    public Coin() {
        this.active = false;
        this.isCollected = false;
        if (COIN_TEXTURE == null) {
            COIN_TEXTURE = new Texture("game/coin.png");
        }
        this.texture = COIN_TEXTURE;
    }

    // ==================== INITIALIZATION ====================

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
        shape.setRadius(10 / Constants.PPM);

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.isSensor = true;  // Sensor = bisa dilewati

        body.createFixture(fdef).setUserData("COIN");
        body.setUserData(this);  // Simpan referensi ke object ini

        shape.dispose();
    }

    // ==================== COLLECTION ====================

    /** Tandai coin sebagai sudah diambil */
    public void collect() {
        isCollected = true;
        active = false;
    }

    public boolean isCollected() { return isCollected; }

    // ==================== RENDERING ====================

    public void draw(SpriteBatch batch) {
        if (!active || isCollected) return;

        float size = 24 / Constants.PPM;
        batch.draw(texture,
            body.getPosition().x - size / 2,
            body.getPosition().y - size / 2,
            size, size);
    }

    // ==================== POOL RESET ====================

    @Override
    public void reset() {
        if (body != null) {
            world.destroyBody(body);
            body = null;
        }
        this.active = false;
        this.isCollected = false;
    }
}
