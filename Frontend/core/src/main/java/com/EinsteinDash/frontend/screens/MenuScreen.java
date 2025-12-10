package com.EinsteinDash.frontend.screens;

import com.EinsteinDash.frontend.Main;
import com.EinsteinDash.frontend.utils.Constants;
import com.EinsteinDash.frontend.utils.GamePalette;
import com.EinsteinDash.frontend.utils.Session;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;

/**
 * MenuScreen - Halaman menu utama setelah login.
 * Menampilkan opsi Play, Leaderboard, Logout, dan Exit.
 */
public class MenuScreen extends ScreenAdapter {

    private final Main game;
    private Stage stage;
    private Skin skin;

    public MenuScreen(Main game) {
        this.game = game;
    }

    // ==================== LIFECYCLE ====================

    @Override
    public void show() {
        stage = new Stage(new FitViewport(Constants.V_WIDTH, Constants.V_HEIGHT));
        Gdx.input.setInputProcessor(stage);
        skin = game.assets.get("uiskin.json", Skin.class);

        setupUI();
    }

    /** Setup UI layout */
    private void setupUI() {
        Table table = new Table();
        table.setFillParent(true);

        Session session = Session.getInstance();

        // Labels
        Label titleLabel = new Label("MAIN MENU", skin);
        titleLabel.setFontScale(2);

        Label userLabel = new Label("Welcome, " + session.getUsername() + "!", skin);

        Label starsLabel = new Label("Total Stars: " + session.getTotalStars(), skin);
        starsLabel.setColor(1, 0.8f, 0, 1); // Yellow

        Label coinsLabel = new Label("Total Coins: " + session.getTotalCoins(), skin);
        coinsLabel.setColor(GamePalette.Bright.GOLD);

        // Buttons
        TextButton playButton = new TextButton("PLAY LEVELS", skin);
        TextButton leaderboardButton = new TextButton("LEADERBOARD", skin);
        TextButton logoutButton = new TextButton("LOGOUT", skin);
        TextButton exitButton = new TextButton("EXIT", skin);

        playButton.setColor(GamePalette.Neon.LIME);
        leaderboardButton.setColor(GamePalette.Neon.YELLOW);
        logoutButton.setColor(GamePalette.Bright.SKY);
        exitButton.setColor(GamePalette.Neon.RED);

        // Layout
        table.add(titleLabel).colspan(2).padBottom(40).row();
        table.add(userLabel).colspan(2).padBottom(10).row();
        table.add(starsLabel).colspan(2).padBottom(10).row(); // Reduced padding
        table.add(coinsLabel).colspan(2).padBottom(30).row(); // Added coins row
        table.add(playButton).width(200).height(50).padBottom(20).padRight(10);
        table.add(leaderboardButton).width(200).height(50).padBottom(20).padLeft(10).row();
        table.add(logoutButton).width(200).height(50).padBottom(20).padRight(10);
        table.add(exitButton).width(200).height(50).padBottom(20).padLeft(10).row();

        stage.addActor(table);

        // Button listeners
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new LevelSelectScreen(game));
            }
        });

        leaderboardButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new LeaderboardScreen(game));
            }
        });

        logoutButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new LoginScreen(game));
            }
        });

        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
    }

    // ==================== RENDER & DISPOSE ====================

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.3f, 1); // Dark blue background
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
