package com.EinsteinDash.frontend.scenes;

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

    public Hud(SpriteBatch sb) {
        // Setup Viewport UI
        viewport = new FitViewport(Constants.V_WIDTH, Constants.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, sb);

        // Buat style progress bar
        ProgressBar.ProgressBarStyle progressBarStyle = new ProgressBar.ProgressBarStyle();
        // Background (garis kosong)
        progressBarStyle.background = getDrawable(GamePalette.Dark.MIDNIGHT, 10, 10);
        // KnobBefore (bagian yang sudah terisi)
        progressBarStyle.knobBefore = getDrawable(GamePalette.Neon.LIME, 10, 10);

        // Setup progress bar
        progressBarStyle.knob = getDrawable(Color.CLEAR, 0, 10);
        progressBar = new ProgressBar(0, 100, 0.01f, false, progressBarStyle);
        progressBar.setValue(0);    // Value awal 0%
        progressBar.setAnimateDuration(0.0f); // Matikan animasi internal agar responsif realtime
        percentageLabel = new Label("0%", new Label.LabelStyle(new BitmapFont(), Color.WHITE));

        // Layout dengan table
        Table table = new Table();
        table.top(); // Di bagian atas layar
        table.setFillParent(true);
        // Tambah Bar (Lebar 60% layar)
        table.add(progressBar).width(Constants.V_WIDTH * 0.6f).padTop(20).padRight(10);
        // Tambah % di sebelahnya
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
        // Hitung persentase (0.0 sampai 1.0)
        float progress = playerX / levelEndX;

        // Fix agar tidak kurang dari 0 atau lebih dari 100
        if (progress < 0) progress = 0;
        if (progress > 1) progress = 1;

        // Update Visual (Kali 100 jadi persen)
        progressBar.setValue(progress * 100);
        percentageLabel.setText((int)(progress * 100) + "%");
    }

    public void dispose() {
        stage.dispose();
    }

    public int getPercentage() {
        return (int)progressBar.getValue();
    }
}
