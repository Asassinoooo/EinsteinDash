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

    // Fisika Timer
    private float accumulator = 0;
    private static final float TIME_STEP = 1 / 60f;

    public PlayScreen(Main game, LevelDto levelData) {
        this.game = game;
        this.levelData = levelData;

        Box2D.init();

        gameCam = new OrthographicCamera();
        gamePort = new FitViewport(
            (Constants.V_WIDTH / Constants.PPM) / 2.5f,
            (Constants.V_HEIGHT / Constants.PPM) / 2.5f,
            gameCam
        );

        world = new World(new Vector2(0, -26f), true);
        b2dr = new Box2DDebugRenderer();

        WorldContactListener contactListener = new WorldContactListener();
        contactListener.addObserver(this);
        world.setContactListener(contactListener);

        levelFactory = new LevelFactory(world);
        if (this.levelData != null && this.levelData.getLevelData() != null) {
            levelFactory.createLevel(this.levelData.getLevelData());
        }

        player = new Player(world);
        inputHandler = new InputHandler();

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
        update(delta);

        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.setProjectionMatrix(gameCam.combined);
        game.batch.begin();
        player.draw(game.batch);
        levelFactory.draw(game.batch);
        game.batch.end();

        hud.update(player.getInterpolatedPosition().x, levelFactory.getLevelEndPosition());
        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();

        // b2dr.render(world, gameCam.combined); // Uncomment untuk debug box2d
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

        // --- GLOBAL INPUT: ESC TO PAUSE/RESUME ---
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if (currentState == State.RUNNING) {
                pauseGame();
            } else {
                resumeGame();
            }
        }

        // --- STATE CHECK ---
        if (currentState == State.PAUSED) {
            hud.stage.act(dt); // UI tetap animasi (tombol tekan, slider geser)
            return; // STOP UPDATE FISIKA GAME
        }

        inputHandler.handleInput(player);

        float frameTime = Math.min(dt, 5 * TIME_STEP);
        accumulator += frameTime;

        while (accumulator >= TIME_STEP) {
            player.capturePreviousPosition();
            world.step(TIME_STEP, 6, 2);
            accumulator -= TIME_STEP;
            levelFactory.removeCollectedCoins();
        }

        float alpha = accumulator / TIME_STEP;
        player.update(dt);
        player.updateVisual(alpha);

        updateCameraPositionSmooth();
    }

    private void updateCameraPositionSmooth() {
        Vector2 targetPos = player.getInterpolatedPosition();
        float targetX = targetPos.x + (gamePort.getWorldWidth() / 4);
        float minX = gamePort.getWorldWidth() / 2;
        if (targetX < minX) targetX = minX;

        float lerpFactor = 0.1f;
        gameCam.position.x += (targetX - gameCam.position.x) * lerpFactor;
        gameCam.position.y = gamePort.getWorldHeight() / 3;
        gameCam.update();
    }

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

    // --- FIX LOGIC PAUSE ---
    public void pauseGame() {
        currentState = State.PAUSED;

        Skin skin = game.assets.get("uiskin.json", Skin.class);
        PauseWindow pauseWindow = new PauseWindow(game, this, skin);

        // GUNAKAN METHOD BARU DI HUD UNTUK MENYIMPAN REFERENSI
        hud.showPauseWindow(pauseWindow);

        Gdx.input.setInputProcessor(hud.stage);
    }

    public void resumeGame() {
        currentState = State.RUNNING;

        // PANGGIL INI UNTUK MENGHAPUS WINDOW DARI LAYAR
        hud.hidePauseWindow();

        Gdx.input.setInputProcessor(null);
    }

    public void restartLevel() {
        game.setScreen(new PlayScreen(game, levelData));
    }

    @Override public void dispose() { world.dispose(); b2dr.dispose(); player.dispose(); hud.dispose(); }
}
