package com.EinsteinDash.frontend.screens;

import com.EinsteinDash.frontend.Main;
import com.EinsteinDash.frontend.model.LevelDto;
import com.EinsteinDash.frontend.utils.Constants;
import com.EinsteinDash.frontend.utils.GamePalette;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

/**
 * LevelCompletedWindow - Window popup saat level berhasil diselesaikan.
 * Menampilkan reward (stars & coins) dan opsi navigasi.
 */
public class LevelCompletedWindow extends Window {

    public LevelCompletedWindow(final Main game, Skin skin,
                                final LevelDto levelData,
                                int starsGot, int coinsGot,
                                final int totalCoinsRun) {
        super("LEVEL COMPLETED!", skin);

        setModal(true);
        setMovable(false);
        setSize(500, 350);
        defaults().pad(10);

        setupContent(skin, starsGot, coinsGot);
        setupButtons(game, skin, levelData);

        // Center position
        setPosition((Constants.V_WIDTH - getWidth()) / 2, (Constants.V_HEIGHT - getHeight()) / 2);
    }

    /** Setup content labels */
    private void setupContent(Skin skin, int starsGot, int coinsGot) {
        Label congratsLabel = new Label("Congratulations!", skin);
        congratsLabel.setFontScale(1.5f);
        congratsLabel.setAlignment(Align.center);
        add(congratsLabel).colspan(2).row();

        // Stars reward
        if (starsGot > 0) {
            Label starText = new Label("New Stars Earned: +" + starsGot, skin);
            starText.setColor(GamePalette.Neon.YELLOW);
            add(starText).colspan(2).row();
        } else {
            Label noStar = new Label("Stars: (Already Collected)", skin);
            noStar.setColor(GamePalette.Dark.CHARCOAL);
            add(noStar).colspan(2).row();
        }

        // Coins reward
        if (coinsGot > 0) {
            Label coinText = new Label("New Coins Collected: +" + coinsGot, skin);
            coinText.setColor(GamePalette.Neon.BLUE);
            add(coinText).colspan(2).row();
        } else {
            Label noCoin = new Label("Coins: No new coins found", skin);
            noCoin.setColor(GamePalette.Dark.CHARCOAL);
            add(noCoin).colspan(2).row();
        }
    }

    /** Setup navigation buttons */
    private void setupButtons(final Main game, Skin skin, final LevelDto levelData) {
        TextButton btnMenu = new TextButton("MENU", skin);
        TextButton btnReplay = new TextButton("RESTART", skin);

        Table buttonTable = new Table();
        buttonTable.add(btnMenu).width(120).padRight(20);
        buttonTable.add(btnReplay).width(120);
        add(buttonTable).colspan(2).padTop(30);

        btnMenu.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new LevelSelectScreen(game));
            }
        });

        btnReplay.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new PlayScreen(game, levelData));
            }
        });
    }
}
