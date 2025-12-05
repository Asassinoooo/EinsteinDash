package com.EinsteinDash.frontend.utils;

import com.EinsteinDash.frontend.objects.Coin;
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

            if (otherFix.getUserData() != null) {
                String type = otherFix.getUserData().toString();

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

                    // Toleransi kecil (0.02f) untuk menghindari bug saat mendarat pas di ujung
                    float tolerance = 0.05f;

                    // Jika kaki player berada di bawah permukaan balok saat menyentuhnya...
                    if (playerBottom < blockTop - tolerance) {
                        // ...Berarti dia menabrak dinding!
                        System.out.println("CRASHED WALL!"); // Debug log
                        notifyPlayerDied();
                    } else {
                        // Jika tidak, berarti dia mendarat di atas (aman)
                    }
                }
                else if (type.equals("GOAL")) {
                    System.out.println("WINNER!");
                    notifyLevelCompleted();
                }
                else if (type.equals("COIN")) {
                    Object data = otherFix.getBody().getUserData();
                    if (data instanceof Coin) {
                        ((Coin) data).collect();
                        System.out.println("COIN COLLECTED! +1 Score");
                    }
                }
            }
        }
    }

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
