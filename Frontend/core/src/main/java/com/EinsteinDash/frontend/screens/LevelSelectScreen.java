package com.EinsteinDash.frontend.screens;

import java.util.ArrayList;

import com.EinsteinDash.frontend.Main;
import com.EinsteinDash.frontend.model.LevelDto;
import com.EinsteinDash.frontend.model.ProgressDto;
import com.EinsteinDash.frontend.network.BackendFacade;
import com.EinsteinDash.frontend.utils.Constants;
import com.EinsteinDash.frontend.utils.Session;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;

/**
 * LevelSelectScreen - Menampilkan daftar level yang bisa dipilih.
 * Sync progress dari database dan tampilkan status completed.
 */
public class LevelSelectScreen extends ScreenAdapter {

    private final Main game;
    private Stage stage;
    private Skin skin;
    private Table contentTable;

    public LevelSelectScreen(Main game) {
        this.game = game;
    }

    // ==================== LIFECYCLE ====================

    @Override
    public void show() {
        stage = new Stage(new FitViewport(Constants.V_WIDTH, Constants.V_HEIGHT));
        Gdx.input.setInputProcessor(stage);
        skin = game.assets.get("uiskin.json", Skin.class);

        setupUI();
        loadLevels();
    }

    /** Setup UI layout */
    private void setupUI() {
        Table mainTable = new Table();
        mainTable.setFillParent(true);

        Label titleLabel = new Label("SELECT LEVEL", skin);
        titleLabel.setFontScale(2);

        contentTable = new Table();
        ScrollPane scrollPane = new ScrollPane(contentTable, skin);

        TextButton backButton = new TextButton("BACK", skin);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MenuScreen(game));
            }
        });

        mainTable.add(titleLabel).pad(20).row();
        mainTable.add(scrollPane).width(800).height(400).pad(10).row();
        mainTable.add(backButton).width(200).height(50).pad(20).row();

        stage.addActor(mainTable);
    }

    // ==================== DATA LOADING ====================

    /** Load levels dan sync dengan progress dari database */
    private void loadLevels() {
        contentTable.add(new Label("Loading levels...", skin)).row();

        // 1. GUEST MODE (OFFLINE)
        if (Session.getInstance().isGuest()) {
            Gdx.app.log("LEVEL_SELECT", "Guest Mode: Loading default levels");
            ArrayList<LevelDto> defaults = com.EinsteinDash.frontend.utils.DefaultLevels.getDefaults();
            syncProgressAndDisplay(defaults);
            return;
        }

        // 2. BACKEND FETCH
        game.backend.fetchLevels(new BackendFacade.LevelListCallback() {
            @Override
            public void onSuccess(ArrayList<LevelDto> levels) {
                contentTable.clear();

                if (levels.isEmpty()) {
                    contentTable.add(new Label("No levels found.", skin));
                    return;
                }

                // Sync dengan progress user
                syncProgressAndDisplay(levels);
            }

            @Override
            public void onFailed(String errorMessage) {
                Gdx.app.error("LEVEL_SELECT", "Fetch Failed: " + errorMessage);

                // FALLBACK KE LOCAL LEVELS JIKA SERVER ERROR/OFFLINE
                contentTable.clear();
                contentTable.add(new Label("Offline Mode active (" + errorMessage + ")", skin)).padBottom(10).row();

                ArrayList<LevelDto> defaults = com.EinsteinDash.frontend.utils.DefaultLevels.getDefaults();
                syncProgressAndDisplay(defaults);
            }
        });
    }

    /** Sync progress dari database lalu tampilkan levels */
    private void syncProgressAndDisplay(final ArrayList<LevelDto> levels) {
        // Jika GUEST, tidak perlu fetch progress ke server.
        // Langsung tampilkan level apa adanya (default: locked/0 stars)
        // Atau ambil dari Session.localProgress jika ingin fitur "guest progress
        // sementara"
        if (Session.getInstance().isGuest()) {
            contentTable.clear();
            displayLevels(levels); // Method baru helper
            return;
        }

        int userId = Session.getInstance().getUserId();
        contentTable.add(new Label("Syncing progress...", skin));

        game.backend.fetchUserProgress(userId, new BackendFacade.ProgressListCallback() {
            @Override
            public void onSuccess(ArrayList<ProgressDto> progressList) {
                contentTable.clear();

                // Merge progress dengan level data
                for (LevelDto level : levels) {
                    for (ProgressDto prog : progressList) {
                        if (prog.getLevelId() == level.getId()) {
                            level.setCompleted(prog.isCompleted());
                            level.setCoinsCollected(prog.getCoinsCollected());
                            Session.getInstance().saveLocalProgress(level.getId(), prog.getCoinsCollected());
                            break;
                        }
                    }
                }
                displayLevels(levels);
            }

            @Override
            public void onFailed(String error) {
                contentTable.clear();
                contentTable.add(new Label("Failed to sync: " + error, skin));
                // Tetap tampilkan level meski sync gagal (fallback local progress)
                displayLevels(levels);
            }
        });
    }

    /** Helper untuk menampilkan tombol level */
    private void displayLevels(ArrayList<LevelDto> levels) {
        for (LevelDto level : levels) {
            // Cek local progress juga sebagai cadangan
            if (Session.getInstance().isLevelCompleted(level.getId())) {
                level.setCompleted(true);
            }
            int bestCoins = Session.getInstance().getLevelBestCoins(level.getId());
            if (bestCoins > level.getCoinsCollected()) {
                level.setCoinsCollected(bestCoins);
            }

            // Create button
            String btnText = level.getLevelName() + " (" + level.getStars() + " Stars)";
            TextButton levelBtn = new TextButton(btnText, skin);
            levelBtn.getLabel().setFontScale(1.2f);

            if (level.isCompleted()) {
                levelBtn.setColor(Color.LIME);
            }

            levelBtn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    game.setScreen(new PlayScreen(game, level));
                }
            });

            contentTable.add(levelBtn).width(600).height(70).pad(10).row();
        }
    }

    // ==================== RENDER & DISPOSE ====================

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
    }
}
