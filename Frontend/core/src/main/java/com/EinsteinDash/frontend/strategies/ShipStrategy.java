package com.EinsteinDash.frontend.strategies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.EinsteinDash.frontend.utils.Player;

public class ShipStrategy implements MovementStrategy {

    @Override
    public void update(Player player, float dt) {
        // 1. Gerak Horizontal Konstan
        if (player.b2body.getLinearVelocity().x <= player.getMovementSpeed()) {
            player.b2body.setLinearVelocity(new Vector2(player.getMovementSpeed(), player.b2body.getLinearVelocity().y));
        }

        // 2. Gravitasi Sedikit Lebih Ringan (Agar melayang enak)
        player.b2body.setGravityScale(0.45f);

        // 3. Batasi Kecepatan Jatuh/Naik (Terminal Velocity) agar tidak terlalu liar
        float velY = player.b2body.getLinearVelocity().y;
        if (velY > 3f) player.b2body.setLinearVelocity(player.getMovementSpeed(), 3f);
        if (velY < -3f) player.b2body.setLinearVelocity(player.getMovementSpeed(), -3f);

        // 4. Logika Rotasi (Mengikuti arah terbang)
        // Rumus: Kecepatan Y * Faktor Pengali
        float targetRotation = velY * 10f;

        // Clamp agar tidak berputar 360 derajat (Maksimal nunduk/ndongak 45 derajat)
        targetRotation = MathUtils.clamp(targetRotation, -45, 45);

        player.setRotation(targetRotation);
    }

    @Override
    public void handleInput(Player player) {
        // Mode Ship butuh input "Continuous" (Ditahan), bukan sekali tekan.
        // Karena Command Pattern kita trigger "Jump" sekali tekan, kita perlu hack sedikit
        // Atau biarkan PlayScreen menangani input hold.

        // PENTING: Untuk ship, kita beri gaya dorong ke atas
        // Gunakan applyForce (bukan Impulse) agar smooth
        player.b2body.applyForceToCenter(0, 9.0f, true);
    }
}
