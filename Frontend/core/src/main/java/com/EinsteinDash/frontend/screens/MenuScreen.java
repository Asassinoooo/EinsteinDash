package com.EinsteinDash.frontend.screens;

import com.EinsteinDash.frontend.Main;
import com.EinsteinDash.frontend.background.MenuBackgroundAnimation; // Refactored import
import com.EinsteinDash.frontend.network.BackendFacade;
import com.EinsteinDash.frontend.utils.Constants;
import com.EinsteinDash.frontend.utils.GamePalette;
import com.EinsteinDash.frontend.utils.Session;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
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
    private MenuBackgroundAnimation backgroundAnimation;
    private ShapeRenderer shapeRenderer;

    // Buttons (Promoted to fields for hover check)
    private TextButton playButton;
    private TextButton leaderboardButton;
    private TextButton logoutButton;
    private TextButton exitButton;

    // Labels (Promoted to fields for update)
    private Label starsLabel;
    private Label coinsLabel;

    public MenuScreen(Main game) {
        this.game = game;
    }

    // ==================== LIFECYCLE ====================

    @Override
    public void show() {
        stage = new Stage(new FitViewport(Constants.V_WIDTH, Constants.V_HEIGHT));
        Gdx.input.setInputProcessor(stage);
        skin = game.assets.get("ui/uiskin.json", Skin.class);
        backgroundAnimation = new MenuBackgroundAnimation(game);
        shapeRenderer = new ShapeRenderer();

        setupUI();
        updateUserData();
    }

    /** Setup UI layout */
    private void setupUI() {
        Table table = new Table();
        table.setFillParent(true);
        // User requested text moved up 50px, but buttons stay.
        // 1. Move everything up 50px (280 -> 230)
        table.padTop(230);

        Session session = Session.getInstance();

        // Labels
        Label titleLabel = new Label("MAIN MENU", skin);
        titleLabel.setFontScale(2.5f); // Larger
        titleLabel.setColor(0, 1, 1, 1); // Neon Cyan

        Label userLabel = new Label("Welcome, " + session.getUsername() + "!", skin);
        userLabel.setFontScale(2.0f);
        userLabel.setColor(1, 0, 1, 1); // Neon Magenta (Purple) requested

        starsLabel = new Label("Stars: " + session.getTotalStars(), skin);
        starsLabel.setFontScale(1.0f);
        starsLabel.setColor(1, 1, 0, 1); // Neon Yellow

        coinsLabel = new Label("Coins: " + session.getTotalCoins(), skin);
        coinsLabel.setFontScale(1.0f);
        coinsLabel.setColor(1, 1, 0, 1); // Neon Yellow

        // Buttons (Fields)
        playButton = new TextButton("PLAY LEVELS", skin);
        leaderboardButton = new TextButton("LEADERBOARD", skin);
        logoutButton = new TextButton("LOGOUT", skin);
        exitButton = new TextButton("EXIT", skin);

        playButton.setColor(GamePalette.Neon.LIME);
        leaderboardButton.setColor(GamePalette.Neon.YELLOW);
        logoutButton.setColor(GamePalette.Neon.CYAN); // Changed from Bright.SKY to Neon.CYAN
        exitButton.setColor(GamePalette.Neon.RED);

        // Layout
        // Row 1: Title
        table.add(titleLabel).colspan(2).padBottom(5).row(); 

        // Row 2: Welcome (Moved to own row)
        table.add(userLabel).colspan(2).padBottom(10).row();

        // Row 3: Stats (Stars | Gap | Coins)
        Table statsTable = new Table();
        statsTable.add(starsLabel);
        // Add spacer gap roughly size of "Welcome..." text. 
        // Since we don't calculate exact text bounds easily here, we use a fixed width 
        // that approximates the visual gap user liked (~200px or so based on scale 2.0 font).
        statsTable.add().width(300); 
        statsTable.add(coinsLabel);

        table.add(statsTable).colspan(2).padBottom(150).row();

        // Buttons Layout Logic
        if (session.isGuest()) {
            // GUEST: Centered Single Column for Play, Row for Logout/Exit
            table.add(playButton).width(200).height(50).padBottom(20).colspan(2).row();
            
            // Leaderboard hidden
            
            // Logout and Exit side-by-side
            Table guestBtnTable = new Table();
            guestBtnTable.add(logoutButton).width(200).height(50).padBottom(20).padRight(10);
            guestBtnTable.add(exitButton).width(200).height(50).padBottom(20).padLeft(10);
            
            table.add(guestBtnTable).colspan(2).row();
        } else {
            // LOGGED IN: Side-by-Side
            // Use a separate table for buttons to keep centering easy
            Table btnTable = new Table();
            btnTable.add(playButton).width(200).height(50).padBottom(20).padRight(10);
            btnTable.add(leaderboardButton).width(200).height(50).padBottom(20).padLeft(10).row();
            btnTable.add(logoutButton).width(200).height(50).padBottom(20).padRight(10);
            btnTable.add(exitButton).width(200).height(50).padBottom(20).padLeft(10).row();

            table.add(btnTable).colspan(2).row();
        }

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

    private void updateUserData() {
        Session session = Session.getInstance();
        if (session.isLoggedIn() && !session.isGuest()) {
            game.backend.fetchUserData(session.getUserId(), new BackendFacade.UserDataCallback() {
                @Override
                public void onSuccess(int stars, int coins) {
                    if (starsLabel != null)
                        starsLabel.setText("Stars: " + stars);
                    if (coinsLabel != null)
                        coinsLabel.setText("Coins: " + coins);
                }

                @Override
                public void onFailed(String errorMessage) {
                    Gdx.app.error("MENU", "Failed to update user data: " + errorMessage);
                }
            });
        }
    }

    // ==================== RENDER & DISPOSE ====================

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1); // Black background
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (backgroundAnimation != null) {
            backgroundAnimation.render(delta, game.batch);
        }

        stage.act(delta);
        stage.draw();

        // Draw Neon Outline on Hover
        drawHoverOutline(playButton, GamePalette.Neon.LIME);
        drawHoverOutline(leaderboardButton, GamePalette.Neon.YELLOW);
        drawHoverOutline(logoutButton, GamePalette.Neon.CYAN); // Matches new button color
        drawHoverOutline(exitButton, GamePalette.Neon.RED);
    }

    private void drawHoverOutline(TextButton button, com.badlogic.gdx.graphics.Color color) {
        if (button.isOver()) {
            shapeRenderer.setProjectionMatrix(stage.getCamera().combined);
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(color);
            // Draw multiple lines for thickness/glow
            float x = button.getX();
            float y = button.getY();
            float w = button.getWidth();
            float h = button.getHeight();

            // Parent coordinates (Table) might affect X/Y if not using localToStageCoordinates
            // Screen is FitViewport, Input is Stage driven. button.getX() is local to parent table if taking part in layout?
            // Yes, standard buttons in a table need coordinate transformation.
            // Let's rely on button.localToStageCoordinates.

            com.badlogic.gdx.math.Vector2 start = button.localToStageCoordinates(new com.badlogic.gdx.math.Vector2(0, 0));

            for(int i=0; i<3; i++) {
                 shapeRenderer.rect(start.x - i, start.y - i, w + i*2, h + i*2);
            }
            shapeRenderer.end();
            Gdx.gl.glDisable(GL20.GL_BLEND);
        }
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        if (backgroundAnimation != null) {
            backgroundAnimation.dispose();
        }
        stage.dispose();
    }
}
