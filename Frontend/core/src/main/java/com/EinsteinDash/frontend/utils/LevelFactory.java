package com.EinsteinDash.frontend.utils;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Pool;
import com.EinsteinDash.frontend.objects.Block;
import com.EinsteinDash.frontend.objects.Spike;

public class LevelFactory {

    private World world;
    private Texture floorTexture;

    // Buat pool Block
    private final Pool<Block> blockPool = new Pool<Block>() {
        @Override
        protected Block newObject() {
            return new Block(); // Buat baru jika habis
        }
    };

    // Buat pool Block
    private final Pool<Spike> spikePool = new Pool<Spike>() {
        @Override
        protected Spike newObject() {
            return new Spike(); // Buat baru jika habis
        }
    };

    // Daftar Object Aktif
    private final Array<Block> activeBlocks = new Array<>();
    private final Array<Spike> activeSpikes = new Array<>();

    public LevelFactory(World world) {
        this.world = world;
        this.floorTexture = new Texture("floor.png");
    }

    public void createLevel(String jsonLevelData) {
        // Bersihkan level lama (kembalikan ke pool)
        freeAll();

        // Buat Lantai
        createFloor();

        if (jsonLevelData == null || jsonLevelData.isEmpty()) return;

        JsonValue root = new JsonReader().parse(jsonLevelData);

        // Membaca JSON level
        for (JsonValue object : root) {
            String type = object.getString("type");
            float x = object.getFloat("x");
            float y = object.getFloat("y");

            // Mendeteksi dan generate Block
            if (type.equals("BLOCK")) {
                Block block = blockPool.obtain();
                block.init(world, x, y);
                activeBlocks.add(block);
            }
            // Mendeteksi dan generate Block
            else if (type.equals("SPIKE")) {
                Spike spike = spikePool.obtain();
                spike.init(world, x, y);
                activeSpikes.add(spike);
            }
        }
    }

    // Hanya menggambar objek yang ada di daftar aktif
    public void draw(SpriteBatch batch) {
        // Gambar Lantai
        batch.draw(floorTexture, -50, -1 / Constants.PPM, 1000, 1 / Constants.PPM);

        // Gambar Block
        for (Block block : activeBlocks) {
            block.draw(batch);
        }

        // Gambar Spike
        for (Spike spike : activeSpikes) {
            spike.draw(batch);
        }
    }

    // Mengembalikan semua objek ke pool
    public void freeAll() {
        spikePool.freeAll(activeSpikes);
        activeSpikes.clear();

        blockPool.freeAll(activeBlocks);
        activeBlocks.clear();
    }

    private void createFloor() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(0, 0);
        bdef.type = BodyDef.BodyType.StaticBody;
        Body body = world.createBody(bdef);

        EdgeShape shape = new EdgeShape();
        shape.set(-50, 0, 1000, 0);

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.friction = 0;

        body.createFixture(fdef).setUserData("FLOOR");
        body.setUserData("FLOOR");

        shape.dispose();
    }
}
