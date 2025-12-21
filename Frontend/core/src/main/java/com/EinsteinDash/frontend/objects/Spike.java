package com.EinsteinDash.frontend.objects;

import com.EinsteinDash.frontend.utils.Constants;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Pool;

/**
 * Spike - Obstacle berbahaya yang membunuh player saat disentuh.
 * Menggunakan sensor (tidak memiliki collision fisik).
 */
public class Spike implements Pool.Poolable {

    // === PHYSICS ===
    public Body body;
    private World world;

    // === RENDERING ===
    private Texture texture;
    private boolean active;
    private static Texture SPIKE_TEXTURE;

    // ==================== CONSTRUCTOR ====================

    public Spike() {
        this.active = false;
        if (SPIKE_TEXTURE == null) {
            SPIKE_TEXTURE = new Texture("game/spike.png");
        }
        this.texture = SPIKE_TEXTURE;
    }

    // ==================== INITIALIZATION ====================

    public void init(World world, float x, float y) {
        this.world = world;
        this.active = true;
        defineBody(x, y);
    }

    /** Buat Box2D sensor (tembus, hanya deteksi collision) */
    private void defineBody(float x, float y) {
        BodyDef bdef = new BodyDef();
        bdef.position.set((x * 32 + 16) / Constants.PPM, (y * 32 + 16) / Constants.PPM);
        bdef.type = BodyDef.BodyType.StaticBody;
        body = world.createBody(bdef);

        // Hitbox segitiga (lebih kecil dari visual)
        float scale = 1.3f;
        PolygonShape shape = new PolygonShape();
        Vector2[] vertices = new Vector2[4];
        vertices[0] = new Vector2((-3 * scale) / Constants.PPM, (-5 * scale) / Constants.PPM);
        vertices[1] = new Vector2((3 * scale) / Constants.PPM, (-5 * scale) / Constants.PPM);
        vertices[2] = new Vector2((-3 * scale) / Constants.PPM, (6 * scale) / Constants.PPM);
        vertices[3] = new Vector2((3 * scale) / Constants.PPM, (6 * scale) / Constants.PPM);
        shape.set(vertices);

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.isSensor = true;  // Sensor = tidak ada collision fisik

        body.createFixture(fdef).setUserData("SPIKE");
        body.setUserData("SPIKE");

        shape.dispose();
    }

    // ==================== RENDERING ====================

    public void draw(SpriteBatch batch) {
        if (!active) return;

        float visualSize = (32 / Constants.PPM) * 1.6f;
        batch.draw(texture,
            body.getPosition().x - visualSize / 2,
            body.getPosition().y - visualSize / 2,
            visualSize, visualSize);
    }

    // ==================== POOL RESET ====================

    @Override
    public void reset() {
        if (body != null) {
            world.destroyBody(body);
            body = null;
        }
        this.active = false;
    }
}
