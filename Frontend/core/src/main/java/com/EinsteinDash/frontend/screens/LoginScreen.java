package com.EinsteinDash.frontend.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.EinsteinDash.frontend.Main;
import com.EinsteinDash.frontend.network.BackendFacade;
import com.EinsteinDash.frontend.utils.Constants;

import javax.swing.plaf.basic.BasicTreeUI;
import java.awt.*;
import java.security.Key;

public class LoginScreen extends ScreenAdapter {

    private final Main game;
    private Stage stage;
    private Skin skin; // Aset tampilan UI

    // Komponen UI
    private TextField usernameField;
    private TextField passwordField;
    private Label statusLabel;

    public LoginScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        // Setup Stage
        stage = new Stage(new FitViewport(Constants.V_WIDTH, Constants.V_HEIGHT));
        Gdx.input.setInputProcessor(stage); // Agar tombol bisa diklik

        // Load Skin (Pastikan file uiskin ada di folder assets)
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        // Layout menggunakan Table (Seperti HTML Table)
        Table table = new Table();
        table.setFillParent(true); // Memenuhi layar
        // table.setDebug(true); // untuk melihat outline

        // Buat Widget Text
        Label titleLabel = new Label("EINSTEIN DASH", skin);
        titleLabel.setFontScale(2);

        usernameField = new TextField("", skin);
        usernameField.setMessageText("Username");

        passwordField = new TextField("", skin);
        passwordField.setMessageText("Password");
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');

        TextButton loginButton = new TextButton("LOGIN", skin);
        statusLabel = new Label("", skin);
        TextButton registerButton = new TextButton("No account? Register here", skin);
        registerButton.getLabel().setFontScale(0.8f);

        // Enter di field username akan langsung ke field password
        usernameField.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == com.badlogic.gdx.Input.Keys.ENTER) {
                    stage.setKeyboardFocus(passwordField);
                    return true;
                }
                return false;
            }
        });

        // Enter di field password akan langsung ke verifikasi login
        passwordField.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == com.badlogic.gdx.Input.Keys.ENTER) {
                    handleLogin();
                    return true;
                }
                return false;
            }
        });

        // Agar kursor langsung di username saat awal
        stage.setKeyboardFocus(usernameField);

        // Masukkan ke Table
        table.add(titleLabel).padBottom(20).row();
        table.add(usernameField).width(200).padBottom(10).row();
        table.add(passwordField).width(200).padBottom(20).row();
        table.add(loginButton).width(100).padBottom(10).row();
        table.add(registerButton).padBottom(10).row();
        table.add(statusLabel).row();
        stage.addActor(table);

        // Logika Tombol Login
        loginButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                handleLogin();
            }
        });

        // Logika Tombol Register
        registerButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new RegisterScreen(game));
            }
        });
    }

    private void handleLogin() {
        String user = usernameField.getText();
        String pass = passwordField.getText();

        statusLabel.setText("Connecting...");

        // Panggil Facade
        game.backend.login(user, pass, new BackendFacade.LoginCallback() {
            @Override
            public void onSuccess() {
                statusLabel.setText("Success!");

                // Transisi ke Menu Screen
                game.setScreen(new MenuScreen(game));
            }

            @Override
            public void onFailed(String errorMessage) {
                statusLabel.setText(errorMessage);
            }
        });
    }

    @Override
    public void render(float delta) {
        // Bersihkan layar (Hitam)
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Gambar UI
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
