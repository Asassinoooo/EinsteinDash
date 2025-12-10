package com.EinsteinDash.frontend.objects;

import com.EinsteinDash.frontend.utils.Constants;
import com.EinsteinDash.frontend.utils.GamePalette;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Pool;

/**
 * Goal - Garis finish level.
 * Saat player menyentuh goal, level dianggap selesai.
 */
public class Goal implements Pool.Poolable {

    // === PHYSICS ===
    public Body body;
    private World world;

    // === RENDERING ===
    private Texture texture;
    private boolean active;
    private static Texture LINE_TEXTURE;  // Texture garis 1x1 pixel

    // ==================== CONSTRUCTOR ====================

    public Goal() {
        this.active = false;

        // Buat texture garis putih (akan diwarnai saat draw)
        if (LINE_TEXTURE == null) {
            Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
            pixmap.setColor(Color.WHITE);
            pixmap.fill();
            LINE_TEXTURE = new Texture(pixmap);
            pixmap.dispose();
        }
        this.texture = LINE_TEXTURE;
    }

    // ==================== INITIALIZATION ====================

    public void init(World world, float x, float y) {
        this.world = world;
        this.active = true;
        defineBody(x, y);
    }

    private void defineBody(float x, float y) {
        BodyDef bdef = new BodyDef();
        bdef.position.set((x * 32 + 16) / Constants.PPM, (Constants.V_HEIGHT / 2f) / Constants.PPM);
        bdef.type = BodyDef.BodyType.StaticBody;
        body = world.createBody(bdef);

        // Collider sangat tipis dan tinggi
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(2 / Constants.PPM, 15);  // Lebar 4px, Tinggi 30m

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.isSensor = true;

        body.createFixture(fdef).setUserData("GOAL");
        body.setUserData("GOAL");

        shape.dispose();
    }

    // ==================== RENDERING ====================

    public void draw(SpriteBatch batch) {
        if (!active) return;

        float width = 5 / Constants.PPM;
        float height = Constants.V_HEIGHT / Constants.PPM * 2;

        // Gambar garis kuning
        batch.setColor(GamePalette.Neon.YELLOW);
        batch.draw(texture,
            body.getPosition().x - width / 2,
            body.getPosition().y - height / 2,
            width, height);
        batch.setColor(Color.WHITE);  // Reset warna
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
