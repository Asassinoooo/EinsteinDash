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

    // === NEW PORTAL TEXTURES ===
    private static Texture GRAVITY_NORMAL_TEXTURE;
    private static Texture GRAVITY_REVERSE_TEXTURE;
    private static Texture SPEED_0_5_TEXTURE;
    private static Texture SPEED_1_TEXTURE;
    private static Texture SPEED_2_TEXTURE;
    private static Texture SPEED_3_TEXTURE;
    private static Texture SPEED_4_TEXTURE;

    // ==================== CONSTRUCTOR ====================

    public Portal() {
        this.active = false;
        loadTextures();
    }

    /** Load semua texture portal (sekali saja) */
    private void loadTextures() {
        if (SHIP_PORTAL_TEXTURE == null)
            SHIP_PORTAL_TEXTURE = new Texture("portal/portal_ship.png");
        if (CUBE_PORTAL_TEXTURE == null)
            CUBE_PORTAL_TEXTURE = new Texture("portal/portal_cube.png");
        if (BALL_PORTAL_TEXTURE == null)
            BALL_PORTAL_TEXTURE = new Texture("portal/portal_ball.png");
        if (UFO_PORTAL_TEXTURE == null)
            UFO_PORTAL_TEXTURE = new Texture("portal/portal_ufo.png");
        if (WAVE_PORTAL_TEXTURE == null)
            WAVE_PORTAL_TEXTURE = new Texture("portal/portal_wave.png");
        if (ROBOT_PORTAL_TEXTURE == null)
            ROBOT_PORTAL_TEXTURE = new Texture("portal/portal_robot.png");
        if (SPIDER_PORTAL_TEXTURE == null)
            SPIDER_PORTAL_TEXTURE = new Texture("portal/portal_spider.png");

        // NEW TEXTURES
        if (GRAVITY_NORMAL_TEXTURE == null)
            GRAVITY_NORMAL_TEXTURE = new Texture("portal/portal_normal_gravity.png");
        if (GRAVITY_REVERSE_TEXTURE == null)
            GRAVITY_REVERSE_TEXTURE = new Texture("portal/portal_reverse_gravity.png");

        if (SPEED_0_5_TEXTURE == null)
            SPEED_0_5_TEXTURE = new Texture("portal/portal_0.5x_speed.png");
        if (SPEED_1_TEXTURE == null)
            SPEED_1_TEXTURE = new Texture("portal/portal_1x_speed.png");
        if (SPEED_2_TEXTURE == null)
            SPEED_2_TEXTURE = new Texture("portal/portal_2x_speed.png");
        if (SPEED_3_TEXTURE == null)
            SPEED_3_TEXTURE = new Texture("portal/portal_3x_speed.png");
        if (SPEED_4_TEXTURE == null)
            SPEED_4_TEXTURE = new Texture("portal/portal_4x_speed.png");
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

            // GRAVITY PORTAL
            case "PORTAL_GRAVITY_UP":
                this.texture = GRAVITY_REVERSE_TEXTURE; // Up is reverse gravity
                break;
            case "PORTAL_GRAVITY_DOWN":
                this.texture = GRAVITY_NORMAL_TEXTURE; // Down is normal gravity
                break;

            // SPEED PORTAL
            case "PORTAL_SPEED_0_5":
                this.texture = SPEED_0_5_TEXTURE;
                break;
            case "PORTAL_SPEED_1":
                this.texture = SPEED_1_TEXTURE;
                break;
            case "PORTAL_SPEED_2":
                this.texture = SPEED_2_TEXTURE;
                break;
            case "PORTAL_SPEED_3":
                this.texture = SPEED_3_TEXTURE;
                break;
            case "PORTAL_SPEED_4":
                this.texture = SPEED_4_TEXTURE;
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

        float height = 80 / Constants.PPM;
        // Calculate width dynamically based on texture aspect ratio
        float aspect = (float) texture.getWidth() / texture.getHeight();
        float width = height * aspect;

        // Reset warna agar texture tidak ter-tint warna dari object sebelumnya
        Color oldColor = new Color(batch.getColor());
        batch.setColor(Color.WHITE);

        // Draw portal
        batch.draw(texture,
                body.getPosition().x - width / 2,
                body.getPosition().y - height / 2,
                width, height);

        // Kembalikan ke warna semula
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
