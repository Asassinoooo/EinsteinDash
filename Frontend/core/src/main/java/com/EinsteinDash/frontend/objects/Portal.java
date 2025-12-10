package com.EinsteinDash.frontend.objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Pool;
import com.EinsteinDash.frontend.utils.Constants;

public class Portal implements Pool.Poolable {
    public Body body;
    private World world;
    private Texture texture; // Tekstur yang aktif untuk instance ini
    private boolean active;
    private String type;

    private static Texture SHIP_PORTAL_TEXTURE;
    private static Texture CUBE_PORTAL_TEXTURE;
    private static Texture BALL_PORTAL_TEXTURE;
    private static Texture UFO_PORTAL_TEXTURE;
    private static Texture WAVE_PORTAL_TEXTURE;
    private static Texture ROBOT_PORTAL_TEXTURE;
    private static Texture SPIDER_PORTAL_TEXTURE;

    public Portal() {
        this.active = false;

        if (SHIP_PORTAL_TEXTURE == null) {
            SHIP_PORTAL_TEXTURE = new Texture("portal_ship.png");
        }
        if (CUBE_PORTAL_TEXTURE == null) {
            CUBE_PORTAL_TEXTURE = new Texture("portal_cube.png");
        }
        if (BALL_PORTAL_TEXTURE == null) {
            BALL_PORTAL_TEXTURE =  new Texture("portal_ball.png");
        }
        if  (UFO_PORTAL_TEXTURE == null) {
            UFO_PORTAL_TEXTURE = new Texture("portal_ufo.png");
        }
        if (WAVE_PORTAL_TEXTURE == null) {
            WAVE_PORTAL_TEXTURE = new Texture("portal_wave.png");
        }
        if  (ROBOT_PORTAL_TEXTURE == null) {
            ROBOT_PORTAL_TEXTURE = new Texture("portal_robot.png");
        }
        if (SPIDER_PORTAL_TEXTURE == null) {
            SPIDER_PORTAL_TEXTURE = new Texture("portal_spider.png");
        }

    }

    public void init(World world, float x, float y, String type) {
        this.world = world;
        this.active = true;
        this.type = type;

        if ("PORTAL_SHIP".equals(type)) {
            this.texture = SHIP_PORTAL_TEXTURE;
        } else if ("PORTAL_CUBE".equals(type)) {
            this.texture = BALL_PORTAL_TEXTURE;
        } else if ("PORTAL_BALL".equals(type)) {
            this.texture = BALL_PORTAL_TEXTURE;
        } else if ("PORTAL_UFO".equals(type)) {
            this.texture = UFO_PORTAL_TEXTURE;
        } else if ("PORTAL_WAVE".equals(type)) {
            this.texture = WAVE_PORTAL_TEXTURE;
        } else if ("PORTAL_ROBOT".equals(type)) {
            this.texture = ROBOT_PORTAL_TEXTURE;
        } else if ("PORTAL_SPIDER".equals(type)) {
            this.texture  = SPIDER_PORTAL_TEXTURE;
        } else {
            this.texture = CUBE_PORTAL_TEXTURE;
        }

        defineBody(x, y);
    }

    private void defineBody(float x, float y) {
        BodyDef bdef = new BodyDef();
        bdef.position.set((x * 32 + 16) / Constants.PPM, (y * 32 + 32) / Constants.PPM);
        bdef.type = BodyDef.BodyType.StaticBody;
        body = world.createBody(bdef);

        PolygonShape shape = new PolygonShape();
        // Lebar 10px (total 20), Tinggi 40px (total 80) - Sesuaikan dengan ukuran gambar jika perlu
        shape.setAsBox(10 / Constants.PPM, 40 / Constants.PPM);

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.isSensor = true;

        body.setUserData(this);
        body.createFixture(fdef).setUserData("PORTAL");

        shape.dispose();
    }

    public void draw(SpriteBatch batch) {
        if (!active) return;

        // Ukuran visual (Sesuaikan jika gambar ukurannya beda)
        float width = 20 / Constants.PPM;
        float height = 80 / Constants.PPM;


        // Praktik baik: Pastikan warna batch Putih (Netral) sebelum menggambar
        // agar gambar PNG tidak terkena tint warna dari objek sebelumnya.
        Color oldColor = batch.getColor();
        batch.setColor(Color.WHITE);

        // Gambar tekstur yang sudah dipilih di method init()
        batch.draw(texture,
            body.getPosition().x - width/2,
            body.getPosition().y - height/2,
            width, height);

        // Kembalikan warna lama
        batch.setColor(oldColor);
    }

    public String getType() {
        return type;
    }

    @Override
    public void reset() {
        if (body != null) {
            world.destroyBody(body);
            body = null;
        }
        this.active = false;
        this.type = null;
        this.texture = null; // Reset referensi tekstur
    }
}
