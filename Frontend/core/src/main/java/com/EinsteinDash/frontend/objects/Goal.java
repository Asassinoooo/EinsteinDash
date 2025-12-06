package com.EinsteinDash.frontend.objects;

import com.EinsteinDash.frontend.utils.GamePalette;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Pool;
import com.EinsteinDash.frontend.utils.Constants;

public class Goal implements Pool.Poolable {
    public Body body;
    private World world;
    private Texture texture;
    private boolean active;

    // Texture statis (dibuat sekali saja)
    private static Texture LINE_TEXTURE;

    public Goal() {
        this.active = false;

        // Texture line berwarna kuning
        if (LINE_TEXTURE == null) {
            Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
            pixmap.setColor(Color.WHITE);
            pixmap.fill();
            LINE_TEXTURE = new Texture(pixmap);
            pixmap.dispose(); // Hapus pixmap dari memori setelah jadi Texture
        }
        this.texture = LINE_TEXTURE;
    }

    public void init(World world, float x, float y) {
        this.world = world;
        this.active = true;
        defineBody(x, y);
    }

    private void defineBody(float x, float y) {
        BodyDef bdef = new BodyDef();
        // Posisi X sesuai JSON, tapi Y taruh di tengah layar
        bdef.position.set((x * 32 + 16) / Constants.PPM, (Constants.V_HEIGHT / 2f) / Constants.PPM);
        bdef.type = BodyDef.BodyType.StaticBody;
        body = world.createBody(bdef);

        // Bentuk Kotak Sangat Tinggi & Tipis
        PolygonShape shape = new PolygonShape();

        // Ukuran collider 4x30
        shape.setAsBox(2 / Constants.PPM, 15);

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.isSensor = true; // Tembus

        body.createFixture(fdef).setUserData("GOAL");
        body.setUserData("GOAL");

        shape.dispose();
    }

    public void draw(SpriteBatch batch) {
        if (!active) return;

        // Lebar 5px
        float width = 5 / Constants.PPM;
        // Tinggi setinggi layar
        float height = Constants.V_HEIGHT / Constants.PPM * 2; // Kali 2 biar aman

        // Warna
        batch.setColor(GamePalette.Neon.YELLOW);      // Set warna kuning

        // Gambar Garis
        batch.draw(texture,
            body.getPosition().x - width/2,
            body.getPosition().y - height/2,
            width, height);

        // Kembalikan warna default
        batch.setColor(Color.WHITE);
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
