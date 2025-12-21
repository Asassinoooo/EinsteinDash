package com.EinsteinDash.frontend.background;

import com.EinsteinDash.frontend.Main;
import com.EinsteinDash.frontend.utils.Constants;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;

public class GeneralBackgroundAnimation implements Disposable {

    private final Main game;
    private Texture bgTexture;
    private Texture shipTexture;
    private Texture ufoTexture;

    private float stateTime = 0;

    // Ship (Top Left)
    // Adjusted: Down 50px, Right 10px -> (50+10, V_HEIGHT - 100 - 50)
    private final Vector2 shipPos = new Vector2(60, Constants.V_HEIGHT - 150);
    private final Vector2 shipStartPos = new Vector2(60, Constants.V_HEIGHT - 150);

    // UFO (Top Right)
    // Adjusted: Left 20px more, Down 20px more -> (V_WIDTH - 170 - 20, V_HEIGHT - 170 - 20)
    // Final: Left 40px total, Down 90px total from original
    private final Vector2 ufoPos = new Vector2(Constants.V_WIDTH - 190, Constants.V_HEIGHT - 190);
    private final Vector2 ufoStartPos = new Vector2(Constants.V_WIDTH - 190, Constants.V_HEIGHT - 190);

    public GeneralBackgroundAnimation(Main game) {
        this.game = game;
        loadTextures();
    }

    private void loadTextures() {
        // Using GeneralBackground as requested
        bgTexture = new Texture("background/GeneralBackground.png");
        shipTexture = new Texture("player/player_ship.png");
        ufoTexture = new Texture("player/player_ufo.png");
    }

    public void render(float delta, SpriteBatch batch) {
        stateTime += delta;
        update(delta);

        batch.begin();
        // 1. Main Background - Lowered 80px to match MenuScreen
        batch.draw(bgTexture, 0, -80, Constants.V_WIDTH, Constants.V_HEIGHT);

        // 2. Animated Elements
        // Ship (Top Left) - Scaled 2x (80x50 -> 160x100)
        batch.draw(shipTexture, shipPos.x, shipPos.y, 160, 100);

        // UFO (Top Right) - Scaled 2x (70x70 -> 140x140)
        batch.draw(ufoTexture, ufoPos.x, ufoPos.y, 140, 140);

        batch.end();
    }

    private void update(float delta) {
        // Simple floating animation using Sin/Cos
        shipPos.y = shipStartPos.y + MathUtils.sin(stateTime * 2f) * 10;
        
        ufoPos.y = ufoStartPos.y + MathUtils.cos(stateTime * 1.5f) * 10;
        ufoPos.x = ufoStartPos.x + MathUtils.sin(stateTime * 1f) * 20;
    }

    @Override
    public void dispose() {
        if (bgTexture != null) bgTexture.dispose();
        if (shipTexture != null) shipTexture.dispose();
        if (ufoTexture != null) ufoTexture.dispose();
    }
}
