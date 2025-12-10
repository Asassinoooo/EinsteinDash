package com.EinsteinDash.frontend;

import com.EinsteinDash.frontend.audio.AudioManager;
import com.EinsteinDash.frontend.network.BackendFacade;
import com.EinsteinDash.frontend.screens.LoginScreen;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.SkinLoader;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * Main - Entry point game EinsteinDash.
 */
public class Main extends Game {

    // === SHARED RESOURCES ===
    public SpriteBatch batch;       // Renderer untuk semua screen
    public AssetManager assets;     // Manager untuk load/unload asset
    public BackendFacade backend;   // Facade komunikasi ke REST API

    // === MANAGERS ===
    private AudioManager audioManager;  // Manager untuk audio/music

    // ==================== LIFECYCLE ====================

    @Override
    public void create() {
        // Inisialisasi shared resources
        batch = new SpriteBatch();
        assets = new AssetManager();
        backend = new BackendFacade();

        // Inisialisasi managers
        audioManager = new AudioManager();
        audioManager.loadAllTracks();

        // Load UI skin
        assets.load("uiskin.json", Skin.class, new SkinLoader.SkinParameter("uiskin.atlas"));
        assets.finishLoading();

        // Tampilkan layar login sebagai starting screen
        this.setScreen(new LoginScreen(this));
    }

    @Override
    public void render() {
        super.render();  // Delegasi ke screen aktif
    }

    @Override
    public void dispose() {
        batch.dispose();
        assets.dispose();
        audioManager.dispose();
    }

    // ==================== GETTERS ====================

    /**
     * Get AudioManager untuk kontrol music dan sound effects.
     * @return AudioManager instance
     */
    public AudioManager getAudioManager() {
        return audioManager;
    }
}
