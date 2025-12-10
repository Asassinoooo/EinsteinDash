package com.EinsteinDash.frontend.utils;

import com.EinsteinDash.frontend.objects.Coin;
import com.EinsteinDash.frontend.objects.Portal;
import com.EinsteinDash.frontend.strategies.*;
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
                    } else if ("PORTAL_BALL".equals(portal.getType())) {
                        player.setStrategy(new BallStrategy());
                    } else if ("PORTAL_UFO".equals(portal.getType())) {
                        player.setStrategy(new UfoStrategy());
                    } else if ("PORTAL_WAVE".equals(portal.getType())) {
                        player.setStrategy(new WaveStrategy());
                    } else if ("PORTAL_ROBOT".equals(portal.getType())) {
                        player.setStrategy(new RobotStrategy());
                    } else if ("PORTAL_SPIDER".equals(portal.getType())) {
                        player.setStrategy(new SpiderStrategy());
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
                else if (type.equals("BLOCK") || type.equals("FLOOR") || type.equals("CEILING")) {
                    // Logic Deteksi Tabrakan
                    // Apakah ini lantai (aman) atau tembok (mati)?

                    boolean isSafeLanding = true;

                    if (type.equals("BLOCK") || type.equals("FLOOR")) {
                        if (playerBodyData instanceof Player) {
                            Player player = (Player) playerBodyData;
                            if (player.getMovementStrategy() instanceof RobotStrategy) {
                                RobotStrategy robotStrategy = ((RobotStrategy) player.getMovementStrategy());
                                robotStrategy.setJumpTimer(0f);
                            }
                        }
                    }

                    if (type.equals("BLOCK")) {
                        // Pastikan data yang kita pegang adalah Player
                        if (playerBodyData instanceof Player) {
                            Player player = (Player) playerBodyData;

                            // --- MODIFIKASI: HANYA CEK DINDING JIKA MODE CUBE ---
                            if (player.getMovementStrategy() instanceof CubeStrategy || player.getMovementStrategy() instanceof RobotStrategy) {

                                Body playerBody = playerFix.getBody();
                                Body blockBody = otherFix.getBody();

                                float playerBottom = playerBody.getPosition().y - (15 / Constants.PPM);
                                float blockTop = blockBody.getPosition().y + (16 / Constants.PPM);
                                float tolerance = 0.05f;

                                // Jika kaki player DI BAWAH permukaan balok, berarti nabrak samping/bawah
                                if (playerBottom < blockTop - tolerance) {
                                    isSafeLanding = false;
                                    System.out.println("CRASHED WALL (Cube Mode)!");
                                    notifyPlayerDied();
                                }
                            }
                            else if (player.getMovementStrategy() instanceof WaveStrategy) {
                                notifyPlayerDied();
                                return; // Stop logic

                                // Jika FLOOR (tanah dasar/langit batas world), wave mati juga biasanya
                                // Tapi kalau FLOOR itu batas level aman, biarkan.
                                // Asumsi: BLOCK = Obstacle, FLOOR = Safe Border.
                                // Jika Anda ingin Wave mati kena lantai dasar juga, uncomment notifyPlayerDied di bawah.
                                // notifyPlayerDied();
                            }
                            else if (player.getMovementStrategy() instanceof BallStrategy ||
                                player.getMovementStrategy() instanceof ShipStrategy ||
                                player.getMovementStrategy() instanceof SpiderStrategy ||
                                player.getMovementStrategy() instanceof UfoStrategy) {
                                if (checkSideCollision(playerFix.getBody(), otherFix.getBody())) {
                                    System.out.println("CRASH SIDE! (Mode: " + player.getMovementStrategy().getClass().getSimpleName() + ")");
                                    notifyPlayerDied();
                                } else {
                                    // Jika tidak mati (berarti kena atas/bawah), tambahkan kontak kaki
                                    player.addFootContact();
                                }
                            }
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
                if (type.equals("BLOCK") || type.equals("FLOOR") || type.equals("CEILING")) {
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

    private boolean checkSideCollision(Body playerBody, Body blockBody) {
        float tolerance = 0.05f; // Toleransi kecil

        // Hitung batas vertikal
        float playerBottom = playerBody.getPosition().y - (14 / Constants.PPM);
        float playerTop = playerBody.getPosition().y + (14 / Constants.PPM);

        float blockBottom = blockBody.getPosition().y - (16 / Constants.PPM);
        float blockTop = blockBody.getPosition().y + (16 / Constants.PPM);

        // LOGIKA SAMPING:
        // Tabrakan dianggap samping jika posisi vertikal player berada "di dalam" rentang tinggi blok.
        // Artinya: Kaki player di bawah atap blok DAN Kepala player di atas lantai blok.

        boolean isBelowTop = playerBottom < (blockTop - tolerance);
        boolean isAboveBottom = playerTop > (blockBottom + tolerance);

        // Jika kita berada di antara atas dan bawah blok, berarti kita menabrak "daging" blok (samping)
        return isBelowTop && isAboveBottom;
    }
}
