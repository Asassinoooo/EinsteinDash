package com.EinsteinDash.frontend.screens;

import com.EinsteinDash.frontend.Main;
import com.EinsteinDash.frontend.utils.Constants;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

/**
 * MenuBackgroundAnimation - Menangani animasi background di menu utama.
 * Refined: Background down 60px, Scale x2, Spider Animation, Wave Diagonal ZigZag.
 */
public class MenuBackgroundAnimation implements Disposable {

    private final Main game;
    private float stateTime;

    private ShapeRenderer shapeRenderer;

    // === TEXTURES ===
    private Texture bgTexture;
    private Texture cubeTexture;
    private Texture robotTexture;
    private Texture ufoTexture;
    private Texture shipTexture;
    private Texture waveTexture;
    private Texture ballTexture;

    // Spider contains 3 frames
    private Texture spiderTex1, spiderTex2, spiderTex3;
    private Animation<TextureRegion> spiderAnim;

    // === POSITIONS & STATES ===
    
    // 1. Spider (Left side, loop left-right, Animated)
    // Offset: Right +50px + 10px = 160px, Up +3px = 203px
    private final Vector2 spiderPos = new Vector2(160, 203);   
    private float spiderStartX;
    private boolean spiderMovingRight = true;

    // 2. Cube (Center, largest x2, loop up-down)
    // Scale x2 -> 256x256. User req: Scale x0.7 -> 180.
    // Offset: -240 + 10 (Up) = -230
    private final Vector2 cubePos = new Vector2(Constants.V_WIDTH / 2f - 90, Constants.V_HEIGHT / 2f - 230);
    private float cubeStartY;

    // 3. Robot (Right side, static x2)
    // Offset: Right +40px + 10px = 50px, Down +60px + 5px = 65px (y = 135)
    private final Vector2 robotPos = new Vector2(1050, 135);

    // 4. UFO (Top-right, diagonal loop x2)
    private final Vector2 ufoPos = new Vector2(Constants.V_WIDTH - 200, Constants.V_HEIGHT - 200);
    private final Vector2 ufoStartPos = new Vector2(Constants.V_WIDTH - 200, Constants.V_HEIGHT - 200);

    // 5. Ship (Top-left, diagonal loop x2)
    // Offset: Left +200px (x - 200)
    private final Vector2 shipPos = new Vector2(50, Constants.V_HEIGHT - 200);
    private final Vector2 shipStartPos = new Vector2(50, Constants.V_HEIGHT - 200);

    // 6. Wave (Top quarter, Diagonal ZigZag x2)
    private final Array<Vector2> waveTrail = new Array<>();
    private final Vector2 wavePos = new Vector2(0, Constants.V_HEIGHT * 0.75f);
    
    // 7. Ball (Bottom, Loop Left-Right, Rotating)
    private final Vector2 ballPos = new Vector2(-100, 50); // Start off-screen left, y=50
    private float ballRotation = 0;
    private float waveTimer = 0;
    
    // Wave Diagonal Logic
    // Represents moving UP (holding) or DOWN (release) diagonally
    private boolean waveGoingUp = true; 
    private float waveVelocityY = 300f; // Speed vertical

    public MenuBackgroundAnimation(Main game) {
        this.game = game;
        this.shapeRenderer = new ShapeRenderer();
        loadTextures();
        initPositions();
    }

    private void loadTextures() {
        bgTexture = new Texture("background/MainBackground.png");
        cubeTexture = new Texture("player/player_cube.png");
        robotTexture = new Texture("player/player_robot.png");
        ufoTexture = new Texture("player/player_ufo.png");
        shipTexture = new Texture("player/player_ship.png");
        waveTexture = new Texture("player/player_wave.png");
        ballTexture = new Texture("player/player_ball.png");
        
        // Spider Animation
        spiderTex1 = new Texture("player/player_spider1.png");
        spiderTex2 = new Texture("player/player_spider2.png");
        spiderTex3 = new Texture("player/player_spider3.png");
        
        TextureRegion[] frames = new TextureRegion[3];
        frames[0] = new TextureRegion(spiderTex1);
        frames[1] = new TextureRegion(spiderTex2);
        frames[2] = new TextureRegion(spiderTex3);
        spiderAnim = new Animation<>(0.1f, frames);
        spiderAnim.setPlayMode(Animation.PlayMode.LOOP);
    }

    private void initPositions() {
        spiderStartX = spiderPos.x;
        cubeStartY = cubePos.y;
    }

    public void render(float delta, SpriteBatch batch) {
        stateTime += delta;
        update(delta);
        
        // 1. Draw Background Image (Moved down 60px + 20px = 80px)
        // Check transparent gap at top (MenuScreen clear color is black so it fits)
        float bgY = -80;
        
        batch.begin();
        batch.draw(bgTexture, 0, bgY, Constants.V_WIDTH, Constants.V_HEIGHT);
        batch.end();

        // 2. Draw Wave Trail (Neon Effect x2 Thickness)
        drawWaveTrail(batch);

        // 3. Draw Characters (Scale x2)
        batch.begin();
        
        // Spider (Animated, Scaled x2)
        // Original visual width ~64 -> New ~128.
        TextureRegion currentSpiderFrame = spiderAnim.getKeyFrame(stateTime, true);
        float spiderWidth = 128;
        float spiderRatio = (float) currentSpiderFrame.getRegionHeight() / currentSpiderFrame.getRegionWidth();
        float spiderHeight = spiderWidth * spiderRatio;
        batch.draw(currentSpiderFrame, spiderPos.x, spiderPos.y, spiderWidth, spiderHeight);

        // Robot (Scale x2 -> 128x128)
        batch.draw(robotTexture, robotPos.x, robotPos.y, 128, 128);

        // UFO (Scale x2 -> 140x140)
        batch.draw(ufoTexture, ufoPos.x, ufoPos.y, 140, 140);

        // Ship (Scale x2 -> 160x100)
        batch.draw(shipTexture, shipPos.x, shipPos.y, 160, 100);

        // Wave Head (Scale x2 -> 64x64)
        // Rotate 45 deg based on direction
        float waveRotation = waveGoingUp ? 45 : -45;
        // Origin needs to be center for rotation
        batch.draw(waveTexture, wavePos.x, wavePos.y, 32, 32, 64, 64, 1, 1, waveRotation, 0, 0, waveTexture.getWidth(), waveTexture.getHeight(), false, false);

        // Cube (Scale x2 = 256. x0.7 of 256 is ~180). 
        batch.draw(cubeTexture, cubePos.x, cubePos.y, 180, 180);

        // 7. Ball (Rotating)
        // Scaled x2 approx -> 64x64 or 80x80? Standard is usually 32-40. Let's try 80x80.
        // Origin center for rotation
        batch.draw(ballTexture, ballPos.x, ballPos.y, 40, 40, 80, 80, 1, 1, ballRotation, 0, 0, ballTexture.getWidth(), ballTexture.getHeight(), false, false);

        batch.end();
    }

    private void drawWaveTrail(SpriteBatch batch) {
        if (waveTrail.size < 2) return;

        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Layer 1: Outer Glow (Cyan/Blue) - Thicker x2
        shapeRenderer.setColor(0f, 1f, 1f, 0.4f);
        for (int i = 0; i < waveTrail.size - 1; i++) {
            Vector2 p1 = waveTrail.get(i);
            Vector2 p2 = waveTrail.get(i+1);
            // Offset +32 for center of 64px sprite
            shapeRenderer.rectLine(p1.x + 32, p1.y + 32, p2.x + 32, p2.y + 32, 16); 
        }

        // Layer 2: Inner Core (White) - Thicker x2
        shapeRenderer.setColor(1f, 1f, 1f, 0.8f);
        for (int i = 0; i < waveTrail.size - 1; i++) {
            Vector2 p1 = waveTrail.get(i);
            Vector2 p2 = waveTrail.get(i+1);
            shapeRenderer.rectLine(p1.x + 32, p1.y + 32, p2.x + 32, p2.y + 32, 6);
        }

        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    private void update(float delta) {
        // Spider
        float spiderSpeed = 50 * delta;
        if (spiderMovingRight) {
            spiderPos.x += spiderSpeed;
            if (spiderPos.x > spiderStartX + 100) spiderMovingRight = false;
        } else {
            spiderPos.x -= spiderSpeed;
            if (spiderPos.x < spiderStartX) spiderMovingRight = true;
        }

        // Cube
        cubePos.y = cubeStartY + MathUtils.sin(stateTime * 2) * 20;

        // UFO
        ufoPos.x = ufoStartPos.x - 100 + MathUtils.cos(stateTime) * 100;
        ufoPos.y = ufoStartPos.y - 50 + MathUtils.sin(stateTime) * 50;

        // Ship
        shipPos.x = shipStartPos.x + 100 + MathUtils.cos(stateTime * 1.5f) * 100;
        shipPos.y = shipStartPos.y - 50 + MathUtils.sin(stateTime * 1.5f) * 50;

        // 7. Ball Logic
        float ballSpeed = 150f;
        ballPos.x += ballSpeed * delta;
        ballRotation -= ballSpeed * delta * 2; // Rotate clockwise (negative)
        
        if (ballPos.x > Constants.V_WIDTH + 100) {
            ballPos.x = -100;
        }

        // Wave Diagonal Logic (ZigZag)
        // Always move Right
        wavePos.x += 200 * delta; 
        
        // Vertical movement
        float waveSpeedV = waveVelocityY * delta;
        if (waveGoingUp) {
            wavePos.y += waveSpeedV;
            // Switch direction if too high
            if (wavePos.y > Constants.V_HEIGHT * 0.9f) waveGoingUp = false;
        } else {
            wavePos.y -= waveSpeedV;
            // Switch direction if too low
            if (wavePos.y < Constants.V_HEIGHT * 0.6f) waveGoingUp = true;
        }

        // Trail Update
        waveTimer += delta;
        if (waveTimer > 0.05f) {
            waveTrail.add(new Vector2(wavePos.x, wavePos.y));
            if (waveTrail.size > 20) waveTrail.removeIndex(0);
            waveTimer = 0;
        }

        // Loop Wave
        if (wavePos.x > Constants.V_WIDTH + 100) {
            wavePos.x = -100; // Reset left
            waveTrail.clear();
        }
    }

    @Override
    public void dispose() {
        if (bgTexture != null) bgTexture.dispose();
        if (cubeTexture != null) cubeTexture.dispose();
        if (robotTexture != null) robotTexture.dispose();
        if (ufoTexture != null) ufoTexture.dispose();
        if (shipTexture != null) shipTexture.dispose();
        if (waveTexture != null) waveTexture.dispose();
        
        if (spiderTex1 != null) spiderTex1.dispose();
        if (spiderTex2 != null) spiderTex2.dispose();
        if (spiderTex3 != null) spiderTex3.dispose();

        if (shapeRenderer != null) shapeRenderer.dispose();
    }
}
