package com.EinsteinDash.frontend.strategies;

import com.EinsteinDash.frontend.utils.Player;

public interface MovementStrategy {
    // Dipanggil setiap frame untuk update fisika & visual
    void update(Player player, float dt);

    // Dipanggil saat tombol ditekan (Input)
    void handleInput(Player player);
}
