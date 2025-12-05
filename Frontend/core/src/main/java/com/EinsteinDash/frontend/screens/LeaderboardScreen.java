package com.EinsteinDash.frontend.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.EinsteinDash.frontend.Main;
import com.EinsteinDash.frontend.network.BackendFacade;
import com.EinsteinDash.frontend.utils.Constants;
import com.EinsteinDash.frontend.utils.GamePalette;

public class LeaderboardScreen extends ScreenAdapter {

    private final Main game;
    private Stage stage;
    private Skin skin;  // Aset tampilan UI
    private Table leaderboardTable; // Tabel untuk isi data
    private Label statusLabel;  // Menampilkan status sedang apa

    public LeaderboardScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(new FitViewport(Constants.V_WIDTH, Constants.V_HEIGHT));
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        // Layout Utama
        Table rootTable = new Table();
        rootTable.setFillParent(true);
        rootTable.center();

        // Judul
        Label title = new Label("TOP 10 PLAYERS", skin);
        title.setFontScale(2f);
        title.setColor(GamePalette.Neon.CYAN);

        // Status (Loading...)
        statusLabel = new Label("Fetching data...", skin);
        statusLabel.setColor(Color.DARK_GRAY);

        // ScrollPane (Agar bisa discroll jika data banyak)
        leaderboardTable = new Table();
        ScrollPane scrollPane = new ScrollPane(leaderboardTable, skin);
        scrollPane.setFadeScrollBars(false);

        // Tombol Back
        TextButton backButton = new TextButton("BACK TO MENU", skin);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MenuScreen(game));
            }
        });

        // Menyusun Layout Root
        rootTable.add(title).padBottom(20).row();
        rootTable.add(statusLabel).padBottom(10).row();

        // Kotak tabel leaderboard
        rootTable.add(scrollPane).width(600).height(400).padBottom(20).row();
        rootTable.add(backButton).width(200).height(50).row();
        scrollPane.setColor(GamePalette.Dark.INDIGO);
        stage.addActor(rootTable);

        // Panggil Backend
        fetchData();
    }

    private void fetchData() {
        game.backend.getLeaderboard(new BackendFacade.LeaderboardCallback() {
            // Berhasil Fetch data
            @Override
            public void onSuccess(JsonValue rootData) {
                statusLabel.setText(""); // Hapus tulisan loading
                populateTable(rootData);
            }

            // Gagal Fetch data
            @Override
            public void onFailed(String errorMessage) {
                statusLabel.setText(errorMessage);
                statusLabel.setColor(GamePalette.Neon.RED);
            }
        });
    }

    private void populateTable(JsonValue data) {
        leaderboardTable.clear(); // Bersihkan isi lama

        // Header Table
        leaderboardTable.add(new Label("RANK", skin)).width(100).pad(10);
        leaderboardTable.add(new Label("USERNAME", skin)).width(300).pad(10);
        leaderboardTable.add(new Label("STARS", skin)).width(100).pad(10);
        leaderboardTable.row();

        // Garis Pembatas Header
        leaderboardTable.add(new Image(skin.newDrawable("white", Color.GRAY))).colspan(3).height(2).fillX().padBottom(10).row();

        int rank = 1;
        // Looping data JSON
        for (JsonValue user : data) {
            String username = user.getString("username");
            int stars = user.getInt("totalStars");

            // Tentukan Warna Berdasarkan Ranking
            Color rankColor = Color.WHITE;
            if (rank == 1) rankColor = GamePalette.Bright.GOLD;       // Emas
            else if (rank == 2) rankColor = GamePalette.Bright.SILVER; // Perak
            else if (rank == 3) rankColor = GamePalette.Neon.ORANGE;   // Perunggu/Orange

            // Buat Label
            Label rankLabel = new Label("#" + rank, skin);
            Label nameLabel = new Label(username, skin);
            Label scoreLabel = new Label(String.valueOf(stars), skin);

            rankLabel.setColor(rankColor);
            nameLabel.setColor(rankColor);
            scoreLabel.setColor(rankColor);

            // Masukkan ke Tabel
            leaderboardTable.add(rankLabel).pad(5);
            leaderboardTable.add(nameLabel).left().pad(5);
            leaderboardTable.add(scoreLabel).right().pad(5);
            leaderboardTable.row();

            // Garis tipis antar baris
            leaderboardTable.add(new Image(skin.newDrawable("white", GamePalette.Dark.SLATE))).colspan(3).height(1).fillX().padBottom(5).row();

            rank++;
        }
    }

    @Override
    public void render(float delta) {
        // Background Gelap
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
        skin.dispose();
    }
}
