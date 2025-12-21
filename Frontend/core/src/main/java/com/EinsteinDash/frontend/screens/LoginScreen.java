package com.EinsteinDash.frontend.screens;

import com.EinsteinDash.frontend.Main;
import com.EinsteinDash.frontend.network.BackendFacade;
import com.EinsteinDash.frontend.utils.Constants;
import com.EinsteinDash.frontend.utils.GamePalette;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
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

/**
 * LoginScreen - Halaman login user.
 * Menyediakan form username/password dan integrasi dengan BackendFacade.
 */
public class LoginScreen extends ScreenAdapter {

    private final Main game;
    private Stage stage;
    private Skin skin;

    // === UI COMPONENTS ===
    private TextField usernameField;
    private TextField passwordField;
    private Label statusLabel;

    public LoginScreen(Main game) {
        this.game = game;
    }

    // ==================== LIFECYCLE ====================

    @Override
    public void show() {
        stage = new Stage(new FitViewport(Constants.V_WIDTH, Constants.V_HEIGHT));
        Gdx.input.setInputProcessor(stage);
        skin = game.assets.get("ui/uiskin.json", Skin.class);

        setupUI();
    }

    /** Setup UI layout */
    private void setupUI() {
        Table table = new Table();
        table.setFillParent(true);

        // Title
        Label titleLabel = new Label("EINSTEIN DASH", skin);
        titleLabel.setFontScale(2);

        // Input Fields
        usernameField = new TextField("", skin);
        usernameField.setMessageText("Username");

        passwordField = new TextField("", skin);
        passwordField.setMessageText("Password");
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');

        // Buttons
        TextButton loginButton = new TextButton("LOGIN", skin);
        TextButton registerButton = new TextButton("No account? Register here", skin);
        registerButton.getLabel().setFontScale(0.8f);

        TextButton guestButton = new TextButton("PLAY AS GUEST / OFFLINE", skin);
        guestButton.setColor(GamePalette.Neon.CYAN);

        statusLabel = new Label("", skin);

        // Enter navigation: username -> password
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

        // Enter di password -> login
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

        // Layout
        table.add(titleLabel).padBottom(20).row();
        table.add(usernameField).width(200).padBottom(10).row();
        table.add(passwordField).width(200).padBottom(20).row();
        table.add(loginButton).width(100).padBottom(10).row();
        table.add(registerButton).padBottom(10).row();
        table.add(guestButton).padBottom(10).row(); // Add Guest Button
        table.add(statusLabel).row();
        stage.addActor(table);

        // Button listeners
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

    /** Proses login via BackendFacade */
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
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
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
