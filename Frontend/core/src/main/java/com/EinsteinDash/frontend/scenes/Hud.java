package com.EinsteinDash.frontend.scenes;

import com.EinsteinDash.frontend.screens.PauseWindow; // Pastikan import ini
import com.EinsteinDash.frontend.utils.GamePalette;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.EinsteinDash.frontend.utils.Constants;

public class Hud {
    public Stage stage;
    private Viewport viewport;

    // Widget UI
    private ProgressBar progressBar;
    private Label percentageLabel;

    // --- TAMBAHAN BARU: Reference ke Pause Window ---
    private PauseWindow currentPauseWindow;

    public Hud(SpriteBatch sb) {
        // Setup Viewport UI
        viewport = new FitViewport(Constants.V_WIDTH, Constants.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, sb);

        // Buat style progress bar
        ProgressBar.ProgressBarStyle progressBarStyle = new ProgressBar.ProgressBarStyle();
        progressBarStyle.background = getDrawable(GamePalette.Dark.MIDNIGHT, 10, 10);
        progressBarStyle.knobBefore = getDrawable(GamePalette.Neon.LIME, 10, 10);
        progressBarStyle.knob = getDrawable(Color.CLEAR, 0, 10);

        progressBar = new ProgressBar(0, 100, 0.01f, false, progressBarStyle);
        progressBar.setValue(0);
        progressBar.setAnimateDuration(0.0f);
        percentageLabel = new Label("0%", new Label.LabelStyle(new BitmapFont(), Color.WHITE));

        // Layout dengan table
        Table table = new Table();
        table.top();
        table.setFillParent(true);
        table.add(progressBar).width(Constants.V_WIDTH * 0.6f).padTop(20).padRight(10);
        table.add(percentageLabel).padTop(20);

        stage.addActor(table);
    }

    private TextureRegionDrawable getDrawable(Color color, int w, int h) {
        Pixmap pixmap = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        TextureRegionDrawable drawable = new TextureRegionDrawable(new TextureRegion(new Texture(pixmap)));
        pixmap.dispose();
        return drawable;
    }

    public void update(float playerX, float levelEndX) {
        float progress = playerX / levelEndX;
        if (progress < 0) progress = 0;
        if (progress > 1) progress = 1;

        progressBar.setValue(progress * 100);
        percentageLabel.setText((int)(progress * 100) + "%");
    }

    // --- METHOD BARU: KONTROL PAUSE MENU ---

    public void showPauseWindow(PauseWindow window) {
        // Jika ada window lama, hapus dulu agar tidak menumpuk
        if (currentPauseWindow != null) {
            currentPauseWindow.remove();
        }

        this.currentPauseWindow = window;
        stage.addActor(currentPauseWindow);
    }

    public void hidePauseWindow() {
        if (currentPauseWindow != null) {
            currentPauseWindow.remove(); // Hapus dari layar
            currentPauseWindow = null;   // Reset variable
        }
    }

    public void dispose() {
        stage.dispose();
    }

    public int getPercentage() {
        return (int)progressBar.getValue();
    }
}
