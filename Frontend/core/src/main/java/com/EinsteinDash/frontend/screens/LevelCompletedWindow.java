package com.EinsteinDash.frontend.screens;

import com.EinsteinDash.frontend.utils.GamePalette;
import com.EinsteinDash.frontend.model.LevelDto;
import com.EinsteinDash.frontend.network.BackendFacade;
import com.EinsteinDash.frontend.utils.Session;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.EinsteinDash.frontend.Main;
import com.EinsteinDash.frontend.screens.MenuScreen;
import com.EinsteinDash.frontend.screens.PlayScreen;
import com.EinsteinDash.frontend.utils.Constants;

public class LevelCompletedWindow extends Window {

    public LevelCompletedWindow(final Main game, Skin skin,
                                final LevelDto levelData,
                                int starsGot, int coinsGot,
                                final int totalCoinsRun) {
        super("LEVEL COMPLETED!", skin);

        // Setup window
        setModal(true); // Memblokir input ke game di belakangnya
        setMovable(false); // Tidak bisa digeser user
        setPosition(Constants.V_WIDTH / 2f, Constants.V_HEIGHT / 2f, Align.center);

        // Ukuran window
        setSize(500, 350);

        // Setup tabel layout di dalam window
        defaults().pad(10); // mengatur settingan umum untuk setiap sel tabel

        // Konten tulisan
        Label congratsLabel = new Label("Congratulations!", skin);
        congratsLabel.setFontScale(1.5f);
        congratsLabel.setAlignment(Align.center);

        add(congratsLabel).colspan(2).row();

        // Tampilkan Reward
        // Bintang
        if (starsGot > 0) {
            Label starText = new Label("New Stars Earned: +" + starsGot, skin);
            starText.setColor(GamePalette.Neon.YELLOW); // Warna Kuning
            add(starText).colspan(2).row();
        } else {
            Label noStar = new Label("Stars: (Already Collected)", skin);
            noStar.setColor(GamePalette.Dark.CHARCOAL);
            add(noStar).colspan(2).row();
        }

        // Koin
        if (coinsGot > 0) {
            Label coinText = new Label("New Coins Collected: +" + coinsGot, skin);
            coinText.setColor(GamePalette.Neon.BLUE); // Warna biru
            add(coinText).colspan(2).row();
        } else if (coinsGot == 0) {
            Label noCoin = new Label("Coins: No new coins found", skin);
            noCoin.setColor(GamePalette.Dark.CHARCOAL);
            add(noCoin).colspan(2).row();
        }

        // Tombol Navigasi
        TextButton btnMenu = new TextButton("MENU", skin);
        TextButton btnReplay = new TextButton("RESTART", skin);

        // Baris Tombol
        Table buttonTable = new Table();
        buttonTable.add(btnMenu).width(120).padRight(20);
        buttonTable.add(btnReplay).width(120);
        add(buttonTable).colspan(2).padTop(30);

        // Logika Tombol
        btnMenu.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new LevelSelectScreen(game));
            }
        });

        btnReplay.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("UI", "Restarting Level...");
                game.setScreen(new PlayScreen(game, levelData));
            }
        });

        // Agar window muncul di tengah
        setPosition((Constants.V_WIDTH - getWidth()) / 2, (Constants.V_HEIGHT - getHeight()) / 2);
    }
}
