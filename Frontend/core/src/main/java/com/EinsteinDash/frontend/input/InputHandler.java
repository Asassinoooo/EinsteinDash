package com.EinsteinDash.frontend.input;

import com.EinsteinDash.frontend.utils.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

/**
 * InputHandler - Menangani input dari keyboard/touch dan meneruskan ke Player.
 * Pattern: Command - memisahkan input detection dari action execution.
 */
public class InputHandler {

    private Command jumpCommand;

    public InputHandler() {
        jumpCommand = new JumpCommand();
    }

    /**
     * Cek input dan eksekusi command yang sesuai.
     * Dipanggil setiap frame dari PlayScreen.update().
     */
    public void handleInput(Player player) {
        // Cek apakah tombol sedang ditekan (continuous untuk Ship/Wave)
        boolean isPressed = Gdx.input.isKeyPressed(Input.Keys.SPACE) || Gdx.input.isTouched();

        // Kirim ke player setiap frame saat ditekan
        // MovementStrategy yang menentukan aksi berdasarkan mode
        if (isPressed) {
            jumpCommand.execute(player);
        }
    }
}
