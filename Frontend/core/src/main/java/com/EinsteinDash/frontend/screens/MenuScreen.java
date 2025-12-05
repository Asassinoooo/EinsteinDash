package com.EinsteinDash.frontend.screens;

import com.EinsteinDash.frontend.utils.GamePalette;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.EinsteinDash.frontend.Main;
import com.EinsteinDash.frontend.utils.Constants;
import com.EinsteinDash.frontend.utils.Session;

public class MenuScreen extends ScreenAdapter {

    private final Main game;
    private Stage stage;
    private Skin skin;

    public MenuScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(new FitViewport(Constants.V_WIDTH, Constants.V_HEIGHT));
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("uiskin.json"));

        Table table = new Table();
        table.setFillParent(true);

        // Ambil data user dari Singleton Session
        Session session = Session.getInstance();
        String welcomeText = "Welcome, " + session.getUsername() + "!";
        String starsText = "Total Stars: " + session.getTotalStars();

        // Buat UI Components
        Label titleLabel = new Label("MAIN MENU", skin);
        titleLabel.setFontScale(2); // Perbesar judul

        Label userLabel = new Label(welcomeText, skin);
        Label starsLabel = new Label(starsText, skin);

        // Warna kuning untuk bintang biar menarik
        starsLabel.setColor(1, 0.8f, 0, 1);

        TextButton playButton = new TextButton("PLAY LEVELS", skin);
        TextButton leaderboardButton = new TextButton("LEADERBOARD", skin);
        TextButton logoutButton = new TextButton("LOGOUT", skin);
        TextButton exitButton = new TextButton("EXIT", skin);

        // Layout
        table.add(titleLabel).colspan(2).padBottom(40).row();
        table.add(userLabel).colspan(2).padBottom(10).row();
        table.add(starsLabel).colspan(2).padBottom(30).row();

        table.add(playButton).width(200).height(50).padBottom(20).padRight(10);
        playButton.setColor(GamePalette.Neon.LIME);
        table.add(leaderboardButton).width(200).height(50).padBottom(20).padLeft(10);
        leaderboardButton.setColor(GamePalette.Neon.YELLOW);
        table.row();

        table.add(logoutButton).width(200).height(50).padBottom(20).padRight(10);
        logoutButton.setColor(GamePalette.Bright.SKY);
        table.add(exitButton).width(200).height(50).padBottom(20).padLeft(10);
        exitButton.setColor(GamePalette.Neon.RED);
        table.row();

        stage.addActor(table);

        // Events
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Nanti kita buat LevelSelectScreen di sini
                Gdx.app.log("MENU", "Pindah ke Level Select...");
                // game.setScreen(new LevelSelectScreen(game));
            }
        });

        leaderboardButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Pindah ke layar leaderboard
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

    @Override
    public void render(float delta) {
        // Background biru gelap ala Geometry Dash
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.3f, 1);
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
        skin.dispose();
    }
}
