package com.EinsteinDash.frontend.input;

import com.EinsteinDash.frontend.utils.Player;

/**
 * JumpCommand - Perintah untuk melompat.
 * Mendelegasikan ke Player.jump() yang akan diteruskan ke MovementStrategy.
 */
public class JumpCommand implements Command {
    @Override
    public void execute(Player player) {
        player.jump();
    }
}
