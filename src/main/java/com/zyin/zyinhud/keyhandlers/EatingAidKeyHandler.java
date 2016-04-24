package com.zyin.zyinhud.keyhandlers;

import com.zyin.zyinhud.mods.EatingAid;

import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;

/**
 * The type Eating aid key handler.
 */
public class EatingAidKeyHandler implements ZyinHUDKeyHandlerBase
{
    /**
     * The constant HotkeyDescription.
     */
    public static final String HotkeyDescription = "key.zyinhud.eatingaid";

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

		if (EatingAid.Enabled)
            EatingAid.instance.Eat();
	}
}