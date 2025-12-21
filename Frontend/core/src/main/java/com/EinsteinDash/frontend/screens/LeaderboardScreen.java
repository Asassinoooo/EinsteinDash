package com.EinsteinDash.frontend.screens;

import com.EinsteinDash.frontend.Main;
import com.EinsteinDash.frontend.background.GeneralBackgroundAnimation;
import com.EinsteinDash.frontend.network.BackendFacade;
import com.EinsteinDash.frontend.utils.Constants;
import com.EinsteinDash.frontend.utils.GamePalette;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
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
import com.badlogic.gdx.math.Vector2;

/**
 * LeaderboardScreen - Menampilkan top 10 player berdasarkan total stars.
 */
public class LeaderboardScreen extends ScreenAdapter {

    private final Main game;
    private Stage stage;
    private Skin skin;
    private Table leaderboardTable;
    private Label statusLabel;
    private TextButton backButton;
    private ShapeRenderer shapeRenderer;
    private GeneralBackgroundAnimation backgroundAnimation;

    public LeaderboardScreen(Main game) {
        this.game = game;
    }

    // ==================== LIFECYCLE ====================

    @Override
    public void show() {
        stage = new Stage(new FitViewport(Constants.V_WIDTH, Constants.V_HEIGHT));
        Gdx.input.setInputProcessor(stage);
        skin = game.assets.get("ui/uiskin.json", Skin.class);
        shapeRenderer = new ShapeRenderer();

        // 1. Init Background Animation (Sama seperti LevelSelect)
        backgroundAnimation = new GeneralBackgroundAnimation(game);

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
        
        // 2. Background Transparan (Sama seperti LevelSelect)
        Color bgColor = new Color(0, 0, 0, 0.5f);
        com.badlogic.gdx.scenes.scene2d.utils.Drawable bgDrawable = skin.newDrawable("white", bgColor);
        leaderboardTable.setBackground(bgDrawable);

        ScrollPane scrollPane = new ScrollPane(leaderboardTable, skin);
        scrollPane.setFadeScrollBars(false);
        // Hapus background scrollpane default
        if (scrollPane.getStyle().background != null) {
            scrollPane.getStyle().background = null;
        }

        // 4. Styling Tombol Back: Warna dasar biru (Sama seperti LevelSelect)
        backButton = new TextButton("BACK TO MENU", skin);
        backButton.setColor(GamePalette.Neon.BLUE);
        
        backButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                backButton.setColor(Color.NAVY); // Efek klik
                return super.touchDown(event, x, y, pointer, button);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                backButton.setColor(GamePalette.Neon.BLUE); // Restore
                super.touchUp(event, x, y, pointer, button);
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MenuScreen(game));
            }
        });

        // Layout
        // 3. Tulisan Top 10 turun 100px -> padTop(100) -> Adjusted to 50 as per request "dinaikan 50px"
        rootTable.add(title).padBottom(20).padTop(50).row();
        rootTable.add(statusLabel).padBottom(10).row();
        // Height reduced 500 -> 350 to show back button
        rootTable.add(scrollPane).width(800).height(350).padBottom(20).row(); 
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

        // Header - Adjusted widths
        leaderboardTable.add(new Label("RANK", skin)).width(80).pad(10);
        leaderboardTable.add(new Label("USERNAME", skin)).width(350).pad(10); 
        leaderboardTable.add(new Label("STARS", skin)).width(120).pad(10);
        leaderboardTable.add(new Label("COINS", skin)).width(120).pad(10);
        leaderboardTable.row();

        // Divider
        leaderboardTable.add(new Image(skin.newDrawable("white", GamePalette.Neon.CYAN))) 
                .colspan(4).height(3).fillX().padBottom(15).row();

        int rank = 1;
        for (JsonValue user : data) {
            String username = user.getString("username");
            int stars = user.getInt("totalStars");
            int coins = user.getInt("totalCoins", 0); 

            // Rank color: gold, silver, bronze
            Color rankColor = Color.WHITE;
            if (rank == 1)
                rankColor = GamePalette.Bright.GOLD;
            else if (rank == 2)
                rankColor = GamePalette.Bright.SILVER;
            else if (rank == 3)
                rankColor = GamePalette.Neon.ORANGE;

            Label rankLabel = new Label("#" + rank, skin);
            Label nameLabel = new Label(username, skin);
            Label scoreLabel = new Label(String.valueOf(stars), skin);
            Label coinLabel = new Label(String.valueOf(coins), skin);

            rankLabel.setColor(rankColor);
            nameLabel.setColor(Color.WHITE); 
            scoreLabel.setColor(GamePalette.Neon.YELLOW); 
            coinLabel.setColor(GamePalette.Bright.GOLD); 

            leaderboardTable.add(rankLabel).pad(5);
            leaderboardTable.add(nameLabel).left().pad(5);
            leaderboardTable.add(scoreLabel).right().pad(5);
            leaderboardTable.add(coinLabel).right().pad(5);
            leaderboardTable.row();

            // Row divider
            leaderboardTable.add(new Image(skin.newDrawable("white", GamePalette.Dark.SLATE)))
                    .colspan(4).height(1).fillX().padBottom(5).row(); 

            rank++;
        }
    }

    // ==================== RENDER & DISPOSE ====================

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Render Background Animation
        if (backgroundAnimation != null) {
            backgroundAnimation.render(delta, game.batch);
        }

        stage.act(delta);
        stage.draw();

        // === HOVER OUTLINE FOR BACK BUTTON ===
        // Logic outline persis seperti LevelSelectScreen
        shapeRenderer.setProjectionMatrix(stage.getCamera().combined);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        
        if (backButton.isOver()) {
             drawOutline(backButton, GamePalette.Neon.CYAN);
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

        // Gambar outline 3 layer
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
