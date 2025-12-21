package com.EinsteinDash.frontend.screens;

import java.util.ArrayList;

import com.EinsteinDash.frontend.Main;
import com.EinsteinDash.frontend.background.GeneralBackgroundAnimation; // Refactored import
import com.EinsteinDash.frontend.model.LevelDto;
import com.EinsteinDash.frontend.model.ProgressDto;
import com.EinsteinDash.frontend.network.BackendFacade;
import com.EinsteinDash.frontend.utils.Constants;
import com.EinsteinDash.frontend.utils.GamePalette;
import com.EinsteinDash.frontend.utils.Session;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.math.Vector2;

/**
 * LevelSelectScreen - Customized Level Selection UI.
 * Features: Neon Aesthetics, Hover Outlines, Color coding for progress.
 */
public class LevelSelectScreen extends ScreenAdapter {

    private final Main game;
    private Stage stage;
    private Skin skin;
    private Table contentTable;
    private TextButton backButton;
    private ShapeRenderer shapeRenderer;
    private GeneralBackgroundAnimation backgroundAnimation; // Refactored Type

    // List untuk melacak tombol level buat render outline saat hover
    private final ArrayList<TextButton> levelButtons = new ArrayList<>();
    // List paralel untuk melacak warna setiap tombol buat outlinenya
    private final ArrayList<Color> levelButtonColors = new ArrayList<>();

    public LevelSelectScreen(Main game) {
        this.game = game;
    }

    // ==================== LIFECYCLE ====================

    @Override
    public void show() {
        stage = new Stage(new FitViewport(Constants.V_WIDTH, Constants.V_HEIGHT));
        Gdx.input.setInputProcessor(stage);
        skin = game.assets.get("ui/uiskin.json", Skin.class);
        shapeRenderer = new ShapeRenderer();

        // Inisialisasi animasi background (Refactored)
        backgroundAnimation = new GeneralBackgroundAnimation(game);

        setupUI();
        loadLevels();
    }

    /** Setup UI layout */
    private void setupUI() {
        Table mainTable = new Table();
        mainTable.setFillParent(true);

        // Styling Judul: Light Blue (Cyan) agar sesuai dengan Main Menu
        Label titleLabel = new Label("SELECT LEVEL", skin);
        titleLabel.setFontScale(2.5f);
        titleLabel.setColor(GamePalette.Neon.CYAN);

        contentTable = new Table();
        
        // Background Transparan untuk Content Table agar animasi di belakang terlihat
        // Create a dark semi-transparent tint
        Color bgColor = new Color(0, 0, 0, 0.5f);
        com.badlogic.gdx.scenes.scene2d.utils.Drawable bgDrawable = skin.newDrawable("white", bgColor);
        contentTable.setBackground(bgDrawable);

        ScrollPane scrollPane = new ScrollPane(contentTable, skin);
        // Hapus background default scrollpane biat background contentTable terlihat
        if (scrollPane.getStyle().background != null) {
             scrollPane.getStyle().background = null;
        }

        // Styling Tombol Back: Warna dasar biru
        backButton = new TextButton("BACK TO MENU", skin);
        backButton.setColor(GamePalette.Neon.BLUE);
        
        // Listener Klik Custom untuk efek klik lebih gelap
        backButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                // Ubah jadi biru gelap saat diklik
                backButton.setColor(Color.NAVY); 
                return super.touchDown(event, x, y, pointer, button);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                // Kembalikan ke biru asli
                backButton.setColor(GamePalette.Neon.BLUE);
                super.touchUp(event, x, y, pointer, button);
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MenuScreen(game));
            }
        });

        mainTable.add(titleLabel).pad(20).padTop(50).row();
        mainTable.add(scrollPane).width(600).height(400).pad(10).row();
        mainTable.add(backButton).width(200).height(50).pad(20).row();

        stage.addActor(mainTable);
    }
    
    // ==================== DATA LOADING ====================

    private void loadLevels() {
        contentTable.add(new Label("Loading levels...", skin)).row();

        // 1. GUEST MODE (OFFLINE)
        if (Session.getInstance().isGuest()) {
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
                syncProgressAndDisplay(levels);
            }

            @Override
            public void onFailed(String errorMessage) {
                contentTable.clear();
                contentTable.add(new Label("Offline Mode active (" + errorMessage + ")", skin)).padBottom(10).row();
                ArrayList<LevelDto> defaults = com.EinsteinDash.frontend.utils.DefaultLevels.getDefaults();
                syncProgressAndDisplay(defaults);
            }
        });
    }

    private void syncProgressAndDisplay(final ArrayList<LevelDto> levels) {
        if (Session.getInstance().isGuest()) {
            contentTable.clear();
            displayLevels(levels);
            return;
        }

        int userId = Session.getInstance().getUserId();
        contentTable.add(new Label("Syncing progress...", skin));

        game.backend.fetchUserProgress(userId, new BackendFacade.ProgressListCallback() {
            @Override
            public void onSuccess(ArrayList<ProgressDto> progressList) {
                contentTable.clear();
                for (LevelDto level : levels) {
                    for (ProgressDto prog : progressList) {
                        if (prog.getLevelId() == level.getId()) {
                            level.setCompleted(prog.isCompleted());
                            level.setCoinsCollected(prog.getCoinsCollected());
                            level.setPercentage(prog.getPercentage());
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
                displayLevels(levels);
            }
        });
    }

    // ==================== DISPLAY LOGIC ====================

    private void displayLevels(ArrayList<LevelDto> levels) {
        float btnWidth = 500;
        float btnHeight = 60;
        
        levelButtons.clear();
        levelButtonColors.clear();

        for (final LevelDto level : levels) {
            
            // Cek progress lokal & server
            boolean isCompleted = level.isCompleted() || Session.getInstance().isLevelCompleted(level.getId());
            int percentage = level.isCompleted() ? 100 : level.getPercentage();
            if (isCompleted) percentage = 100;

            // Styling Tombol Level
            // Logika: Hijau jika tamat, Abu-abu jika belum
            Color btnColor = isCompleted ? GamePalette.UI.SUCCESS : Color.GRAY;
            
            // Text: "Nama Level (XX%)"
            String btnText = level.getLevelName() + " (" + percentage + "%)";

            TextButton levelBtn = new TextButton(btnText, skin);
            levelBtn.setColor(btnColor);
            
            levelBtn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    game.setScreen(new PlayScreen(game, level));
                }
            });

            levelButtons.add(levelBtn);
            levelButtonColors.add(btnColor);

            contentTable.add(levelBtn).width(btnWidth).height(btnHeight).pad(5).row();
        }
    }

    // ==================== RENDER & DISPOSE ====================

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (backgroundAnimation != null) {
            backgroundAnimation.render(delta, game.batch);
        }

        stage.act(delta);
        stage.draw();

        // === HOVER OUTLINES ===
        shapeRenderer.setProjectionMatrix(stage.getCamera().combined);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        
        // 1. Outline Tombol Back (Neon Cyan saat hover)
        if (backButton.isOver()) {
             drawOutline(backButton, GamePalette.Neon.CYAN);
        }

        // 2. Outline Tombol Level (Sesuai warna tombol saat itu)
        for (int i = 0; i < levelButtons.size(); i++) {
            TextButton btn = levelButtons.get(i);
            if (btn.isOver()) {
                drawOutline(btn, levelButtonColors.get(i));
            }
        }

        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }
    
    private void drawOutline(Actor actor, Color color) {
        shapeRenderer.setColor(color);
        Vector2 start = actor.localToStageCoordinates(new Vector2(0, 0));
        float x = start.x;
        float y = start.y;
        float w = actor.getWidth();
        float h = actor.getHeight();

        // Gambar outline 3 layer agar terlihat tebal
        for(int i=0; i<3; i++) {
             shapeRenderer.rect(x - i, y - i, w + i*2, h + i*2);
        }
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();
        if (backgroundAnimation != null) backgroundAnimation.dispose();
    }
}
