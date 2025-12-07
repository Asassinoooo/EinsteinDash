package com.EinsteinDash.frontend.utils;

import com.EinsteinDash.frontend.objects.Coin;
import com.EinsteinDash.frontend.objects.Portal;
import com.EinsteinDash.frontend.strategies.CubeStrategy;
import com.EinsteinDash.frontend.strategies.ShipStrategy;
import com.badlogic.gdx.physics.box2d.*;
import java.util.ArrayList;
import java.util.List;

public class WorldContactListener implements ContactListener {

    private List<GameObserver> observers = new ArrayList<>();

    public void addObserver(GameObserver observer) {
        observers.add(observer);
    }

    @Override
    public void beginContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        // Cek apakah salah satu adalah Player
        if (isPlayer(fixA) || isPlayer(fixB)) {
            Fixture playerFix = isPlayer(fixA) ? fixA : fixB;
            Fixture otherFix = isPlayer(fixA) ? fixB : fixA;

            // Ambil Data dari Body (Untuk Object: Player, Coin, Portal)
            Object playerBodyData = playerFix.getBody().getUserData();
            Object otherBodyData = otherFix.getBody().getUserData();

            // Ambil Data dari Fixture (Untuk String: Spike, Block, Goal)
            Object otherFixtureData = otherFix.getUserData();

            // =============================================================
            // 1. LOGIKA OBJECT (Portal & Coin)
            // =============================================================

            // Cek Portal (Ganti Strategy)
            if (otherBodyData instanceof Portal) {
                Portal portal = (Portal) otherBodyData;

                // Kita butuh akses ke object Player untuk ganti strategi
                if (playerBodyData instanceof Player) {
                    Player player = (Player) playerBodyData;

                    if ("PORTAL_SHIP".equals(portal.getType())) {
                        System.out.println("SWITCH TO SHIP!");
                        player.setStrategy(new ShipStrategy());
                    }
                    else if ("PORTAL_CUBE".equals(portal.getType())) {
                        System.out.println("SWITCH TO CUBE!");
                        player.setStrategy(new CubeStrategy());
                    }
                }
            }

            // Cek Coin (Collect)
            else if (otherBodyData instanceof Coin) {
                Coin coin = (Coin) otherBodyData;
                if (!coin.isCollected()) {
                    coin.collect();
                    System.out.println("COIN COLLECTED! +1 Score");
                    for(GameObserver o : observers) {
                        o.onCoinCollected();
                    }
                }
            }

            // =============================================================
            // 2. LOGIKA STRING (Spike, Block, Goal)
            // =============================================================
            else if (otherFixtureData != null) {
                String type = otherFixtureData.toString();

                if (type.equals("SPIKE")) {
                    notifyPlayerDied();
                }
                else if (type.equals("BLOCK")) {
                    // --- LOGIKA DETEKSI TABRAKAN SAMPING (TEMBOK) ---
                    Body playerBody = playerFix.getBody();
                    Body blockBody = otherFix.getBody();

                    // Hitung posisi Y
                    float playerY = playerBody.getPosition().y;
                    float blockY = blockBody.getPosition().y;

                    // Hitung batas kaki player dan batas atas balok
                    // (Asumsi ukuran di LevelFactory & Player adalah 16px dan 15px radius)
                    float playerBottom = playerY - (15 / Constants.PPM);
                    float blockTop = blockY + (16 / Constants.PPM);

                    // Toleransi kecil (0.05f)
                    float tolerance = 0.05f;

                    // Jika kaki player berada di bawah permukaan balok saat menyentuhnya...
                    if (playerBottom < blockTop - tolerance) {
                        System.out.println("CRASHED WALL!");
                        notifyPlayerDied();
                    } else {
                        // Mendarat di atas (Aman)
                    }
                }
                else if (type.equals("GOAL")) {
                    System.out.println("WINNER!");
                    notifyLevelCompleted();
                }
            }
        }
    }

    // Helper untuk cek apakah fixture ini punya tag "PLAYER"
    private boolean isPlayer(Fixture fix) {
        return fix.getUserData() != null && fix.getUserData().equals("PLAYER");
    }

    private void notifyPlayerDied() {
        for (GameObserver o : observers) o.onPlayerDied();
    }

    private void notifyLevelCompleted() {
        for (GameObserver o : observers) o.onLevelCompleted();
    }

    @Override public void endContact(Contact contact) {}
    @Override public void preSolve(Contact contact, Manifold oldManifold) {}
    @Override public void postSolve(Contact contact, ContactImpulse impulse) {}
}
