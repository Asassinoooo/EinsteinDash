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

/**
 * LevelFactory - Membuat dan mengelola objek level dari JSON.
 * Pattern: Factory + Object Pool untuk efisiensi memori.
 */
public class LevelFactory {

    private World world;
    private Texture floorTexture;
    private float levelEndPosition = 100f;

    // === FLOOR/CEILING CONFIG ===
    private float currentFloorY = 0f;
    private float currentCeilingY = 14f;
    private static final float FLOOR_VISUAL_OFFSET = -0.77f;

    // ==================== OBJECT POOLS ====================

    private final Pool<Block> blockPool = new Pool<Block>() {
        @Override
        protected Block newObject() { return new Block(); }
    };

    private final Pool<Spike> spikePool = new Pool<Spike>() {
        @Override
        protected Spike newObject() { return new Spike(); }
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

    // === ACTIVE OBJECTS ===
    private final Array<Block> activeBlocks = new Array<>();
    private final Array<Spike> activeSpikes = new Array<>();
    private final Array<Goal> activeGoals = new Array<>();
    private final Array<Coin> activeCoins = new Array<>();
    private final Array<Portal> activePortals = new Array<>();

    // ==================== CONSTRUCTOR ====================

    public LevelFactory(World world) {
        this.world = world;
        this.floorTexture = new Texture("floor.png");
        this.floorTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
    }

    // ==================== LEVEL CREATION ====================

    /** Parse JSON dan buat semua objek level */
    public void createLevel(String jsonLevelData) {
        freeAll();  // Bersihkan level lama
        createFloor(0f);
        createCeiling(2.15f);

        if (jsonLevelData == null || jsonLevelData.isEmpty()) return;

        JsonValue root = new JsonReader().parse(jsonLevelData);

        for (JsonValue object : root) {
            String type = object.getString("type");
            float x = object.getFloat("x");
            float y = object.getFloat("y");

            spawnObject(type, x, y);
        }
    }

    /** Spawn objek berdasarkan tipe */
    private void spawnObject(String type, float x, float y) {
        switch (type) {
            case "BLOCK":
                Block block = blockPool.obtain();
                block.init(world, x, y);
                activeBlocks.add(block);
                break;

            case "SPIKE":
                Spike spike = spikePool.obtain();
                spike.init(world, x, y);
                activeSpikes.add(spike);
                break;

            case "GOAL":
                this.levelEndPosition = (x * 32) / Constants.PPM;
                Goal goal = goalPool.obtain();
                goal.init(world, x, y);
                activeGoals.add(goal);
                break;

            case "COIN":
                Coin coin = coinPool.obtain();
                coin.init(world, x, y);
                activeCoins.add(coin);
                break;

            default:
                // Portal types: PORTAL_SHIP, PORTAL_CUBE, etc.
                if (type.startsWith("PORTAL")) {
                    Portal portal = portalPool.obtain();
                    portal.init(world, x, y, type);
                    activePortals.add(portal);
                }
                break;
        }
    }

    // ==================== FLOOR & CEILING ====================

    /** Buat lantai fisik (Edge shape) */
    private void createFloor(float yPosition) {
        this.currentFloorY = yPosition;

        BodyDef bdef = new BodyDef();
        bdef.position.set(0, yPosition);
        bdef.type = BodyDef.BodyType.StaticBody;
        Body body = world.createBody(bdef);

        EdgeShape shape = new EdgeShape();
        shape.set(-50, 0, 5000, 0);

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.friction = 0;

        body.createFixture(fdef).setUserData("FLOOR");
        body.setUserData("FLOOR");

        shape.dispose();
    }

    /** Buat langit-langit fisik */
    private void createCeiling(float yPosition) {
        this.currentCeilingY = yPosition;

        BodyDef bdef = new BodyDef();
        bdef.position.set(0, yPosition);
        bdef.type = BodyDef.BodyType.StaticBody;
        Body body = world.createBody(bdef);

        EdgeShape shape = new EdgeShape();
        shape.set(-50, 0, 5000, 0);

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.friction = 0;

        body.createFixture(fdef).setUserData("CEILING");

        shape.dispose();
    }

    // ==================== RENDERING ====================

    /** Gambar semua objek aktif */
    public void draw(SpriteBatch batch) {
        drawFloorAndCeiling(batch);

        for (Block block : activeBlocks) block.draw(batch);
        for (Spike spike : activeSpikes) spike.draw(batch);
        for (Goal goal : activeGoals) goal.draw(batch);
        for (Coin coin : activeCoins) coin.draw(batch);
        for (Portal portal : activePortals) portal.draw(batch);
    }

    /** Gambar floor dan ceiling dengan texture looping */
    private void drawFloorAndCeiling(SpriteBatch batch) {
        float totalWidth = 750f;
        float startX = -50f;
        float height = 0.8f;
        int repeatX = (int) ((totalWidth * Constants.PPM) / 128);

        // Floor
        batch.draw(floorTexture,
            startX, currentFloorY + FLOOR_VISUAL_OFFSET,
            totalWidth, height,
            0, 0, repeatX, 1
        );

        // Ceiling (flipped)
        batch.draw(floorTexture,
            startX, currentCeilingY,
            totalWidth, height,
            0, 1, repeatX, 0
        );
    }

    // ==================== CLEANUP ====================

    /** Hapus koin yang sudah dikumpulkan */
    public void removeCollectedCoins() {
        Iterator<Coin> iter = activeCoins.iterator();
        while (iter.hasNext()) {
            Coin coin = iter.next();
            if (coin.isCollected()) {
                coinPool.free(coin);
                iter.remove();
            }
        }
    }

    /** Kembalikan semua objek ke pool */
    public void freeAll() {
        spikePool.freeAll(activeSpikes);
        activeSpikes.clear();

        blockPool.freeAll(activeBlocks);
        activeBlocks.clear();

        goalPool.freeAll(activeGoals);
        activeGoals.clear();

        coinPool.freeAll(activeCoins);
        activeCoins.clear();

        portalPool.freeAll(activePortals);
        activePortals.clear();
    }

    // ==================== GETTER ====================

    public float getLevelEndPosition() {
        return levelEndPosition;
    }
}
