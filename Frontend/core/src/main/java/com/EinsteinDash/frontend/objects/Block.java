package com.EinsteinDash.frontend.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Pool;
import com.EinsteinDash.frontend.utils.Constants;

public class Block implements Pool.Poolable {
    public Body body;
    private World world;
    private Texture texture;
    private boolean active;

    // Texture static
    private static Texture BLOCK_TEXTURE;

    public Block() {
        this.active = false;
        // Load gambar jika belum ada
        if (BLOCK_TEXTURE == null) {
            BLOCK_TEXTURE = new Texture("block.png");
        }
        this.texture = BLOCK_TEXTURE;
    }

    // Reuse object
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

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(16 / Constants.PPM, 16 / Constants.PPM);

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;

        body.createFixture(fdef).setUserData("BLOCK");
        body.setUserData("BLOCK");

        shape.dispose();
    }

    public void draw(SpriteBatch batch) {
        if (!active) return;

        float size = (32 / Constants.PPM);
        float visualSize = size * 1f;

        batch.draw(texture,
            body.getPosition().x - visualSize/2,
            body.getPosition().y - visualSize/2,
            visualSize, visualSize);
    }

    @Override
    public void reset() {
        // Reset body saat dikembalikan ke pool
        if (body != null) {
            world.destroyBody(body);
            body = null;
        }
        this.active = false;
    }
}
