package com.EinsteinDash.frontend.objects;

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
    private static Texture GOAL_TEXTURE;

    public Goal() {
        this.active = false;
        if (GOAL_TEXTURE == null) {
            GOAL_TEXTURE = new Texture("portal.png");
        }
        this.texture = GOAL_TEXTURE;
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

        PolygonShape shape = new PolygonShape();
        // Ukuran
        shape.setAsBox(20 / Constants.PPM, 40 / Constants.PPM);

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.isSensor = true;

        // Labeling
        body.createFixture(fdef).setUserData("GOAL");
        body.setUserData("GOAL");

        shape.dispose();
    }

    public void draw(SpriteBatch batch) {
        if (!active) return;

        // Visual agak besar
        float width = 64 / Constants.PPM;
        float height = 128 / Constants.PPM;

        batch.draw(texture,
            body.getPosition().x - width/2,
            body.getPosition().y - height/2,
            width, height);
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
