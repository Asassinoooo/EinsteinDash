package com.EinsteinDash.frontend.screens;

import com.EinsteinDash.frontend.Main;
import com.EinsteinDash.frontend.screens.LevelSelectScreen;
import com.EinsteinDash.frontend.screens.PlayScreen;
import com.EinsteinDash.frontend.utils.Constants;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

public class PauseWindow extends Window {

    public PauseWindow(final Main game, final PlayScreen playScreen, Skin skin) {
        super("GAME PAUSED", skin);

        setModal(true);       // Memblokir input di belakang
        setMovable(false);    // Tidak bisa digeser

        // Ukuran window
        setSize(500, 300);

        // Setup tabel layout default
        defaults().pad(10);

        // Agar isi tabel rata tengah
        getTitleLabel().setAlignment(Align.center);

        // Setup tombol
        TextButton btnMenu = new TextButton("MENU", skin);
        TextButton btnContinue = new TextButton("CONTINUE", skin);
        TextButton btnRestart = new TextButton("RESTART", skin);

        // Tabel khusus untuk menyusun tombol sejajar (Menu - Continue - Restart)
        Table buttonTable = new Table();

        // Menu (Kiri)
        buttonTable.add(btnMenu).width(110).padRight(15);
        // Continue (Tengah - Sedikit lebih lebar agar menonjol)
        buttonTable.add(btnContinue).width(140).padRight(15);
        // Restart (Kanan)
        buttonTable.add(btnRestart).width(110);

        // Masukkan tabel tombol ke Window (Baris 1)
        add(buttonTable).expandX().fillX().row();

        // Setup knob volume
        Label volLabel = new Label("Music Volume", skin);
        volLabel.setAlignment(Align.center);

        // Tambahkan Label (Baris 2)
        add(volLabel).padTop(30).row();

        // Slider (Baris 3)
        final Slider volumeSlider = new Slider(0.0f, 1.0f, 0.1f, false, skin);
        volumeSlider.setValue(game.getMusicVolume()); // Ambil volume saat ini dari Main

        add(volumeSlider).width(300).padTop(5).row();

        // Tombol Continue
        btnContinue.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                playScreen.resumeGame(); // Kembali main
                remove(); // Tutup window ini
            }
        });

        // Tombol Restart
        btnRestart.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("UI", "Restarting Level...");
                playScreen.onPlayerDied();
            }
        });

        // Tombol Menu
        btnMenu.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Kembali ke Level Select
                game.setScreen(new LevelSelectScreen(game));
            }
        });

        // Slider Volume Logic
        volumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setMusicVolume(volumeSlider.getValue());
            }
        });

        // --- 5. POSISI AKHIR ---
        // Letakkan di tengah layar (Menggunakan rumus yang sama dengan base)
        setPosition((Constants.V_WIDTH - getWidth()) / 2, (Constants.V_HEIGHT - getHeight()) / 2);
    }
}
