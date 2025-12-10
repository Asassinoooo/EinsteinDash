package com.EinsteinDash.frontend.input;

import com.EinsteinDash.frontend.utils.Player;

/**
 * Command - Interface untuk Command Pattern.
 * Memungkinkan input yang dapat di-remap atau di-record.
 */
public interface Command {
    void execute(Player player);
}
