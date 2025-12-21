package com.EinsteinDash.frontend.screens;

import com.EinsteinDash.frontend.Main;
import com.EinsteinDash.frontend.model.LevelDto;
import com.EinsteinDash.frontend.utils.Constants;
import com.EinsteinDash.frontend.utils.GamePalette;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.math.Vector2;

/**
 * LevelCompletedWindow - Window popup saat level berhasil diselesaikan.
 * Refined: Neon Purple BG (30%), Colored Buttons, Neon Outlines, Larger Header.
 */
public class LevelCompletedWindow extends Window {

    private final ShapeRenderer shapeRenderer;
    
    // UI Helpers for outline
    private TextButton btnMenu;
    private TextButton btnReplay;

    public LevelCompletedWindow(final Main game, Skin skin,
                                final LevelDto levelData,
                                int starsGot, int coinsGot,
                                final int totalCoinsRun) {
        super("LEVEL COMPLETED!", skin);
        
        this.shapeRenderer = new ShapeRenderer();

        // 1. Window Style & Background
        // USER REQUEST: Ungu, opacity persis pause window (30%)
        Color winColor = new Color(GamePalette.Neon.PURPLE); 
        winColor.a = 0.3f;
        
        setBackground(skin.newDrawable("white", winColor));

        setModal(true);
        setMovable(false);
        setSize(500, 370); // Slightly taller for content + header
        
        // Header adjustment (+20px equivalent logic)
        padTop(60); 
        getTitleLabel().setAlignment(Align.center);
        getTitleLabel().setFontScale(1.5f);

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
        add(congratsLabel).colspan(2).padBottom(20).row();

        // Stars reward
        if (starsGot > 0) {
            Label starText = new Label("New Stars Earned: +" + starsGot, skin);
            starText.setColor(GamePalette.Neon.YELLOW);
            add(starText).colspan(2).row();
        } else {
            Label noStar = new Label("Stars: (Already Collected)", skin);
            noStar.setColor(Color.LIGHT_GRAY); // Cleaner than charcoal on purple
            add(noStar).colspan(2).row();
        }

        // Coins reward
        if (coinsGot > 0) {
            Label coinText = new Label("New Coins Collected: +" + coinsGot, skin);
            coinText.setColor(GamePalette.Neon.BLUE);
            add(coinText).colspan(2).row();
        } else {
            Label noCoin = new Label("Coins: No new coins found", skin);
            noCoin.setColor(Color.LIGHT_GRAY);
            add(noCoin).colspan(2).row();
        }
    }

    /** Setup navigation buttons */
    private void setupButtons(final Main game, Skin skin, final LevelDto levelData) {
        // Buttons: Menu (Blue) & Restart (Purple) matching Pause Window logic
        btnMenu = new TextButton("MENU", skin);
        btnMenu.setColor(GamePalette.Neon.BLUE);
        
        btnReplay = new TextButton("RESTART", skin);
        btnReplay.setColor(GamePalette.Neon.PURPLE);

        Table buttonTable = new Table();
        buttonTable.add(btnMenu).width(140).padRight(20);
        buttonTable.add(btnReplay).width(140);
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
    
    @Override
    public void draw(Batch batch, float parentAlpha) {
        // Draw window and children
        super.draw(batch, parentAlpha);
        
        // Draw Outlines
        batch.end();
        
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.setTransformMatrix(batch.getTransformMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        
        drawButtonOutline(btnMenu, GamePalette.Neon.BLUE);
        drawButtonOutline(btnReplay, GamePalette.Neon.PURPLE);
        
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
        
        batch.begin();
    }
    
    private void drawButtonOutline(TextButton btn, Color color) {
        if (btn.isOver()) {
            shapeRenderer.setColor(color);
            Vector2 v = btn.localToStageCoordinates(new Vector2(0,0));
            float x = v.x;
            float y = v.y;
            float w = btn.getWidth();
            float h = btn.getHeight();
            
            for(int i=0; i<3; i++) {
                 shapeRenderer.rect(x - i, y - i, w + i*2, h + i*2);
            }
        }
    }
}
