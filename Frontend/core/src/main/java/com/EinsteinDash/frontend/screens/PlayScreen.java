package com.EinsteinDash.frontend.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.EinsteinDash.frontend.Main;
import com.EinsteinDash.frontend.model.LevelDto;
import com.EinsteinDash.frontend.utils.*;
import com.EinsteinDash.frontend.input.InputHandler;

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

    public PlayScreen(Main game, LevelDto levelData) {
        this.game = game;
        this.levelData = levelData;

        // 1. Inisialisasi Box2D
        Box2D.init();

        // 2. Setup Camera (ZOOM IN 2.5x)
        gameCam = new OrthographicCamera();
        gamePort = new FitViewport(
            (Constants.V_WIDTH / Constants.PPM) / 2.5f,
            (Constants.V_HEIGHT / Constants.PPM) / 2.5f,
            gameCam
        );

        // 3. Setup World Fisika
        world = new World(new Vector2(0, -10), true); // Gravitasi standar -10
        b2dr = new Box2DDebugRenderer();

        // 4. Pasang Contact Listener (Observer Pattern untuk Deteksi Tabrakan)
        WorldContactListener contactListener = new WorldContactListener();
        contactListener.addObserver(this); // Daftarkan layar ini agar tahu kalau player mati
        world.setContactListener(contactListener);

        // 5. Generate Level dari JSON
        levelFactory = new LevelFactory(world);
        if (this.levelData != null && this.levelData.getLevelData() != null) {
            levelFactory.createLevel(this.levelData.getLevelData());
        }

        // 6. Spawn Player & Input
        player = new Player(world);
        inputHandler = new InputHandler();

        // Set posisi kamera awal agar pas saat spawn
        updateCameraPosition();
    }

    @Override
    public void show() {
        // Agar klik mouse saat main tidak memencet tombol "Back" yang tersembunyi
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void render(float delta) {
        update(delta);

        // Clear Screen (Warna Latar Belakang)
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1); // Sedikit biru gelap
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Render Garis Hijau Fisika (Debug View)
        b2dr.render(world, gameCam.combined);
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

        // 1. Handle Input
        inputHandler.handleInput(player);

        // 2. Step World (Simulasi Fisika 60fps)
        world.step(1/60f, 6, 2);

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
        Gdx.app.log("GAME", "LEVEL COMPLETE!");
    }

    @Override
    public void resize(int width, int height) {
        gamePort.update(width, height);
    }

    @Override
    public void dispose() {
        world.dispose();
        b2dr.dispose();
        player.dispose();
    }
}
