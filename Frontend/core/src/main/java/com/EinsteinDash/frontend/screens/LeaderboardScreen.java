package com.EinsteinDash.frontend.screens;

import com.EinsteinDash.frontend.Main;
import com.EinsteinDash.frontend.network.BackendFacade;
import com.EinsteinDash.frontend.utils.Constants;
import com.EinsteinDash.frontend.utils.GamePalette;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.viewport.FitViewport;

/**
 * LeaderboardScreen - Menampilkan top 10 player berdasarkan total stars.
 */
public class LeaderboardScreen extends ScreenAdapter {

    private final Main game;
    private Stage stage;
    private Skin skin;
    private Table leaderboardTable;
    private Label statusLabel;

    public LeaderboardScreen(Main game) {
        this.game = game;
    }

    // ==================== LIFECYCLE ====================

    @Override
    public void show() {
        stage = new Stage(new FitViewport(Constants.V_WIDTH, Constants.V_HEIGHT));
        Gdx.input.setInputProcessor(stage);
        skin = game.assets.get("uiskin.json", Skin.class);

        setupUI();
        fetchData();
    }

    /** Setup UI layout */
    private void setupUI() {
        Table rootTable = new Table();
        rootTable.setFillParent(true);
        rootTable.center();

        Label title = new Label("TOP 10 PLAYERS", skin);
        title.setFontScale(2f);
        title.setColor(GamePalette.Neon.CYAN);

        statusLabel = new Label("Fetching data...", skin);
        statusLabel.setColor(Color.DARK_GRAY);

        leaderboardTable = new Table();
        ScrollPane scrollPane = new ScrollPane(leaderboardTable, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setColor(GamePalette.Dark.INDIGO);

        TextButton backButton = new TextButton("BACK TO MENU", skin);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MenuScreen(game));
            }
        });

        // Layout
        rootTable.add(title).padBottom(20).row();
        rootTable.add(statusLabel).padBottom(10).row();
        rootTable.add(scrollPane).width(600).height(400).padBottom(20).row();
        rootTable.add(backButton).width(200).height(50).row();

        stage.addActor(rootTable);
    }

    // ==================== DATA FETCHING ====================

    /** Ambil data leaderboard dari backend */
    private void fetchData() {
        game.backend.getLeaderboard(new BackendFacade.LeaderboardCallback() {
            @Override
            public void onSuccess(JsonValue rootData) {
                statusLabel.setText("");
                populateTable(rootData);
            }

            @Override
            public void onFailed(String errorMessage) {
                statusLabel.setText(errorMessage);
                statusLabel.setColor(GamePalette.Neon.RED);
            }
        });
    }

    /** Isi tabel dengan data leaderboard */
    private void populateTable(JsonValue data) {
        leaderboardTable.clear();

        // Header
        leaderboardTable.add(new Label("RANK", skin)).width(100).pad(10);
        leaderboardTable.add(new Label("USERNAME", skin)).width(300).pad(10);
        leaderboardTable.add(new Label("STARS", skin)).width(100).pad(10);
        leaderboardTable.row();

        // Divider
        leaderboardTable.add(new Image(skin.newDrawable("white", Color.GRAY)))
            .colspan(3).height(2).fillX().padBottom(10).row();

        int rank = 1;
        for (JsonValue user : data) {
            String username = user.getString("username");
            int stars = user.getInt("totalStars");

            // Rank color: gold, silver, bronze
            Color rankColor = Color.WHITE;
            if (rank == 1) rankColor = GamePalette.Bright.GOLD;
            else if (rank == 2) rankColor = GamePalette.Bright.SILVER;
            else if (rank == 3) rankColor = GamePalette.Neon.ORANGE;

            Label rankLabel = new Label("#" + rank, skin);
            Label nameLabel = new Label(username, skin);
            Label scoreLabel = new Label(String.valueOf(stars), skin);

            rankLabel.setColor(rankColor);
            nameLabel.setColor(rankColor);
            scoreLabel.setColor(rankColor);

            leaderboardTable.add(rankLabel).pad(5);
            leaderboardTable.add(nameLabel).left().pad(5);
            leaderboardTable.add(scoreLabel).right().pad(5);
            leaderboardTable.row();

            // Row divider
            leaderboardTable.add(new Image(skin.newDrawable("white", GamePalette.Dark.SLATE)))
                .colspan(3).height(1).fillX().padBottom(5).row();

            rank++;
        }
    }

    // ==================== RENDER & DISPOSE ====================

    @Override
    public void render(float delta) {
        Color bg = GamePalette.Dark.INDIGO;
        Gdx.gl.glClearColor(bg.r, bg.g, bg.b, bg.a);
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
