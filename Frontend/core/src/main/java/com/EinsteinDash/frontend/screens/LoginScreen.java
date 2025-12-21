package com.EinsteinDash.frontend.screens;

import com.EinsteinDash.frontend.Main;
import com.EinsteinDash.frontend.network.BackendFacade;
import com.EinsteinDash.frontend.utils.Constants;
import com.EinsteinDash.frontend.utils.GamePalette;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.math.Vector2;

/**
 * LoginScreen - Halaman login user.
 * Redesigned: Static Background, Custom Button Sizes & Colors, Neon Outlines.
 */
public class LoginScreen extends ScreenAdapter {

    private final Main game;
    private Stage stage;
    private Skin skin;
    private Texture backgroundTexture;
    private ShapeRenderer shapeRenderer;

    // === UI COMPONENTS ===
    private TextField usernameField;
    private TextField passwordField;
    private Label statusLabel;
    
    // Buttons promoted to fields for hover check
    private TextButton loginButton;
    private TextButton registerButton;
    private TextButton guestButton;

    public LoginScreen(Main game) {
        this.game = game;
    }

    // ==================== LIFECYCLE ====================

    @Override
    public void show() {
        stage = new Stage(new FitViewport(Constants.V_WIDTH, Constants.V_HEIGHT));
        Gdx.input.setInputProcessor(stage);
        skin = game.assets.get("ui/uiskin.json", Skin.class);
        
        // 1. Static Background: MainBackground.png
        backgroundTexture = new Texture("background/MainBackground.png");
        shapeRenderer = new ShapeRenderer();

        setupUI();
    }

    /** Setup UI layout */
    private void setupUI() {
        Table table = new Table();
        table.setFillParent(true);
        // User Request: "turun 300px" + "70px" = 370
        table.padTop(370);

        // 3. Remove Title "Einstein Dash" (Title removed)

        // Input Fields
        usernameField = new TextField("", skin);
        usernameField.setMessageText("Username");

        passwordField = new TextField("", skin);
        passwordField.setMessageText("Password");
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');

        // Buttons
        // 4. Colors & 5. Sizes
        // Register (No Account): Grey, Size 1 (Base)
        // Login: Green, Size 2 (2x Base width? Or Scale? "perbandingan ukurannya") -> Assuming Width ratio
        // Guest: Neon Cyan, Size 1.5

        loginButton = new TextButton("LOGIN", skin);
        loginButton.setColor(GamePalette.Neon.LIME); // Brighter Green

        registerButton = new TextButton("No account? Register here", skin);
        registerButton.setColor(Color.GRAY); // Grey
        // "perbesar ukurannya" -> Font scale slightly larger? 
        registerButton.getLabel().setFontScale(1.0f); // Was 0.8f

        guestButton = new TextButton("PLAY AS GUEST / OFFLINE", skin);
        guestButton.setColor(GamePalette.Neon.CYAN);

        statusLabel = new Label("", skin);

        // Enter navigation
        usernameField.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.ENTER) {
                    stage.setKeyboardFocus(passwordField);
                    return true;
                }
                return false;
            }
        });

        passwordField.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.ENTER) {
                    handleLogin();
                    return true;
                }
                return false;
            }
        });

        stage.setKeyboardFocus(usernameField);

        // Layout with Ratio Logic
        float baseWidth = 200f; 
        
        table.add(usernameField).width(300).padBottom(10).row(); // Wider inputs match larger buttons
        table.add(passwordField).width(300).padBottom(20).row();
        
        // Login (Ratio 2)
        table.add(loginButton).width(baseWidth * 2f).height(60).padBottom(15).row();
        
        // Guest (Ratio 1.5)
        table.add(guestButton).width(baseWidth * 1.5f).height(55).padBottom(15).row();
        
        // No Account (Ratio 1)
        table.add(registerButton).width(baseWidth * 1f).height(50).padBottom(10).row();
        
        table.add(statusLabel).row();
        stage.addActor(table);

        // Listeners...
        loginButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                handleLogin();
            }
        });

        registerButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new RegisterScreen(game));
            }
        });

        guestButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                com.EinsteinDash.frontend.utils.Session.getInstance().setGuestMode();
                game.setScreen(new MenuScreen(game));
            }
        });
    }

    // ==================== LOGIN HANDLER ====================

    private void handleLogin() {
        String user = usernameField.getText();
        String pass = passwordField.getText();
        statusLabel.setText("Connecting...");
        game.backend.login(user, pass, new BackendFacade.LoginCallback() {
            @Override
            public void onSuccess() {
                statusLabel.setText("Success!");
                game.setScreen(new MenuScreen(game));
            }
            @Override
            public void onFailed(String errorMessage) {
                statusLabel.setText(errorMessage);
            }
        });
    }

    // ==================== RENDER & DISPOSE ====================

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.begin();
        game.batch.setColor(1, 1, 1, 1); // FIX: Reset color state
         // Lower background by 80px to match MenuScreen
        game.batch.draw(backgroundTexture, 0, -80, Constants.V_WIDTH, Constants.V_HEIGHT);
        game.batch.end();

        stage.act(delta);
        stage.draw();
        
        // 6. Neon Outlines on Hover
        drawHoverOutline(loginButton, GamePalette.Neon.LIME);
        drawHoverOutline(guestButton, GamePalette.Neon.CYAN);
        drawHoverOutline(registerButton, Color.GRAY);
    }
    
    private void drawHoverOutline(TextButton button, Color color) {
        if (button.isOver()) {
            shapeRenderer.setProjectionMatrix(stage.getCamera().combined);
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(color);
            
            Vector2 start = button.localToStageCoordinates(new Vector2(0, 0));
            float x = start.x;
            float y = start.y;
            float w = button.getWidth();
            float h = button.getHeight();

            for(int i=0; i<3; i++) {
                 shapeRenderer.rect(x - i, y - i, w + i*2, h + i*2);
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
        stage.dispose();
        if (backgroundTexture != null) backgroundTexture.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();
    }
}
