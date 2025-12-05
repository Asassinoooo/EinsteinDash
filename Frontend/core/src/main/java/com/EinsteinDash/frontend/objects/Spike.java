package com.EinsteinDash.frontend.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Pool;
import com.EinsteinDash.frontend.utils.Constants;

public class Spike implements Pool.Poolable {
    public Body body;
    private World world;
    private Texture texture;
    private boolean active;
    private static Texture SPIKE_TEXTURE;

    public Spike() {
        this.active = false;
        if (SPIKE_TEXTURE == null) {
            SPIKE_TEXTURE = new Texture("spike.png");
        }
        this.texture = SPIKE_TEXTURE;
    }

    public void init(World world, float x, float y) {
        this.world = world;
        this.active = true;
        defineBody(x, y);
    }

    private void defineBody(float x, float y) {
        BodyDef bdef = new BodyDef();
        bdef.position.set((x * 32 + 16) / Constants.PPM, (y * 32 + 16) / Constants.PPM);
        bdef.type = BodyDef.BodyType.StaticBody;
        body = world.createBody(bdef);

        // Skala hit-box
        float scale = 1.3f;
        PolygonShape shape = new PolygonShape();
        Vector2[] vertices = new Vector2[3];
        vertices[0] = new Vector2((-16 * scale) / Constants.PPM, (-16 * scale) / Constants.PPM);
        vertices[1] = new Vector2((16 * scale) / Constants.PPM, (-16 * scale) / Constants.PPM);
        vertices[2] = new Vector2(0, (16 * scale) / Constants.PPM);
        shape.set(vertices);

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.isSensor = true;

        body.createFixture(fdef).setUserData("SPIKE");
        body.setUserData("SPIKE");

        shape.dispose();
    }

    public void draw(SpriteBatch batch) {
        if (!active) return;

        float size = (32 / Constants.PPM);
        float visualSize = size * 1.6f;

        batch.draw(texture,
            body.getPosition().x - visualSize/2,
            body.getPosition().y - visualSize/2,
            visualSize, visualSize);
    }

    @Override
    public void reset() {
        if (body != null) {
            world.destroyBody(body);
            body = null;
        }
        this.active = false;
    }
}
