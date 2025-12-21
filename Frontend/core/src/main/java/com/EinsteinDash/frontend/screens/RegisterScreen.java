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
 * RegisterScreen - Halaman registrasi user baru.
 * Redesigned: Static Background (y=-80), Lower Layout (-200px), Custom Colors & Outlines.
 */
public class RegisterScreen extends ScreenAdapter {

    private final Main game;
    private Stage stage;
    private Skin skin;
    private Texture backgroundTexture;
    private ShapeRenderer shapeRenderer;

    // === UI COMPONENTS ===
    private TextField usernameField;
    private TextField passwordField;
    private Label statusLabel;

    // Buttons promoted for hover check
    private TextButton registerButton;
    private TextButton backButton;

    public RegisterScreen(Main game) {
        this.game = game;
    }

    // ==================== LIFECYCLE ====================

    @Override
    public void show() {
        stage = new Stage(new FitViewport(Constants.V_WIDTH, Constants.V_HEIGHT));
        Gdx.input.setInputProcessor(stage);
        skin = game.assets.get("ui/uiskin.json", Skin.class);

        // 1. Static Background (Same as LoginScreen)
        backgroundTexture = new Texture("background/MainBackground.png");
        shapeRenderer = new ShapeRenderer();

        setupUI();
    }

    /** Setup UI layout */
    private void setupUI() {
        Table table = new Table();
        table.setFillParent(true);
        // 2. Layout "turunkan posisinya 200px"
        table.padTop(200);

        // 3. CREATE ACCOUNT (Neon Cyan - Brighter)
        Label titleLabel = new Label("CREATE ACCOUNT", skin);
        titleLabel.setFontScale(2);
        titleLabel.setColor(GamePalette.Neon.CYAN); // Neon Cyan

        // Input Fields
        usernameField = new TextField("", skin);
        usernameField.setMessageText("New Username");

        passwordField = new TextField("", skin);
        passwordField.setMessageText("New Password");
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');

        // Buttons
        // 4. Register Button: Green (Like Login -> Neon Lime)
        registerButton = new TextButton("REGISTER", skin);
        registerButton.setColor(GamePalette.Neon.LIME); 

        // 5. Back Button: Blue (Like Leaderboard -> Neon Blue)
        backButton = new TextButton("BACK TO LOGIN", skin);
        backButton.setColor(GamePalette.Neon.BLUE);

        statusLabel = new Label("", skin);

        // Enter Navigation logic
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
                    handleRegister();
                    return true;
                }
                return false;
            }
        });

        stage.setKeyboardFocus(usernameField);

        // Layout Structure
        table.add(titleLabel).padBottom(20).row();
        table.add(usernameField).width(300).padBottom(10).row(); // Same width as Login (300)
        table.add(passwordField).width(300).padBottom(20).row();
        
        // Buttons: Let's keep them readable size. Login uses size logic 2 (400) and 1.5 (300).
        // Let's make Register prominent (Ratio 2? or Standard 200?).
        // User didn't specify size ratio for Register, just said "fill box dan seluruh tombol".
        // Let's use 200 (classic) or 300 (nice). 200 seems standard for secondary screens. 
        // But "back to menu" is long text. 250?
        // Let's stick to 300 to match Input Field width for a clean look.
        
        table.add(registerButton).width(300).height(60).padBottom(15).row();
        table.add(backButton).width(300).height(50).padBottom(10).row();

        table.add(statusLabel).row();
        stage.addActor(table);

        // Listeners
        registerButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                handleRegister();
            }
        });

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new LoginScreen(game));
            }
        });
    }

    // ==================== REGISTER HANDLER ====================

    private void handleRegister() {
        String user = usernameField.getText();
        String pass = passwordField.getText();

        if (user.isEmpty() || pass.isEmpty()) {
            statusLabel.setText("Username/Password cannot be empty!");
            statusLabel.setColor(GamePalette.Neon.RED); // Red error
            return;
        }

        statusLabel.setText("Creating account...");
        statusLabel.setColor(Color.WHITE);

        game.backend.register(user, pass, new BackendFacade.RegisterCallback() {
            @Override
            public void onSuccess() {
                statusLabel.setText("Success! Please Login.");
                statusLabel.setColor(GamePalette.Neon.LIME);
                // Maybe delay or button valid?
                // User can click "Back to Menu" (which goes to Login) manually.
            }

            @Override
            public void onFailed(String errorMessage) {
                statusLabel.setText(errorMessage);
                statusLabel.setColor(GamePalette.Neon.RED);
            }
        });
    }

    // ==================== RENDER & DISPOSE ====================

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.begin();
        // 1. Background Same as Login: y = -80
        game.batch.draw(backgroundTexture, 0, -80, Constants.V_WIDTH, Constants.V_HEIGHT);
        game.batch.end();

        stage.act(delta);
        stage.draw();

        // 6. Neon Outlines
        drawHoverOutline(registerButton, GamePalette.Neon.LIME);
        drawHoverOutline(backButton, GamePalette.Neon.BLUE);
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
