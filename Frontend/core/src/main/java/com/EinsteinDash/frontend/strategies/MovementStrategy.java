package com.EinsteinDash.frontend.strategies;

import com.EinsteinDash.frontend.utils.Player;

/**
 * MovementStrategy - Interface untuk berbagai mode gerakan player.
 * Pattern: Strategy - memungkinkan pergantian behavior tanpa mengubah Player.
 */
public interface MovementStrategy {

    /**
     * Update fisika dan state setiap frame.
     * @param player Player yang dikontrol
     * @param dt Delta time (waktu sejak frame terakhir)
     */
    void update(Player player, float dt);

    /**
     * Handle input dari pemain (lompat/aksi).
     * @param player Player yang dikontrol
     */
    void handleInput(Player player);
}
