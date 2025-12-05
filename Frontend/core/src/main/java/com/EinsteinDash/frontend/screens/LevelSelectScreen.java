package com.EinsteinDash.frontend.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.EinsteinDash.frontend.Main;
import com.EinsteinDash.frontend.model.LevelDto;
import com.EinsteinDash.frontend.network.BackendFacade;
import com.EinsteinDash.frontend.utils.Constants;

import java.util.ArrayList;

public class LevelSelectScreen extends ScreenAdapter {

    private final Main game;
    private Stage stage;
    private Skin skin;
    private Table contentTable; // Tabel di dalam ScrollPane

    public LevelSelectScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(new FitViewport(Constants.V_WIDTH, Constants.V_HEIGHT));
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        // 1. Setup Layout Utama
        Table mainTable = new Table();
        mainTable.setFillParent(true);

        Label titleLabel = new Label("SELECT LEVEL", skin);
        titleLabel.setFontScale(2);

        // 2. Setup ScrollPane (Agar bisa scroll kalau level banyak)
        contentTable = new Table();
        ScrollPane scrollPane = new ScrollPane(contentTable, skin);

        TextButton backButton = new TextButton("BACK", skin);

        mainTable.add(titleLabel).pad(20).row();
        mainTable.add(scrollPane).width(600).height(400).pad(10).row();
        mainTable.add(backButton).width(150).pad(20).row();

        stage.addActor(mainTable);

        // 3. Load Data Level dari Backend
        loadLevels();

        // 4. Tombol Back
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MenuScreen(game));
            }
        });
    }

    private void loadLevels() {
        // Tampilkan tulisan Loading dulu
        contentTable.add(new Label("Loading levels...", skin)).row();

        game.backend.fetchLevels(new BackendFacade.LevelListCallback() {
            @Override
            public void onSuccess(ArrayList<LevelDto> levels) {
                contentTable.clear(); // Hapus tulisan loading

                if (levels.isEmpty()) {
                    contentTable.add(new Label("No levels found.", skin));
                    return;
                }

                // LOOPING MEMBUAT TOMBOL
                for (LevelDto level : levels) {
                    // Teks tombol: "Stereo Madness (1 Stars)"
                    String btnText = level.getLevelName() + " (" + level.getStars() + " Stars)";
                    TextButton levelBtn = new TextButton(btnText, skin);

                    // Style tombol biar gedean dikit
                    levelBtn.getLabel().setFontScale(1.2f);

                    // Listener: Kalau diklik, mulai main!
                    levelBtn.addListener(new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            Gdx.app.log("LEVEL", "Selected: " + level.getLevelName());
                            Gdx.app.log("DATA", level.getLevelData());

                            // Nanti kita ganti ini ke PlayScreen:
                            // game.setScreen(new PlayScreen(game, level));
                        }
                    });

                    // Masukkan tombol ke tabel
                    contentTable.add(levelBtn).width(500).height(60).pad(10).row();
                }
            }

            @Override
            public void onFailed(String errorMessage) {
                contentTable.clear();
                Label errorLabel = new Label("Error: " + errorMessage, skin);
                errorLabel.setColor(1, 0, 0, 1);
                contentTable.add(errorLabel);
            }
        });
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.2f, 1);
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
