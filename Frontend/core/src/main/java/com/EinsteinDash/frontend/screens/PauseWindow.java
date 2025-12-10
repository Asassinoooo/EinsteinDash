package com.EinsteinDash.frontend.screens;

import com.EinsteinDash.frontend.Main;
import com.EinsteinDash.frontend.utils.Constants;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

/**
 * PauseWindow - Window popup saat game di-pause.
 * Menyediakan opsi Continue, Restart, Menu, dan Volume control.
 */
public class PauseWindow extends Window {

    public PauseWindow(final Main game, final PlayScreen playScreen, Skin skin) {
        super("GAME PAUSED", skin);

        setModal(true);
        setMovable(false);
        setSize(500, 300);
        defaults().pad(10);
        getTitleLabel().setAlignment(Align.center);

        setupButtons(game, playScreen, skin);
        setupVolumeSlider(game, skin);

        // Center position
        setPosition((Constants.V_WIDTH - getWidth()) / 2, (Constants.V_HEIGHT - getHeight()) / 2);
    }

    /** Setup navigation buttons */
    private void setupButtons(final Main game, final PlayScreen playScreen, Skin skin) {
        TextButton btnMenu = new TextButton("MENU", skin);
        TextButton btnContinue = new TextButton("CONTINUE", skin);
        TextButton btnRestart = new TextButton("RESTART", skin);

        Table buttonTable = new Table();
        buttonTable.add(btnMenu).width(110).padRight(15);
        buttonTable.add(btnContinue).width(140).padRight(15);
        buttonTable.add(btnRestart).width(110);

        add(buttonTable).expandX().fillX().row();

        // Button listeners
        btnContinue.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                playScreen.resumeGame();
                remove();
            }
        });

        btnRestart.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                playScreen.onPlayerDied();
            }
        });

        btnMenu.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new LevelSelectScreen(game));
            }
        });
    }

    /** Setup volume slider */
    private void setupVolumeSlider(final Main game, Skin skin) {
        Label volLabel = new Label("Music Volume", skin);
        volLabel.setAlignment(Align.center);
        add(volLabel).padTop(30).row();

        final Slider volumeSlider = new Slider(0.0f, 1.0f, 0.1f, false, skin);
        volumeSlider.setValue(game.getMusicVolume());
        add(volumeSlider).width(300).padTop(5).row();

        volumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setMusicVolume(volumeSlider.getValue());
            }
        });
    }
}
