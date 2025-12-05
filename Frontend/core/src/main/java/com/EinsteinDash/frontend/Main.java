package com.EinsteinDash.frontend;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.SkinLoader;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.EinsteinDash.frontend.network.BackendFacade;
import com.EinsteinDash.frontend.screens.LoginScreen;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

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

        assets.load("uiskin.json", Skin.class, new SkinLoader.SkinParameter("uiskin.atlas"));
        assets.finishLoading();

        // Set layar awal ke LoginScreen
        this.setScreen(new LoginScreen(this));
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        batch.dispose();
        assets.dispose();
    }
}
