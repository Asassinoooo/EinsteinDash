package com.EinsteinDash.frontend.screens;

import com.EinsteinDash.frontend.Main;
import com.EinsteinDash.frontend.network.BackendFacade;
import com.EinsteinDash.frontend.utils.Constants;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;

/**
 * RegisterScreen - Halaman registrasi user baru.
 */
public class RegisterScreen extends ScreenAdapter {

    private final Main game;
    private Stage stage;
    private Skin skin;

    private TextField usernameField;
    private TextField passwordField;
    private Label statusLabel;

    public RegisterScreen(Main game) {
        this.game = game;
    }

    // ==================== LIFECYCLE ====================

    @Override
    public void show() {
        stage = new Stage(new FitViewport(Constants.V_WIDTH, Constants.V_HEIGHT));
        Gdx.input.setInputProcessor(stage);
        skin = game.assets.get("uiskin.json", Skin.class);

        setupUI();
    }

    /** Setup UI layout */
    private void setupUI() {
        Table table = new Table();
        table.setFillParent(true);

        Label titleLabel = new Label("CREATE ACCOUNT", skin);
        titleLabel.setFontScale(2);

        usernameField = new TextField("", skin);
        usernameField.setMessageText("New Username");

        passwordField = new TextField("", skin);
        passwordField.setMessageText("New Password");
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');

        TextButton registerButton = new TextButton("REGISTER", skin);
        TextButton backButton = new TextButton("BACK TO LOGIN", skin);

        statusLabel = new Label("", skin);
        statusLabel.setColor(1, 0, 0, 1);

        // Layout
        table.add(titleLabel).padBottom(30).row();
        table.add(usernameField).width(200).padBottom(10).row();
        table.add(passwordField).width(200).padBottom(20).row();
        table.add(registerButton).width(150).padBottom(10).row();
        table.add(backButton).width(150).padBottom(10).row();
        table.add(statusLabel).row();

        stage.addActor(table);

        // Button listeners
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

    /** Proses register via BackendFacade */
    private void handleRegister() {
        String user = usernameField.getText();
        String pass = passwordField.getText();

        if (user.isEmpty() || pass.isEmpty()) {
            statusLabel.setText("Username/Pass cannot be empty!");
            return;
        }

        statusLabel.setText("Registering...");

        game.backend.register(user, pass, new BackendFacade.RegisterCallback() {
            @Override
            public void onSuccess() {
                statusLabel.setColor(0, 1, 0, 1);
                statusLabel.setText("Success! Please Login.");
            }

            @Override
            public void onFailed(String errorMessage) {
                statusLabel.setColor(1, 0, 0, 1);
                statusLabel.setText(errorMessage);
            }
        });
    }

    // ==================== RENDER & DISPOSE ====================

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
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
