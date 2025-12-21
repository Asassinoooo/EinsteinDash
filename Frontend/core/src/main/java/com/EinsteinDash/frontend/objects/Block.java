package com.EinsteinDash.frontend.objects;

import com.EinsteinDash.frontend.utils.Constants;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Pool;

/**
 * Block - Platform solid yang bisa diinjak player.
 * Menggunakan Object Pool untuk optimasi memori.
 */
public class Block implements Pool.Poolable {

    // === PHYSICS ===
    public Body body;
    private World world;

    // === RENDERING ===
    private Texture texture;
    private boolean active;
    private static Texture BLOCK_TEXTURE;  // Shared texture (hemat memori)

    // ==================== CONSTRUCTOR ====================

    public Block() {
        this.active = false;
        if (BLOCK_TEXTURE == null) {
            BLOCK_TEXTURE = new Texture("game/block.png");
        }
        this.texture = BLOCK_TEXTURE;
    }

    // ==================== INITIALIZATION ====================

    /** Inisialisasi block dari pool dengan posisi tertentu */
    public void init(World world, float x, float y) {
        this.world = world;
        this.active = true;
        defineBody(x, y);
    }

    /** Buat Box2D body */
    private void defineBody(float x, float y) {
        BodyDef bdef = new BodyDef();
        bdef.position.set((x * 32 + 16) / Constants.PPM, (y * 32 + 16) / Constants.PPM);
        bdef.type = BodyDef.BodyType.StaticBody;
        body = world.createBody(bdef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(16 / Constants.PPM, 14 / Constants.PPM);

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        body.createFixture(fdef).setUserData("BLOCK");
        body.setUserData("BLOCK");

        shape.dispose();
    }

    // ==================== RENDERING ====================

    public void draw(SpriteBatch batch) {
        if (!active) return;

        float size = 32 / Constants.PPM;
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
    }
}
