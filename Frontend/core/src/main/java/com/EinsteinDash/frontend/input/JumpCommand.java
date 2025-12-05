package com.EinsteinDash.frontend.input;

import com.EinsteinDash.frontend.utils.Player;

public class JumpCommand implements Command {
    @Override
    public void execute(Player player) {
        player.jump();
    }
}
