package com.EinsteinDash.frontend.screens;

import com.EinsteinDash.frontend.Main;
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
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.math.Vector2;

/**
 * PauseWindow - Window popup saat game di-pause.
 * Refined: Neon Light Blue BG (50%), Colored Buttons, Neon Outlines, Larger Header.
 */
public class PauseWindow extends Window {

    private final ShapeRenderer shapeRenderer;
    
    // UI Components for outline tracking
    private TextButton btnMenu;
    private TextButton btnContinue;
    private TextButton btnRestart;
    private Slider volumeSlider;

    public PauseWindow(final Main game, final PlayScreen playScreen, Skin skin) {
        super("GAME PAUSED", skin);
        
        this.shapeRenderer = new ShapeRenderer();

        // 1. Window Style & Background
        // Warna Biru Neon Gelap dengan Opacity 30%
        Color winColor = new Color(GamePalette.Neon.BLUE); 
        winColor.a = 0.3f;
        
        setBackground(skin.newDrawable("white", winColor));

        setModal(true);
        setMovable(false);
        setSize(500, 320); // Height adjusted slightly (+20 from 300)
        
        // 5. Header Height + 20px
        // Default padTop is usually height of title. We increase it.
        // Title table holds the label.
        // Let's adjust padTop of the window content to push content down, leaving room for header.
        padTop(60); // Increased padding for header area
        
        getTitleLabel().setAlignment(Align.center);
        // 6. Sesuaikan tulisan (Scale)
        getTitleLabel().setFontScale(1.5f); 

        setupButtons(game, playScreen, skin);
        setupVolumeSlider(game, skin);

        // Center position
        setPosition((Constants.V_WIDTH - getWidth()) / 2, (Constants.V_HEIGHT - getHeight()) / 2);
    }

    /** Setup navigation buttons */
    private void setupButtons(final Main game, final PlayScreen playScreen, Skin skin) {
        // 2. Buttons: Menu (Blue), Continue (Green), Restart (Purple)
        btnMenu = new TextButton("MENU", skin);
        btnMenu.setColor(GamePalette.Neon.BLUE);

        btnContinue = new TextButton("CONTINUE", skin);
        btnContinue.setColor(GamePalette.Neon.LIME); // Green (Bright)

        btnRestart = new TextButton("RESTART", skin);
        btnRestart.setColor(GamePalette.Neon.PURPLE); // Purple

        Table buttonTable = new Table();
        buttonTable.add(btnMenu).width(110).padRight(15);
        buttonTable.add(btnContinue).width(140).padRight(15);
        buttonTable.add(btnRestart).width(110);

        add(buttonTable).expandX().fillX().row();

        // Button listeners
        btnContinue.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                playScreen.resumeGame();
                remove();
            }
        });

        btnRestart.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                playScreen.onPlayerDied(); // Trigger restart logic
                remove();
            }
        });

        btnMenu.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Ensure music stops or handled by screen switch
                game.setScreen(new LevelSelectScreen(game)); 
            }
        });
    }

    /** Setup volume slider */
    private void setupVolumeSlider(final Main game, Skin skin) {
        Label volLabel = new Label("Music Volume", skin);
        volLabel.setAlignment(Align.center);
        add(volLabel).padTop(30).row();

        volumeSlider = new Slider(0.0f, 1.0f, 0.1f, false, skin);
        volumeSlider.setValue(game.getAudioManager().getVolume());
        
        // Custom Style for Blue Knob (Solid & Visible)
        Slider.SliderStyle style = new Slider.SliderStyle(volumeSlider.getStyle());
        
        // Create solid bold knob from 'white' texture
        com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable knobDrawable = 
            new com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable(
                ((com.badlogic.gdx.graphics.g2d.TextureRegion)skin.getRegion("white"))
            );
        knobDrawable.setMinWidth(20);
        knobDrawable.setMinHeight(40); // Taller for visibility
        
        style.knob = knobDrawable.tint(GamePalette.Neon.CYAN);
        style.knobDown = knobDrawable.tint(GamePalette.Neon.CYAN);
        style.knobOver = knobDrawable.tint(GamePalette.Neon.CYAN);
        
        volumeSlider.setStyle(style);

        add(volumeSlider).width(300).padTop(10).row();

        volumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.getAudioManager().setVolume(volumeSlider.getValue());
            }
        });
    }
    
    @Override
    public void draw(Batch batch, float parentAlpha) {
        // Draw window and children normally
        super.draw(batch, parentAlpha);
        
        // Draw Outlines on top
        batch.end();
        
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.setTransformMatrix(batch.getTransformMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        
        // 3. Button Outlines (Hover)
        drawButtonOutline(btnMenu, GamePalette.Neon.BLUE);
        drawButtonOutline(btnContinue, GamePalette.Neon.LIME);
        drawButtonOutline(btnRestart, GamePalette.Neon.PURPLE);
        
        // 4. Volume Bar Outline REMOVED per user request
        
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
        
        batch.begin();
    }
    
    private void drawButtonOutline(TextButton btn, Color color) {
        if (btn.isOver()) {
             drawActorOutline(btn, color, 3);
        }
    }
    
    private void drawActorOutline(Actor actor, Color color, int thickness) {
        shapeRenderer.setColor(color);
        
        // Calculate position in Stage coordinates
        // Since we are inside draw(), the transform matrix might handle local coords if we used it,
        // but ShapeRenderer reset projection to batch's.
        // Window children coordinates are relative to Window.
        // We need stage coordinates.
        Vector2 v = actor.localToStageCoordinates(new Vector2(0,0));
        
        float x = v.x;
        float y = v.y;
        float w = actor.getWidth();
        float h = actor.getHeight();
        
        for(int i=0; i<thickness; i++) {
             shapeRenderer.rect(x - i, y - i, w + i*2, h + i*2);
        }
    }
}
