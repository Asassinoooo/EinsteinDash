package com.EinsteinDash.frontend.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.EinsteinDash.frontend.utils.Player;

public class InputHandler {
    private Command jumpCommand;

    public InputHandler() {
        jumpCommand = new JumpCommand();
    }

    public void handleInput(Player player) {
        // Jika tekan Spasi atau Klik Mouse/Layar
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.justTouched()) {
            jumpCommand.execute(player);
        }
    }
}
