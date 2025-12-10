package com.EinsteinDash.frontend.screens;

import com.EinsteinDash.frontend.Main;
import com.EinsteinDash.frontend.input.InputHandler;
import com.EinsteinDash.frontend.model.LevelDto;
import com.EinsteinDash.frontend.network.BackendFacade;
import com.EinsteinDash.frontend.scenes.Hud;
import com.EinsteinDash.frontend.utils.Constants;
import com.EinsteinDash.frontend.utils.GameObserver;
import com.EinsteinDash.frontend.utils.LevelFactory;
import com.EinsteinDash.frontend.utils.Player;
import com.EinsteinDash.frontend.utils.Session;
import com.EinsteinDash.frontend.utils.WorldContactListener;
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

/**
 * PlayScreen - Layar gameplay utama.
 * Mengelola physics, rendering, dan game logic.
 */
public class PlayScreen extends ScreenAdapter implements GameObserver {

    // === REFERENCES ===
    private Main game;
    private LevelDto levelData;

    // === GAME STATE ===
    public enum State {
        RUNNING, PAUSED
    }

    private State currentState = State.RUNNING;
    private boolean isDead = false;
    private boolean isLevelFinished = false;
    private float deadTimer = 0;
    private int currentRunCoins = 0;

    // === CAMERA & VIEWPORT ===
    private OrthographicCamera gameCam;
    private Viewport gamePort;

    // === BOX2D PHYSICS ===
    private World world;
    private Box2DDebugRenderer b2dr;

    // === GAME OBJECTS ===
    private LevelFactory levelFactory;
    private Player player;
    private InputHandler inputHandler;
    private Hud hud;

    // === FIXED TIMESTEP (untuk physics stabil) ===
    private float accumulator = 0;
    private static final float TIME_STEP = 1 / 60f; // 60 FPS physics

    // ==================== CONSTRUCTOR ====================

    public PlayScreen(Main game, LevelDto levelData) {
        this.game = game;
        this.levelData = levelData;

        Box2D.init();

        // Setup camera dengan zoom yang sesuai
        gameCam = new OrthographicCamera();
        gamePort = new FitViewport(
                (Constants.V_WIDTH / Constants.PPM) / 2.5f,
                (Constants.V_HEIGHT / Constants.PPM) / 2.5f,
                gameCam);

        // Buat world dengan gravitasi tinggi (gameplay cepat)
        world = new World(new Vector2(0, -26f), true);
        b2dr = new Box2DDebugRenderer();

        // Setup collision listener
        WorldContactListener contactListener = new WorldContactListener();
        contactListener.addObserver(this);
        world.setContactListener(contactListener);

        // Generate level dari JSON
        levelFactory = new LevelFactory(world);
        if (this.levelData != null && this.levelData.getLevelData() != null) {
            levelFactory.createLevel(this.levelData.getLevelData());
        }

        // Setup player dan input
        player = new Player(world);
        inputHandler = new InputHandler();

        // Posisi awal kamera
        float startX = player.b2body.getPosition().x + (gamePort.getWorldWidth() / 4);
        gameCam.position.set(startX, gamePort.getWorldHeight() / 3, 0);
        gameCam.update();

        // Setup HUD
        hud = new Hud(game.batch);
        currentRunCoins = 0;
    }

    // ==================== LIFECYCLE ====================

    @Override
    public void show() {
        Gdx.input.setInputProcessor(null); // Input dihandle manual

        // Play music dari awal setiap kali level dimulai/restart/player mati
        if (levelData != null) {
            game.getAudioManager().playFromStart(levelData.getAudioTrackId());
        }
    }

    // ==================== RENDER ====================

    @Override
    public void render(float delta) {
        update(delta);

        // Clear screen
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Render game objects
        game.batch.setProjectionMatrix(gameCam.combined);
        game.batch.begin();
        player.draw(game.batch);
        levelFactory.draw(game.batch);
        game.batch.end();

        // Render HUD
        hud.update(player.getInterpolatedPosition().x, levelFactory.getLevelEndPosition());
        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();

        // Debug: render Box2D hitboxes (uncomment untuk debug)
        b2dr.render(world, gameCam.combined);
    }

    // ==================== UPDATE ====================

    private void update(float dt) {
        // Handle death delay
        if (isDead) {
            deadTimer += dt;
            if (deadTimer > 0.5f) {
                game.setScreen(new PlayScreen(game, levelData)); // Restart
            }
            return;
        }

        if (isLevelFinished)
            return;

        // Toggle pause dengan ESC
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if (currentState == State.RUNNING) {
                pauseGame();
            } else {
                resumeGame();
            }
        }

        // Skip update jika paused
        if (currentState == State.PAUSED) {
            hud.stage.act(dt); // Update UI animations saja
            return;
        }

        // Handle input
        inputHandler.handleInput(player);

        // Fixed timestep physics (mencegah "spiral of death")
        float frameTime = Math.min(dt, 5 * TIME_STEP);
        accumulator += frameTime;

        while (accumulator >= TIME_STEP) {
            player.capturePreviousPosition();
            world.step(TIME_STEP, 6, 2);
            accumulator -= TIME_STEP;
            levelFactory.removeCollectedCoins();
        }

        // Interpolasi untuk smooth rendering
        float alpha = accumulator / TIME_STEP;
        player.update(dt);
        player.updateVisual(alpha);

        // Update camera
        updateCameraPositionSmooth();
    }

    /** Kamera mengikuti player dengan efek smooth */
    private void updateCameraPositionSmooth() {
        Vector2 targetPos = player.getInterpolatedPosition();
        float targetX = targetPos.x + (gamePort.getWorldWidth() / 4);
        float minX = gamePort.getWorldWidth() / 2;
        if (targetX < minX)
            targetX = minX;

        // Lerp untuk gerakan kamera yang halus
        // FIX: Gunakan lerp 1.0f (instant) untuk X agar tidak ada lag saat speed tinggi
        // sehingga posisi player tetap konsisten di layar.
        float lerpFactor = 1.0f;
        gameCam.position.x += (targetX - gameCam.position.x) * lerpFactor;
        gameCam.position.y = gamePort.getWorldHeight() / 3;
        gameCam.update();
    }

    // ==================== OBSERVER CALLBACKS ====================

    @Override
    public void onPlayerDied() {
        if (!isDead) {
            isDead = true;

            // Sync progress ke server (simpan persentase terakhir)
            int userId = Session.getInstance().getUserId();
            int percentage = hud.getPercentage();

            game.backend.syncProgress(userId, levelData.getId(), percentage, 1, 0,
                    new BackendFacade.SyncCallback() {
                        @Override
                        public void onSuccess(int s, boolean c) {
                        }

                        @Override
                        public void onFailed(String e) {
                        }
                    });

            Gdx.app.log("GAME", "PLAYER MATI!");
        }
    }

    @Override
    public void onLevelCompleted() {
        if (isLevelFinished)
            return;
        isLevelFinished = true;

        // Stop music saat level complete
        game.getAudioManager().stop();

        int userId = Session.getInstance().getUserId();
        boolean wasCompletedBefore = levelData.isCompleted();
        int coinsBefore = levelData.getCoinsCollected();

        // LOGIC KHUSUS GUEST MODE (OFFLINE)
        if (Session.getInstance().isGuest()) {
            boolean isNewComplete = !wasCompletedBefore;
            // Jika Guest, simpan progress ke memory Session
            Session.getInstance().saveLocalProgress(levelData.getId(), currentRunCoins);

            int starsEarned = (isNewComplete) ? levelData.getStars() : 0;
            int coinsEarned = Math.max(0, currentRunCoins - coinsBefore);

            // Tambahkan ke total Guest
            Session.getInstance().addStars(starsEarned);
            Session.getInstance().addCoins(coinsEarned);

            // Update data level objek ini
            levelData.setCompleted(true);
            levelData.setCoinsCollected(Math.max(coinsBefore, currentRunCoins));

            showCompletedWindow(starsEarned, coinsEarned);
            return;
        }

        // LOGIC STANDARD (ONLINE)
        // Sync 100% ke server dan tampilkan popup
        game.backend.syncProgress(userId, levelData.getId(), 100, 1, currentRunCoins,
                new BackendFacade.SyncCallback() {
                    @Override
                    public void onSuccess(int serverCoins, boolean serverCompleted) {
                        // Hitung reward
                        int starsEarned = (!wasCompletedBefore && serverCompleted) ? levelData.getStars() : 0;
                        int coinsEarned = Math.max(0, serverCoins - coinsBefore);

                        // Update local data
                        levelData.setCompleted(serverCompleted);
                        levelData.setCoinsCollected(serverCoins);

                        // Tampilkan popup
                        showCompletedWindow(starsEarned, coinsEarned);
                    }

                    @Override
                    public void onFailed(String error) {
                        showCompletedWindow(-1, -1); // Error state
                    }
                });
    }

    @Override
    public void onCoinCollected() {
        currentRunCoins++;
    }

    /** Tampilkan popup level completed */
    private void showCompletedWindow(int starsEarned, int coinsEarned) {
        Skin skin = game.assets.get("uiskin.json", Skin.class);
        LevelCompletedWindow win = new LevelCompletedWindow(
                game, skin, levelData, starsEarned, coinsEarned, currentRunCoins);
        hud.stage.addActor(win);
        Gdx.input.setInputProcessor(hud.stage);
    }

    // ==================== PAUSE/RESUME ====================

    // --- FIX LOGIC PAUSE ---
    public void pauseGame() {
        currentState = State.PAUSED;

        // Pause music saat game di-pause
        game.getAudioManager().pause();

        Skin skin = game.assets.get("uiskin.json", Skin.class);
        PauseWindow pauseWindow = new PauseWindow(game, this, skin);

        // GUNAKAN METHOD BARU DI HUD UNTUK MENYIMPAN REFERENSI
        hud.showPauseWindow(pauseWindow);

        Gdx.input.setInputProcessor(hud.stage);
    }

    public void resumeGame() {
        currentState = State.RUNNING;

        // Resume music
        game.getAudioManager().resume();

        // PANGGIL INI UNTUK MENGHAPUS WINDOW DARI LAYAR
        hud.hidePauseWindow();

        Gdx.input.setInputProcessor(null);
    }

    public void restartLevel() {
        game.setScreen(new PlayScreen(game, levelData));
    }

    // ==================== RESIZE & DISPOSE ====================

    @Override
    public void resize(int width, int height) {
        gamePort.update(width, height);
        hud.stage.getViewport().update(width, height);
    }

    @Override
    public void dispose() {
        // Stop music saat keluar dari level
        game.getAudioManager().stop();

        world.dispose();
        b2dr.dispose();
        player.dispose();
        hud.dispose();
    }
}
