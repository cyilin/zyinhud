package com.zyin.zyinhud.keyhandlers;

import com.zyin.zyinhud.mods.WeaponSwapper;

import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;

/**
 * The type Weapon swapper key handler.
 */
public class WeaponSwapperKeyHandler implements ZyinHUDKeyHandlerBase
{
    /**
     * The constant HotkeyDescription.
     */
    public static final String HotkeyDescription = "key.zyinhud.weaponswapper";

    /**
     * Pressed.
     *
     * @param event the event
     */
    public static void Pressed(KeyInputEvent event) {
        if (mc.currentScreen != null)
        {
            return;    //don't activate if the user is looking at a GUI
        }

        if (WeaponSwapper.Enabled)
            WeaponSwapper.SwapWeapons();
	}
}