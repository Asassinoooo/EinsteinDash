package com.EinsteinDash.frontend.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

public class LevelFactory {

    private World world;

    public LevelFactory(World world) {
        this.world = world;
    }

    // Design Pattern: Factory Method
    // Menerima string JSON, menghasilkan objek-objek di dunia Box2D
    public void createLevel(String jsonLevelData) {
        if (jsonLevelData == null || jsonLevelData.isEmpty()) return;

        JsonValue root = new JsonReader().parse(jsonLevelData);

        for (JsonValue object : root) {
            String type = object.getString("type");
            float x = object.getFloat("x");
            float y = object.getFloat("y");

            switch (type) {
                case "BLOCK":
                    createBlock(x, y);
                    break;
                case "SPIKE":
                    createSpike(x, y);
                    break;
                case "GOAL":
                    createGoal(x, y);
                    break;
                default:
                    System.out.println("Unknown object: " + type);
            }
        }

        // Selalu buat lantai (Floor) panjang tak terbatas
        createFloor();
    }

    private void createBlock(float x, float y) {
        BodyDef bdef = new BodyDef();
        bdef.position.set((x * 32 + 16) / Constants.PPM, (y * 32 + 16) / Constants.PPM); // Asumsi 1 grid = 32px
        bdef.type = BodyDef.BodyType.StaticBody;
        Body body = world.createBody(bdef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(16 / Constants.PPM, 16 / Constants.PPM); // Setengah lebar/tinggi

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.friction = 0; // Agar player tidak nempel dinding
        body.createFixture(fdef).setUserData("BLOCK");

        shape.dispose();
    }

    private void createSpike(float x, float y) {
        BodyDef bdef = new BodyDef();
        bdef.position.set((x * 32 + 16) / Constants.PPM, (y * 32 + 16) / Constants.PPM);
        bdef.type = BodyDef.BodyType.StaticBody;
        Body body = world.createBody(bdef);

        // Bentuk Segitiga
        PolygonShape shape = new PolygonShape();
        Vector2[] vertices = new Vector2[3];
        vertices[0] = new Vector2(-16 / Constants.PPM, -16 / Constants.PPM); // Kiri Bawah
        vertices[1] = new Vector2(16 / Constants.PPM, -16 / Constants.PPM);  // Kanan Bawah
        vertices[2] = new Vector2(0, 16 / Constants.PPM);                    // Atas Tengah
        shape.set(vertices);

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.isSensor = true; // Sensor = Bisa ditembus (untuk deteksi tabrakan saja)
        body.createFixture(fdef).setUserData("SPIKE"); // Tag untuk deteksi kematian

        shape.dispose();
    }

    private void createGoal(float x, float y) {
        // Mirip block tapi sensor, buat nanti saja
    }

    private void createFloor() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(0, 0);
        bdef.type = BodyDef.BodyType.StaticBody;
        Body body = world.createBody(bdef);

        EdgeShape shape = new EdgeShape();
        // Lantai sepanjang 1000 meter
        shape.set(0, 0, 1000, 0);

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.friction = 0;
        body.createFixture(fdef).setUserData("FLOOR");

        shape.dispose();
    }
}
