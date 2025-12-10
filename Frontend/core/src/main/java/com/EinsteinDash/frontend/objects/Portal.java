package com.EinsteinDash.frontend.objects;

import com.EinsteinDash.frontend.utils.Constants;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Pool;

/**
 * Portal - Mengubah mode gerakan player.
 * Tipe: PORTAL_SHIP, PORTAL_CUBE, PORTAL_BALL, PORTAL_UFO, etc.
 */
public class Portal implements Pool.Poolable {

    // === PHYSICS ===
    public Body body;
    private World world;

    // === STATE ===
    private Texture texture;
    private boolean active;
    private String type;

    // === SHARED TEXTURES ===
    private static Texture SHIP_PORTAL_TEXTURE;
    private static Texture CUBE_PORTAL_TEXTURE;
    private static Texture BALL_PORTAL_TEXTURE;
    private static Texture UFO_PORTAL_TEXTURE;
    private static Texture WAVE_PORTAL_TEXTURE;
    private static Texture ROBOT_PORTAL_TEXTURE;
    private static Texture SPIDER_PORTAL_TEXTURE;

    // ==================== CONSTRUCTOR ====================

    public Portal() {
        this.active = false;
        loadTextures();
    }

    /** Load semua texture portal (sekali saja) */
    private void loadTextures() {
        if (SHIP_PORTAL_TEXTURE == null)
            SHIP_PORTAL_TEXTURE = new Texture("portal_ship.png");
        if (CUBE_PORTAL_TEXTURE == null)
            CUBE_PORTAL_TEXTURE = new Texture("portal_cube.png");
        if (BALL_PORTAL_TEXTURE == null)
            BALL_PORTAL_TEXTURE = new Texture("portal_ball.png");
        if (UFO_PORTAL_TEXTURE == null)
            UFO_PORTAL_TEXTURE = new Texture("portal_ufo.png");
        if (WAVE_PORTAL_TEXTURE == null)
            WAVE_PORTAL_TEXTURE = new Texture("portal_wave.png");
        if (ROBOT_PORTAL_TEXTURE == null)
            ROBOT_PORTAL_TEXTURE = new Texture("portal_robot.png");
        if (SPIDER_PORTAL_TEXTURE == null)
            SPIDER_PORTAL_TEXTURE = new Texture("portal_spider.png");
    }

    // ==================== INITIALIZATION ====================

    public void init(World world, float x, float y, String type) {
        this.world = world;
        this.active = true;
        this.type = type;

        // Pilih texture berdasarkan tipe
        switch (type) {
            case "PORTAL_SHIP":
                this.texture = SHIP_PORTAL_TEXTURE;
                break;
            case "PORTAL_BALL":
                this.texture = BALL_PORTAL_TEXTURE;
                break;
            case "PORTAL_UFO":
                this.texture = UFO_PORTAL_TEXTURE;
                break;
            case "PORTAL_WAVE":
                this.texture = WAVE_PORTAL_TEXTURE;
                break;
            case "PORTAL_ROBOT":
                this.texture = ROBOT_PORTAL_TEXTURE;
                break;
            case "PORTAL_SPIDER":
                this.texture = SPIDER_PORTAL_TEXTURE;
                break;

            // GRAVITY PORTAL (Reuse Cube Texture but tinted later if possible, or just
            // same)
            case "PORTAL_GRAVITY_UP":
                this.texture = CUBE_PORTAL_TEXTURE;
                break;
            case "PORTAL_GRAVITY_DOWN":
                this.texture = CUBE_PORTAL_TEXTURE;
                break;

            // SPEED PORTAL (Reuse Ship Texture)
            case "PORTAL_SPEED_0_5":
            case "PORTAL_SPEED_1":
            case "PORTAL_SPEED_2":
            case "PORTAL_SPEED_3":
            case "PORTAL_SPEED_4":
                this.texture = SHIP_PORTAL_TEXTURE;
                break;

            default:
                this.texture = CUBE_PORTAL_TEXTURE;
                break;
        }

        defineBody(x, y);
    }

    private void defineBody(float x, float y) {
        BodyDef bdef = new BodyDef();
        bdef.position.set((x * 32 + 16) / Constants.PPM, (y * 32 + 32) / Constants.PPM);
        bdef.type = BodyDef.BodyType.StaticBody;
        body = world.createBody(bdef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(10 / Constants.PPM, 40 / Constants.PPM); // Lebar 20px, Tinggi 80px

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.isSensor = true;

        body.setUserData(this); // Simpan referensi untuk collision detection
        body.createFixture(fdef).setUserData("PORTAL");

        shape.dispose();
    }

    // ==================== GETTERS ====================

    public String getType() {
        return type;
    }

    // ==================== RENDERING ====================

    public void draw(SpriteBatch batch) {
        if (!active)
            return;

        float width = 20 / Constants.PPM;
        float height = 80 / Constants.PPM;

        // Reset warna untuk menghindari tint dari object sebelumnya
        Color oldColor = batch.getColor();
        batch.setColor(Color.WHITE);

        // TINTING UNTUK PORTAL BARU (Agar pemain bisa membedakan)
        if (type.equals("PORTAL_GRAVITY_UP"))
            batch.setColor(Color.ORANGE);
        else if (type.equals("PORTAL_GRAVITY_DOWN"))
            batch.setColor(Color.BLUE);
        else if (type.startsWith("PORTAL_SPEED")) {
            if (type.endsWith("0_5"))
                batch.setColor(Color.ORANGE);
            else if (type.endsWith("1"))
                batch.setColor(Color.WHITE);
            else if (type.endsWith("2"))
                batch.setColor(Color.GREEN);
            else if (type.endsWith("3"))
                batch.setColor(Color.MAGENTA);
            else if (type.endsWith("4"))
                batch.setColor(Color.RED);
        }

        batch.draw(texture,
                body.getPosition().x - width / 2,
                body.getPosition().y - height / 2,
                width, height);

        batch.setColor(oldColor);
    }

    // ==================== POOL RESET ====================

    @Override
    public void reset() {
        if (body != null) {
            world.destroyBody(body);
            body = null;
        }
        this.active = false;
        this.type = null;
        this.texture = null;
    }
}
