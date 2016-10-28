package com.zyin.zyinhud.mods;

import com.zyin.zyinhud.util.Localization;
import net.minecraft.util.text.TextFormatting;

/**
 * The type Fps.
 */
public class Fps extends ZyinHUDModBase
{

    /**
     * Enables/Disables this Mod
     */
    public static boolean Enabled;

    /**
     * Toggles this Mod on or off
     *
     * @return The state the Mod was changed to
     */
    public static boolean ToggleEnabled()
    {
    	return Enabled = !Enabled;
    }

    /**
     * The constant currentFps.
     */
    public static String currentFps = "0";

    /**
     * Calculate message for info line string.
     *
     * @return the string
     */
    public static String CalculateMessageForInfoLine()
    {
        if (Fps.Enabled)
        {
            currentFps = String.valueOf(mc.getDebugFPS());
            return TextFormatting.WHITE + currentFps + " " + Localization.get("fps.infoline");
        }
        else
        {
            return "";
        }
    }
}
