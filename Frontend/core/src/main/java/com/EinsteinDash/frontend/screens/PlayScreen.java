package com.EinsteinDash.frontend.screens;

import com.EinsteinDash.frontend.network.BackendFacade;
import com.EinsteinDash.frontend.scenes.Hud;
import com.EinsteinDash.frontend.screens.LevelCompletedWindow;
import com.badlogic.gdx.Gdx;
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
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.physics.box2d.Body;

public class PlayScreen extends ScreenAdapter implements GameObserver {

    private Main game;
    private LevelDto levelData;

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

    // Game State
    private boolean isDead = false;
    private boolean isLevelFinished = false;

    private Hud hud;
    private int currentRunCoins = 0;    // tracker koin

    public PlayScreen(Main game, LevelDto levelData) {
        this.game = game;
        this.levelData = levelData;

        // Inisialisasi Box2D
        Box2D.init();

        // Setup Camera
        gameCam = new OrthographicCamera();
        gamePort = new FitViewport(
            (Constants.V_WIDTH / Constants.PPM) / 2.5f,
            (Constants.V_HEIGHT / Constants.PPM) / 2.5f,
            gameCam
        );

        // Setup World Fisika
        world = new World(new Vector2(0, -10), true); // Gravitasi standar -10
        b2dr = new Box2DDebugRenderer();

        // Pasang Contact Listener (Observer Pattern untuk deteksi tabrakan)
        WorldContactListener contactListener = new WorldContactListener();
        contactListener.addObserver(this); // Daftarkan layar ini agar tahu kalau player mati
        world.setContactListener(contactListener);

        // Generate Level dari JSON
        levelFactory = new LevelFactory(world);
        if (this.levelData != null && this.levelData.getLevelData() != null) {
            levelFactory.createLevel(this.levelData.getLevelData());
        }

        // Spawn Player & Input
        player = new Player(world);
        inputHandler = new InputHandler();

        // Set posisi kamera awal agar pas saat spawn
        updateCameraPosition();

        hud = new Hud(game.batch);
        currentRunCoins = 0;    // reset coin
    }

    @Override
    public void show() {
        // Agar klik mouse saat main tidak memencet tombol "Back" yang tersembunyi
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void render(float delta) {
        update(delta);

        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Render Gambar
        game.batch.setProjectionMatrix(gameCam.combined);
        game.batch.begin();

        // Gambar Player
        player.draw(game.batch);

        // Gambar Level
        levelFactory.draw(game.batch);

        game.batch.end();

        // Update data HUD
        hud.update(player.b2body.getPosition().x, levelFactory.getLevelEndPosition());

        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();

        // b2dr.render(world, gameCam.combined);   // debug (outline hijau)
    }

    private void update(float dt) {
        // Jika mati, hentikan update logika (agar game freeze sebentar sebelum restart)
        if (isDead) {
            // Jika mati, jangan update fisika player, tapi jalankan timer
            deadTimer += dt;
            if (deadTimer > 0.5f) { // Tunggu 0,5 detik baru restart
                game.setScreen(new PlayScreen(game, levelData));
            }
            return;
        }

        if (isLevelFinished) return;    // jika game selesai, freeze

        // 1. Handle Input
        inputHandler.handleInput(player);

        // 2. Step World (Simulasi Fisika 60fps)
        world.step(1/60f, 6, 2);
        levelFactory.removeCollectedCoins();    // Menghapus koin

        // 3. Update Player
        player.update(dt);

        // 4. Update Kamera (Follow Player)
        updateCameraPosition();
        gameCam.update();
    }

    private void updateCameraPosition() {
        // --- LOGIKA POSISI X (Kiri-Kanan) ---
        // Agar player terlihat di KIRI layar (bukan tengah), kamera harus di DEPAN player.
        // Kita geser kamera ke kanan sejauh 1/4 lebar layar dari posisi player.
        float targetCamX = player.b2body.getPosition().x + (gamePort.getWorldWidth() / 4);

        // Batasi kamera agar tidak mundur melewati garis start (x=0)
        // Setengah lebar layar adalah batas minimal kamera
        float minCamX = gamePort.getWorldWidth() / 2;
        if (targetCamX < minCamX) {
            targetCamX = minCamX;
        }
        gameCam.position.x = targetCamX;

        // --- LOGIKA POSISI Y (Atas-Bawah) ---
        gameCam.position.y = gamePort.getWorldHeight() / 3;
    }

    // --- IMPLEMENTASI GAME OBSERVER ---

    @Override
    public void onPlayerDied() {
        if (!isDead) {
            isDead = true;
            Gdx.app.log("GAME", "PLAYER MATI! Restarting level...");
        }
    }

    @Override
    public void onLevelCompleted() {
        if (isLevelFinished) return; // Cegah panggil 2x
        isLevelFinished = true;

        Gdx.app.log("GAME", "LEVEL COMPLETE!");
        // Hitung Bintang
        // Jika level sebelumnya SUDAH tamat (isCompleted = true), bintang baru = 0.
        // Jika BELUM, bintang baru = Bintang Level.
        int starsEarned = 0;
        if (!levelData.isCompleted()) {
            starsEarned = levelData.getStars();
        }

        // Hitung Koin
        // Koin Baru = Koin sesi ini dikurangi koin terbaik Sebelumnya.
        int previousBestCoins = levelData.getCoinsCollected(); // Pastikan LevelDto punya getter ini
        int coinsEarned = Math.max(0, currentRunCoins - previousBestCoins);

        // Tampilkan Window
        LevelCompletedWindow win = new LevelCompletedWindow(
            game,
            game.assets.get("uiskin.json", Skin.class),
            levelData,
            starsEarned,
            coinsEarned,
            currentRunCoins
        );

        // Kirim data ke backend (Sync)
        int userId = Session.getInstance().getUserId();
        game.backend.syncProgress(userId, levelData.getId(), 100, 1, currentRunCoins, new BackendFacade.SyncCallback() {
            @Override
            public void onSuccess() {
                Gdx.app.log("SYNC", "Progress Saved Successfully!");
                levelData.setCompleted(true);
                if (currentRunCoins > levelData.getCoinsCollected()) {
                    levelData.setCoinsCollected(currentRunCoins);
                }
                // Saat kembali ke menu, data tidak hilang
                Session.getInstance().saveLocalProgress(levelData.getId(), levelData.getCoinsCollected());
            }

            @Override
            public void onFailed(String error) {
                Gdx.app.error("SYNC", "Failed to save progress: " + error);
            }
        });

        hud.stage.addActor(win); // Pasang ke layar
        Gdx.input.setInputProcessor(hud.stage); // Alihkan input ke HUD Stage
    }

    @Override
    public void resize(int width, int height) {
        gamePort.update(width, height);
        hud.stage.getViewport().update(width, height);
    }

    @Override
    public void onCoinCollected() {
        currentRunCoins++;
        System.out.println("Coin UI Updated!");
    }

    @Override
    public void dispose() {
        world.dispose();
        b2dr.dispose();
        player.dispose();
        hud.dispose();
    }
}
