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
        // Cek apakah tombol Spasi / Mouse SEDANG DITEKAN (Continuous)
        boolean isPressed = Gdx.input.isKeyPressed(Input.Keys.SPACE) || Gdx.input.isTouched();

        // Cek apakah BARU SAJA ditekan (One shot)
        boolean isJustPressed = Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.justTouched();

        // KITA KIRIM SINYAL KE PLAYER
        // Tapi Player tidak tahu dia sedang mode apa.
        // Jadi kita panggil jump() terus menerus jika ditekan.
        // Biar Strategy yang memutuskan:
        // - CubeStrategy: Cek velocity Y (kalau di udara, abaikan perintah jump ini).
        // - ShipStrategy: Terima perintah jump ini sebagai "Gas".

        if (isPressed) {
            // Kita kirim perintah execute terus menerus selama tombol ditekan
            jumpCommand.execute(player);
        }
    }
}
