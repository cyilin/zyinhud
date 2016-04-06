package com.zyin.zyinhud.keyhandlers;

import com.zyin.zyinhud.mods.PotionAid;

import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;

/**
 * The type Potion aid key handler.
 */
public class PotionAidKeyHandler implements ZyinHUDKeyHandlerBase
{
    /**
     * The constant HotkeyDescription.
     */
    public static final String HotkeyDescription = "key.zyinhud.potionaid";

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

		if (PotionAid.Enabled)
            PotionAid.instance.Drink();
	}
}