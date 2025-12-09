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

        if (isPlayer(fixA) || isPlayer(fixB)) {
            Fixture playerFix = isPlayer(fixA) ? fixA : fixB;
            Fixture otherFix = isPlayer(fixA) ? fixB : fixA;

            Object playerBodyData = playerFix.getBody().getUserData();
            Object otherBodyData = otherFix.getBody().getUserData();
            Object otherFixtureData = otherFix.getUserData();

            // 1. OBJECTS (Portal & Coin)
            if (otherBodyData instanceof Portal) {
                Portal portal = (Portal) otherBodyData;
                if (playerBodyData instanceof Player) {
                    Player player = (Player) playerBodyData;
                    if ("PORTAL_SHIP".equals(portal.getType())) {
                        player.setStrategy(new ShipStrategy());
                    } else if ("PORTAL_CUBE".equals(portal.getType())) {
                        player.setStrategy(new CubeStrategy());
                    }
                }
            }
            else if (otherBodyData instanceof Coin) {
                Coin coin = (Coin) otherBodyData;
                if (!coin.isCollected()) {
                    coin.collect();
                    for(GameObserver o : observers) o.onCoinCollected();
                }
            }

            // 2. BLOCKS & GROUND (Logic Deteksi Tanah & Mati)
            else if (otherFixtureData != null) {
                String type = otherFixtureData.toString();

                if (type.equals("SPIKE")) {
                    notifyPlayerDied();
                }
                else if (type.equals("BLOCK") || type.equals("FLOOR")) {
                    // Logic Deteksi Tabrakan
                    // Apakah ini lantai (aman) atau tembok (mati)?

                    boolean isSafeLanding = true;

                    // Khusus BLOCK, kita cek tabrakan samping
                    if (type.equals("BLOCK")) {
                        Body playerBody = playerFix.getBody();
                        Body blockBody = otherFix.getBody();

                        float playerBottom = playerBody.getPosition().y - (15 / Constants.PPM);
                        float blockTop = blockBody.getPosition().y + (16 / Constants.PPM);
                        float tolerance = 0.05f;

                        // Jika kaki player DI BAWAH permukaan balok, berarti nabrak samping/bawah
                        if (playerBottom < blockTop - tolerance) {
                            isSafeLanding = false;
                            System.out.println("CRASHED WALL!");
                            notifyPlayerDied();
                        }
                    }

                    // Jika Aman (Mendarat di atas), tambahkan sensor kaki
                    if (isSafeLanding && playerBodyData instanceof Player) {
                        ((Player) playerBodyData).addFootContact();
                    }
                }
                else if (type.equals("GOAL")) {
                    notifyLevelCompleted();
                }
            }
        }
    }

    @Override
    public void endContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        if (isPlayer(fixA) || isPlayer(fixB)) {
            Fixture playerFix = isPlayer(fixA) ? fixA : fixB;
            Fixture otherFix = isPlayer(fixA) ? fixB : fixA;

            Object playerBodyData = playerFix.getBody().getUserData();
            Object otherFixtureData = otherFix.getUserData();

            // Saat meninggalkan tanah/blok, kurangi sensor kaki
            if (playerBodyData instanceof Player && otherFixtureData != null) {
                String type = otherFixtureData.toString();
                if (type.equals("BLOCK") || type.equals("FLOOR")) {
                    ((Player) playerBodyData).removeFootContact();
                }
            }
        }
    }

    private boolean isPlayer(Fixture fix) {
        return fix.getUserData() != null && fix.getUserData().equals("PLAYER");
    }
    private void notifyPlayerDied() { for (GameObserver o : observers) o.onPlayerDied(); }
    private void notifyLevelCompleted() { for (GameObserver o : observers) o.onLevelCompleted(); }
    @Override public void preSolve(Contact contact, Manifold oldManifold) {}
    @Override public void postSolve(Contact contact, ContactImpulse impulse) {}
}
