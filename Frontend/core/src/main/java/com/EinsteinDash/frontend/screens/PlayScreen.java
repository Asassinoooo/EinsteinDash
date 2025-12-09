package com.EinsteinDash.frontend.screens;

import com.EinsteinDash.frontend.network.BackendFacade;
import com.EinsteinDash.frontend.scenes.Hud;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.EinsteinDash.frontend.Main;
import com.EinsteinDash.frontend.model.LevelDto;
import com.EinsteinDash.frontend.utils.*;
import com.EinsteinDash.frontend.input.InputHandler;

public class PlayScreen extends ScreenAdapter implements GameObserver {

    private Main game;
    private LevelDto levelData;
    public enum State { RUNNING, PAUSED }
    private State currentState = State.RUNNING;

    // Camera & Viewport
    private OrthographicCamera gameCam;
    private Viewport gamePort;

    // Box2D Variables
    private World world;
    private Box2DDebugRenderer b2dr;

    // Game Objects
    private LevelFactory levelFactory;
    private Player player;
    private InputHandler inputHandler;

    private float deadTimer = 0;
    private boolean isDead = false;
    private boolean isLevelFinished = false;

    private Hud hud;
    private int currentRunCoins = 0;

    // --- OPTIMASI FISIKA (Fixed Time Step) ---
    private float accumulator = 0;
    private static final float TIME_STEP = 1 / 60f; // Target Fisika: 60 update per detik

    public PlayScreen(Main game, LevelDto levelData) {
        this.game = game;
        this.levelData = levelData;

        Box2D.init();

        // Setup Camera
        gameCam = new OrthographicCamera();
        gamePort = new FitViewport(
            (Constants.V_WIDTH / Constants.PPM) / 2.5f,
            (Constants.V_HEIGHT / Constants.PPM) / 2.5f,
            gameCam
        );

        // GRAVITASI: -26f (Sesuai request)
        world = new World(new Vector2(0, -26f), true);

        b2dr = new Box2DDebugRenderer();

        WorldContactListener contactListener = new WorldContactListener();
        contactListener.addObserver(this);
        world.setContactListener(contactListener);

        levelFactory = new LevelFactory(world);
        if (this.levelData != null && this.levelData.getLevelData() != null) {
            levelFactory.createLevel(this.levelData.getLevelData());
            // levelFactory.createCeiling(0, 1000); // Pastikan buat method ini di LevelFactory untuk Ship
        }

        player = new Player(world);
        inputHandler = new InputHandler();

        // Setup awal kamera
        float startX = player.b2body.getPosition().x + (gamePort.getWorldWidth() / 4);
        gameCam.position.set(startX, gamePort.getWorldHeight() / 3, 0);
        gameCam.update();

        hud = new Hud(game.batch);
        currentRunCoins = 0;
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void render(float delta) {
        // Pisahkan logika update dan draw
        update(delta);

        // Clear Screen
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.setProjectionMatrix(gameCam.combined);
        game.batch.begin();

        // Gambar Player (posisi visual hasil interpolasi)
        player.draw(game.batch);

        levelFactory.draw(game.batch);
        game.batch.end();

        // HUD
        hud.update(player.getInterpolatedPosition().x, levelFactory.getLevelEndPosition());
        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();

        // Debug Renderer (Uncomment jika ingin lihat kotak hijau)
        b2dr.render(world, gameCam.combined);
    }

    private void update(float dt) {
        if (isDead) {
            deadTimer += dt;
            if (deadTimer > 0.5f) {
                game.setScreen(new PlayScreen(game, levelData));
            }
            return;
        }

        if (isLevelFinished) return;

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if (currentState == State.RUNNING) {
                pauseGame();
            } else {
                resumeGame();
            }
        }

        // --- JIKA PAUSED, STOP UPDATE ---
        if (currentState == State.PAUSED) {
            // Kita tetap update Stage HUD agar animasi tombol ditekan tetap jalan
            hud.stage.act(dt);
            return; // <-- PENTING: Stop di sini, jangan jalankan fisika di bawah
        }

        inputHandler.handleInput(player);

        // --- FIX HITBOX BERGESER (SPIRAL OF DEATH) ---
        // Batasi dt maksimal. Jika debugging (dt jadi besar), kita potong jadi maksimal 5 frame.
        // Ini mencegah loop while di bawah berjalan ratusan kali dan membuat hitbox kabur.
        float frameTime = Math.min(dt, 5 * TIME_STEP);
        accumulator += frameTime;

        while (accumulator >= TIME_STEP) {
            // 1. Simpan posisi fisik lama
            player.capturePreviousPosition();

            // 2. Step Fisika
            world.step(TIME_STEP, 6, 2);
            accumulator -= TIME_STEP;

            // Logika game lain yang butuh sinkronisasi fisika
            levelFactory.removeCollectedCoins();
        }

        // 3. Hitung Alpha untuk Interpolasi (Sisa waktu)
        float alpha = accumulator / TIME_STEP;

        // 4. Update Visual Player (Interpolasi & Animasi Rotasi)
        player.update(dt);
        player.updateVisual(alpha);

        // 5. Kamera mengikuti posisi visual player
        updateCameraPositionSmooth();
    }

    private void updateCameraPositionSmooth() {
        Vector2 targetPos = player.getInterpolatedPosition();

        float targetX = targetPos.x + (gamePort.getWorldWidth() / 4);
        float minX = gamePort.getWorldWidth() / 2;
        if (targetX < minX) targetX = minX;

        // Lerp 0.1f agar pergerakan kamera halus di 60/144hz
        float lerpFactor = 0.1f;
        gameCam.position.x += (targetX - gameCam.position.x) * lerpFactor;
        gameCam.position.y = gamePort.getWorldHeight() / 3;

        gameCam.update();
    }

    // --- OBSERVER METHODS (Tidak Berubah) ---
    @Override
    public void onPlayerDied() {
        if (!isDead) {
            isDead = true;
            int userId = Session.getInstance().getUserId();
            int percentage = hud.getPercentage();
            game.backend.syncProgress(userId, levelData.getId(), percentage, 1, 0, new BackendFacade.SyncCallback() {
                @Override public void onSuccess(int s, boolean c) {}
                @Override public void onFailed(String e) {}
            });
            Gdx.app.log("GAME", "PLAYER MATI!");
        }
    }

    @Override
    public void onLevelCompleted() {
        if (isLevelFinished) return;
        isLevelFinished = true;
        int userId = Session.getInstance().getUserId();
        boolean wasCompletedBefore = levelData.isCompleted();
        int coinsBefore = levelData.getCoinsCollected();

        game.backend.syncProgress(userId, levelData.getId(), 100, 1, currentRunCoins, new BackendFacade.SyncCallback() {
            @Override
            public void onSuccess(int serverCoins, boolean serverCompleted) {
                int starsEarned = (!wasCompletedBefore && serverCompleted) ? levelData.getStars() : 0;
                int coinsEarned = Math.max(0, serverCoins - coinsBefore);
                levelData.setCompleted(serverCompleted);
                levelData.setCoinsCollected(serverCoins);
                LevelCompletedWindow win = new LevelCompletedWindow(game, game.assets.get("uiskin.json", Skin.class), levelData, starsEarned, coinsEarned, currentRunCoins);
                hud.stage.addActor(win);
                Gdx.input.setInputProcessor(hud.stage);
            }
            @Override
            public void onFailed(String error) {
                LevelCompletedWindow win = new LevelCompletedWindow(game, game.assets.get("uiskin.json", Skin.class), levelData, -1, -1, currentRunCoins);
                hud.stage.addActor(win);
                Gdx.input.setInputProcessor(hud.stage);
            }
        });
    }

    @Override
    public void resize(int width, int height) {
        gamePort.update(width, height);
        hud.stage.getViewport().update(width, height);
    }
    @Override public void onCoinCollected() { currentRunCoins++; }

    public void pauseGame() {
        currentState = State.PAUSED;

        // Munculkan Window Pause
        Skin skin = game.assets.get("uiskin.json", Skin.class);

        // Buat window baru
        PauseWindow pauseWindow = new PauseWindow(game, this, skin);

        // Masukkan window ke Stage milik HUD agar terlihat
        hud.stage.addActor(pauseWindow);

        // Alihkan Input Processor ke Stage agar tombol di Window bisa diklik
        Gdx.input.setInputProcessor(hud.stage);
    }

    public void resumeGame() {
        currentState = State.RUNNING;

        // Kembalikan Input Processor ke null (atau InputHandler game Anda)
        // Agar player bisa loncat lagi dengan tombol Spasi/Mouse
        Gdx.input.setInputProcessor(null);
    }

    public void restartLevel() {
        // Reload screen ini dengan data level yang sama
        game.setScreen(new PlayScreen(game, levelData));
    }

    @Override public void dispose() { world.dispose(); b2dr.dispose(); player.dispose(); hud.dispose(); }
}
