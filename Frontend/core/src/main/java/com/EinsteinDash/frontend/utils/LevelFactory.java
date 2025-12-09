package com.EinsteinDash.frontend.utils;

import com.EinsteinDash.frontend.objects.*;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Pool;

import java.util.Iterator;

public class LevelFactory {

    private World world;
    private float currentFloorY = 0f;
    private float currentCeilingY = 14f;
    private final float FLOOR_VISUAL_OFFSET = -0.77f;
    private Texture floorTexture;
    private float levelEndPosition = 100f;

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

    private final Pool<Goal> goalPool = new Pool<Goal>() {
        @Override
        protected Goal newObject() { return new Goal(); }
    };

    private final Pool<Coin> coinPool = new Pool<Coin>() {
        @Override
        protected Coin newObject() { return new Coin(); }
    };

    private final Pool<Portal> portalPool = new Pool<Portal>() {
        @Override
        protected Portal newObject() { return new Portal(); }
    };

    // Daftar Object Aktif
    private final Array<Block> activeBlocks = new Array<>();
    private final Array<Spike> activeSpikes = new Array<>();
    private final Array<Goal> activeGoals = new Array<>();
    private final Array<Coin> activeCoins = new Array<>();
    private final Array<Portal> activePortals = new Array<>();

    public LevelFactory(World world) {
        this.world = world;
        // load asset
        this.floorTexture = new Texture("floor.png");
        // looping
        this.floorTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
    }

    public void createLevel(String jsonLevelData) {
        // Bersihkan level lama (kembalikan ke pool)
        freeAll();

        // Buat Lantai
        createFloor(0f);
        createCeiling(2.15f);

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
            // Mendeteksi dan generate Goal
            else if (type.equals("GOAL")) {
                // Simpan posisi X sebagai akhir level
                this.levelEndPosition = (x * 32) / Constants.PPM;
                Goal goal = goalPool.obtain();
                goal.init(world, x, y);
                activeGoals.add(goal);
            }
            // Mendeteksi dan generate Coin
            else if (type.equals("COIN")) {
                Coin coin = coinPool.obtain();
                coin.init(world, x, y);
                activeCoins.add(coin);
            }
            // Kita terima tipe "PORTAL_SHIP" atau "PORTAL_CUBE"
            else if (type.startsWith("PORTAL")) {
                Portal portal = portalPool.obtain();
                // Pass tipe-nya (misal "PORTAL_SHIP")
                portal.init(world, x, y, type);
                activePortals.add(portal);
            }
        }
    }

    // Hanya menggambar objek yang ada di daftar aktif
    public void draw(SpriteBatch batch) {
        // Gambar Lantai
        float totalWidthMeters = 750f; // Panjang floor game
        float startX = -50f;    // Posisi X dari -50 di kiri

        int u2 = (int) ((totalWidthMeters * Constants.PPM) / 128);  // Hitung looping texture
        float height = 0.8f;    // Tinggi visual: 0.8 meter (sesuai gambar asli 80px)

        float drawFloorY = currentFloorY + FLOOR_VISUAL_OFFSET;

        batch.draw(floorTexture,
            startX, drawFloorY,
            totalWidthMeters, height,
            0, 0,       // Source X, Y (Mulai dari pojok gambar)
            u2, 1            // Repetisi (Ulang X sebanyak u2 kali, Y 1 kali)
        );

        // Gambar ceiling
        float drawCeilingY = currentCeilingY;
        batch.draw(floorTexture,
            startX, drawCeilingY,
            totalWidthMeters, height,
            0, 1, u2, 0 // Flip Vertikal (v=1, v2=0)
        );

        // Gambar Block
        for (Block block : activeBlocks) {
            block.draw(batch);
        }
        // Gambar Spike
        for (Spike spike : activeSpikes) {
            spike.draw(batch);
        }
        // Gambar Goal
        for (Goal goal : activeGoals) {
            goal.draw(batch);
        }
        // Gambar Coin
        for (Coin coin : activeCoins) {
            coin.draw(batch);
        }
        // Gambar Portal
        for (Portal portal : activePortals) {
            portal.draw(batch);
        }
    }

    public void removeCollectedCoins() {
        // Gunakan Iterator untuk loop aman sambil menghapus
        Iterator<Coin> iter = activeCoins.iterator();

        while (iter.hasNext()) {
            Coin coin = iter.next();

            // Jika koin sudah diambil (collect() sudah dipanggil di listener)
            if (coin.isCollected()) {
                // Kembalikan ke Pool
                coinPool.free(coin);
                // Hapus dari daftar aktif agar tidak dirender lagi
                iter.remove();
            }
        }
    }

    // Mengembalikan semua objek ke pool
    public void freeAll() {
        // Free Spike
        spikePool.freeAll(activeSpikes);
        activeSpikes.clear();
        // Free Block
        blockPool.freeAll(activeBlocks);
        activeBlocks.clear();
        // Free Goal
        goalPool.freeAll(activeGoals);
        activeGoals.clear();
        // Free Coin
        coinPool.freeAll(activeCoins);
        activeCoins.clear();
        // Free Portal
        portalPool.freeAll(activePortals);
        activePortals.clear();
    }

    private void createFloor(float yPosition) {
        this.currentFloorY = yPosition;

        BodyDef bdef = new BodyDef();
        bdef.position.set(0, yPosition);
        bdef.type = BodyDef.BodyType.StaticBody;
        Body body = world.createBody(bdef);

        EdgeShape shape = new EdgeShape();
        shape.set(-50, 0, 5000, 0); // panjang 5000m

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.friction = 0;

        body.createFixture(fdef).setUserData("FLOOR");
        body.setUserData("FLOOR");

        shape.dispose();
    }

    public void createCeiling(float yPosition) {
        this.currentCeilingY = yPosition;

        BodyDef bdef = new BodyDef();
        bdef.position.set(0, yPosition);
        bdef.type = BodyDef.BodyType.StaticBody;
        Body body = world.createBody(bdef);

        EdgeShape shape = new EdgeShape();
        shape.set(-50, 0, 5000, 0); // panjang 5000m

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.friction = 0;

        body.createFixture(fdef).setUserData("CEILING");

        shape.dispose();
    }

    public float getLevelEndPosition() {
        return levelEndPosition;
    }
}
