package com.zyin.zyinhud.keyhandlers;

import com.zyin.zyinhud.mods.EnderPearlAid;

import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;

/**
 * The type Ender pearl aid key handler.
 */
public class EnderPearlAidKeyHandler implements ZyinHUDKeyHandlerBase
{
    /**
     * The constant HotkeyDescription.
     */
    public static final String HotkeyDescription = "key.zyinhud.enderpearlaid";

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

		if (EnderPearlAid.Enabled)
            EnderPearlAid.UseEnderPearl();
	   
	}
}