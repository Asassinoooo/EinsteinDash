package com.EinsteinDash.frontend.scenes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.EinsteinDash.frontend.utils.Constants;

public class Hud {
    public Stage stage;
    private Viewport viewport;

    // Variabel Skor
    private Integer scoreCount;
    private Label scoreLabel;
    private Label coinLabel; // Tulisan "COINS:"

    public Hud(SpriteBatch sb) {
        scoreCount = 0;

        // 1. Setup Kamera Khusus UI (Tidak bergerak)
        // Kita pakai resolusi asli layar (V_WIDTH) tanpa dibagi PPM agar tulisan tajam
        viewport = new FitViewport(Constants.V_WIDTH, Constants.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, sb);

        // 2. Gunakan Table untuk tata letak yang rapi
        Table table = new Table();
        table.top(); // Tempel ke atas
        table.setFillParent(true); // Sebesar layar

        // 3. Buat Label (Tulisan)
        // Gunakan font default LibGDX dulu (Color.WHITE)
        coinLabel = new Label("COINS", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        scoreLabel = new Label(String.format("%03d", scoreCount), new Label.LabelStyle(new BitmapFont(), Color.WHITE));

        // 4. Masukkan ke Table
        // expandX = bagi rata lebar layar
        // padTop = beri jarak dari atas
        table.add(coinLabel).expandX().padTop(10);
        table.add(scoreLabel).expandX().padTop(10);

        // 5. Masukkan Table ke Stage
        stage.addActor(table);
    }

    public void addScore(int value) {
        scoreCount += value;
        scoreLabel.setText(String.format("%03d", scoreCount));
    }

    public void dispose() {
        stage.dispose();
    }
}
