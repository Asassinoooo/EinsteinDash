package com.einsteindash.frontend;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.einsteindash.frontend.network.BackendFacade;
import com.einsteindash.frontend.screens.LoginScreen;

// Design Pattern: Singleton (Secara konsep, class Game ini hanya ada 1 instance)
public class Main extends Game {
    // SpriteBatch digunakan oleh semua screen (Hemat Memori)
    public SpriteBatch batch;

    // AssetManager untuk memuat gambar/suara terpusat
    public AssetManager assets;

    // Facade untuk komunikasi ke Backend
    public BackendFacade backend;

    @Override
    public void create() {
        batch = new SpriteBatch();
        assets = new AssetManager();
        backend = new BackendFacade();

        // Set layar awal ke LoginScreen
        this.setScreen(new LoginScreen(this));
    }

    @Override
    public void render() {
        super.render(); // Penting! Ini akan memanggil method render() di Screen aktif
    }

    @Override
    public void dispose() {
        batch.dispose();
        assets.dispose();
    }
}
