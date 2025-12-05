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

// PENTING: Tambahkan Import PlayScreen
import com.EinsteinDash.frontend.screens.PlayScreen;

import java.util.ArrayList;

public class LevelSelectScreen extends ScreenAdapter {

    private final Main game;
    private Stage stage;
    private Skin skin;
    private Table contentTable;

    public LevelSelectScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(new FitViewport(Constants.V_WIDTH, Constants.V_HEIGHT));
        Gdx.input.setInputProcessor(stage);

        // Menggunakan AssetManager (Optimasi)
        skin = game.assets.get("uiskin.json", Skin.class);

        Table mainTable = new Table();
        mainTable.setFillParent(true);

        Label titleLabel = new Label("SELECT LEVEL", skin);
        titleLabel.setFontScale(2);

        contentTable = new Table();
        ScrollPane scrollPane = new ScrollPane(contentTable, skin);

        TextButton backButton = new TextButton("BACK", skin);

        mainTable.add(titleLabel).pad(20).row();
        mainTable.add(scrollPane).width(800).height(400).pad(10).row(); // Lebar disesuaikan resolusi baru
        mainTable.add(backButton).width(200).height(50).pad(20).row();

        stage.addActor(mainTable);

        loadLevels();

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MenuScreen(game));
            }
        });
    }

    private void loadLevels() {
        contentTable.add(new Label("Loading levels...", skin)).row();

        game.backend.fetchLevels(new BackendFacade.LevelListCallback() {
            @Override
            public void onSuccess(ArrayList<LevelDto> levels) {
                contentTable.clear();

                if (levels.isEmpty()) {
                    contentTable.add(new Label("No levels found.", skin));
                    return;
                }

                // ========================================================
                // DISINI VARIABEL 'level' DIDEFINISIKAN (DALAM LOOP)
                // ========================================================
                for (LevelDto level : levels) {

                    String btnText = level.getLevelName() + " (" + level.getStars() + " Stars)";
                    TextButton levelBtn = new TextButton(btnText, skin);
                    levelBtn.getLabel().setFontScale(1.2f);

                    // Listener ini ada DI DALAM loop, jadi dia kenal variabel 'level'
                    levelBtn.addListener(new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            Gdx.app.log("LEVEL", "Selected: " + level.getLevelName());

                            // Pindah ke PlayScreen dengan membawa objek 'level'
                            game.setScreen(new PlayScreen(game, level));
                        }
                    });

                    contentTable.add(levelBtn).width(600).height(70).pad(10).row();
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
        // skin jangan didispose di sini karena milik Main
    }
}
