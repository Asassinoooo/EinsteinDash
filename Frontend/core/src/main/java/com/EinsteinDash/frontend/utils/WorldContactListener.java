package com.EinsteinDash.frontend.utils;

import java.util.ArrayList;
import java.util.List;

import com.EinsteinDash.frontend.objects.Coin;
import com.EinsteinDash.frontend.objects.Portal;
import com.EinsteinDash.frontend.strategies.BallStrategy;
import com.EinsteinDash.frontend.strategies.CubeStrategy;
import com.EinsteinDash.frontend.strategies.MovementStrategy;
import com.EinsteinDash.frontend.strategies.RobotStrategy;
import com.EinsteinDash.frontend.strategies.ShipStrategy;
import com.EinsteinDash.frontend.strategies.SpiderStrategy;
import com.EinsteinDash.frontend.strategies.UfoStrategy;
import com.EinsteinDash.frontend.strategies.WaveStrategy;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

/**
 * WorldContactListener - Menangani collision detection Box2D.
 * Menentukan aksi berdasarkan tipe objek yang bertabrakan.
 */
public class WorldContactListener implements ContactListener {

    private List<GameObserver> observers = new ArrayList<>();

    public void addObserver(GameObserver observer) {
        observers.add(observer);
    }

    // ==================== BEGIN CONTACT ====================

    @Override
    public void beginContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        if (!isPlayer(fixA) && !isPlayer(fixB))
            return;

        // Identifikasi fixture player vs other
        Fixture playerFix = isPlayer(fixA) ? fixA : fixB;
        Fixture otherFix = isPlayer(fixA) ? fixB : fixA;

        Object playerData = playerFix.getBody().getUserData();
        Object otherBodyData = otherFix.getBody().getUserData();
        Object otherFixtureData = otherFix.getUserData();

        // Handle Portal collision
        if (otherBodyData instanceof Portal) {
            handlePortalContact((Portal) otherBodyData, playerData);
            return;
        }

        // Handle Coin collision
        if (otherBodyData instanceof Coin) {
            handleCoinContact((Coin) otherBodyData);
            return;
        }

        // Handle environment collision (Block, Floor, Ceiling, Spike, Goal)
        if (otherFixtureData != null) {
            handleEnvironmentContact(otherFixtureData.toString(), playerFix, otherFix, playerData);
        }
    }

    // ==================== CONTACT HANDLERS ====================

    /** Handle portal: ubah mode player */
    private void handlePortalContact(Portal portal, Object playerData) {
        if (!(playerData instanceof Player))
            return;
        Player player = (Player) playerData;

        switch (portal.getType()) {
            case "PORTAL_CUBE":
                player.setStrategy(new CubeStrategy());
                break;
            case "PORTAL_SHIP":
                player.setStrategy(new ShipStrategy());
                break;
            case "PORTAL_BALL":
                player.setStrategy(new BallStrategy());
                break;
            case "PORTAL_UFO":
                player.setStrategy(new UfoStrategy());
                break;
            case "PORTAL_WAVE":
                player.setStrategy(new WaveStrategy());
                break;
            case "PORTAL_ROBOT":
                player.setStrategy(new RobotStrategy());
                break;
            case "PORTAL_SPIDER":
                player.setStrategy(new SpiderStrategy());
                break;

            // GRAVITY HANDLER
            case "PORTAL_GRAVITY_UP":
                player.setGravityReversed(true);
                break;
            case "PORTAL_GRAVITY_DOWN":
                player.setGravityReversed(false);
                break;

            // SPEED HANDLER
            case "PORTAL_SPEED_0_5":
                player.setSpeedMultiplier(Constants.SPEED_HALF);
                break;
            case "PORTAL_SPEED_1":
                player.setSpeedMultiplier(Constants.SPEED_NORMAL);
                break;
            case "PORTAL_SPEED_2":
                player.setSpeedMultiplier(Constants.SPEED_DOUBLE);
                break;
            case "PORTAL_SPEED_3":
                player.setSpeedMultiplier(Constants.SPEED_TRIPLE);
                break;
            case "PORTAL_SPEED_4":
                player.setSpeedMultiplier(Constants.SPEED_QUAD);
                break;
        }
    }

    /** Handle coin: collect dan notify observer */
    private void handleCoinContact(Coin coin) {
        if (!coin.isCollected()) {
            coin.collect();
            for (GameObserver o : observers)
                o.onCoinCollected();
        }
    }

    /** Handle environment collision */
    private void handleEnvironmentContact(String type, Fixture playerFix, Fixture otherFix, Object playerData) {
        switch (type) {
            case "SPIKE":
                notifyPlayerDied();
                break;

            case "GOAL":
                notifyLevelCompleted();
                break;

            case "BLOCK":
            case "FLOOR":
            case "CEILING":
                handleSolidContact(type, playerFix, otherFix, playerData);
                break;
        }
    }

    /** Handle contact dengan benda padat (Block/Floor/Ceiling) */
    private void handleSolidContact(String type, Fixture playerFix, Fixture otherFix, Object playerData) {
        if (!(playerData instanceof Player))
            return;
        Player player = (Player) playerData;
        MovementStrategy strategy = player.getStrategy();

        // Reset jump timer untuk Robot saat menyentuh tanah
        if ((type.equals("BLOCK") || type.equals("FLOOR")) && strategy instanceof RobotStrategy) {
            ((RobotStrategy) strategy).setJumpTimer(0f);
        }

        // Collision detection untuk Block
        if (type.equals("BLOCK")) {
            // --- HANYA CEK DINDING JIKA MODE CUBE atau ROBOT ---
            if (strategy instanceof CubeStrategy || strategy instanceof RobotStrategy) {
                if (checkWallCollision(playerFix.getBody(), otherFix.getBody())) {
                    notifyPlayerDied();
                    return;
                }
                player.addFootContact();
                return;
            }
            // --- WAVE mati kena BLOCK ---
            else if (strategy instanceof WaveStrategy) {
                notifyPlayerDied();
                return;
            }
            // --- Ball, Ship, Spider, UFO: cek side collision ---
            else if (strategy instanceof BallStrategy ||
                    strategy instanceof ShipStrategy ||
                    strategy instanceof SpiderStrategy ||
                    strategy instanceof UfoStrategy) {
                if (checkSideCollision(playerFix.getBody(), otherFix.getBody())) {
                    System.out.println("CRASH SIDE! (Mode: " + strategy.getClass().getSimpleName() + ")");
                    notifyPlayerDied();
                } else {
                    // Jika tidak mati (berarti kena atas/bawah), tambahkan kontak kaki
                    player.addFootContact();
                }
                return;
            }
        }

        // Safe landing
        player.addFootContact();
    }

    // ==================== END CONTACT ====================

    @Override
    public void endContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        if (!isPlayer(fixA) && !isPlayer(fixB))
            return;

        Fixture playerFix = isPlayer(fixA) ? fixA : fixB;
        Fixture otherFix = isPlayer(fixA) ? fixB : fixA;

        Object playerData = playerFix.getBody().getUserData();
        Object otherFixtureData = otherFix.getUserData();

        // Kurangi foot contact saat meninggalkan permukaan
        if (playerData instanceof Player && otherFixtureData != null) {
            String type = otherFixtureData.toString();
            if (type.equals("BLOCK") || type.equals("FLOOR") || type.equals("CEILING")) {
                ((Player) playerData).removeFootContact();
            }
        }
    }

    // ==================== COLLISION HELPERS ====================

    /** Cek apakah player menabrak dinding (bukan mendarat di atas) */
    private boolean checkWallCollision(Body playerBody, Body blockBody) {
        Object userData = playerBody.getUserData();
        boolean isGravityReversed = false;
        if (userData instanceof Player) {
            isGravityReversed = ((Player) userData).isGravityReversed();
        }

        // float playerBottom = playerBody.getPosition().y - (15 / Constants.PPM);
        float playerY = playerBody.getPosition().y;
        float blockY = blockBody.getPosition().y;

        float playerHalfHeight = 15 / Constants.PPM;
        float blockHalfHeight = 16 / Constants.PPM;
        float tolerance = 0.05f;

        if (!isGravityReversed) {
            // GRAVITASI NORMAL (Jatuh ke bawah)
            // Aman jika: Player Bottom >= Block Top (Mendarat di atas)
            // Mati jika: Player Bottom < Block Top (Nabrak samping/bawah)
            float playerBottom = playerY - playerHalfHeight;
            float blockTop = blockY + blockHalfHeight;
            return playerBottom < blockTop - tolerance;
        } else {
            // GRAVITASI TERBALIK (Jatuh ke atas)
            // Aman jika: Player Top <= Block Bottom (Mendarat di bawah block/ceiling)
            // Mati jika: Player Top > Block Bottom (Nabrak samping/atas)
            float playerTop = playerY + playerHalfHeight;
            float blockBottom = blockY - blockHalfHeight;
            return playerTop > blockBottom + tolerance;
        }
    }

    /** Cek tabrakan samping (untuk mode Ball/Ship/UFO/Spider) */
    private boolean checkSideCollision(Body playerBody, Body blockBody) {
        float tolerance = 0.05f;

        float playerBottom = playerBody.getPosition().y - (14 / Constants.PPM);
        float playerTop = playerBody.getPosition().y + (14 / Constants.PPM);
        float blockBottom = blockBody.getPosition().y - (16 / Constants.PPM);
        float blockTop = blockBody.getPosition().y + (16 / Constants.PPM);

        // Side collision: player berada di antara top dan bottom block
        boolean isBelowTop = playerBottom < (blockTop - tolerance);
        boolean isAboveBottom = playerTop > (blockBottom + tolerance);

        return isBelowTop && isAboveBottom;
    }

    // ==================== UTILITIES ====================

    private boolean isPlayer(Fixture fix) {
        return fix.getUserData() != null && fix.getUserData().equals("PLAYER");
    }

    private void notifyPlayerDied() {
        for (GameObserver o : observers)
            o.onPlayerDied();
    }

    private void notifyLevelCompleted() {
        for (GameObserver o : observers)
            o.onLevelCompleted();
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
    }
}
