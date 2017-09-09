package com.zyin.zyinhud.mods;

import com.zyin.zyinhud.util.Localization;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.opengl.GL11;

/**
 * Calculates time.
 *
 * @See {@link "http://www.minecraftwiki.net/wiki/Day-night_cycle"}
 */
public class Clock extends ZyinHUDModBase
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
	public static boolean ToggleEnabled() {
		return Enabled = !Enabled;
	}

	/**
	 * The current mode for this mod
	 */
	public static Modes Mode;

	/**
	 * The enum for the different types of Modes this mod can have
	 */
	public static enum Modes {
		/**
		 * Standard modes.
		 */
		STANDARD(Localization.get("clock.mode.standard")),
		/**
		 * Countdown modes.
		 */
		COUNTDOWN(Localization.get("clock.mode.countdown")),
		/**
		 * Graphic modes.
		 */
		GRAPHIC(Localization.get("clock.mode.graphic"));

		private String friendlyName;

		private Modes(String friendlyName) {
			this.friendlyName = friendlyName;
		}

		/**
		 * Sets the next availble mode for this mod
		 *
		 * @return the modes
		 */
		public static Modes ToggleMode() {
			return ToggleMode(true);
		}

		/**
		 * Sets the next availble mode for this mod if forward=true, or previous mode if false
		 *
		 * @param forward the forward
		 * @return the modes
		 */
		public static Modes ToggleMode(boolean forward) {
			if (forward)
        		return Mode = Mode.ordinal() < Modes.values().length - 1 ? Modes.values()[Mode.ordinal() + 1] : Modes.values()[0];
        	else
        		return Mode = Mode.ordinal() > 0 ? Modes.values()[Mode.ordinal() - 1] : Modes.values()[Modes.values().length - 1];
		}

		/**
		 * Gets the mode based on its internal name as written in the enum declaration
		 *
		 * @param modeName the mode name
		 * @return modes
		 */
		public static Modes GetMode(String modeName) {
			try {
				return Modes.valueOf(modeName);
			} catch (IllegalArgumentException e) {
				return values()[1];
			}
		}

		/**
		 * Get friendly name string.
		 *
		 * @return the string
		 */
		public String GetFriendlyName() {
			return friendlyName;
        }
    }
	
	private static final long mobSpawningStartTime = 13187;
	
	//mobs stop spawning at: 22813
	//mobs start to burn at: 23600
	private static final long mobSpawningStopTime = 23600;
	
	//mc.thePlayer.isDaytime() is not available on client, which is actually used to determine bedtime
	private static final long bedTime = 12540;

	/**
	 * Calculates time
	 *
	 * @param infoLineMessageUpToThisPoint the info line message up to this point
	 * @return time if the Clock is enabled, otherwise "".
	 */
	public static String CalculateMessageForInfoLine(String infoLineMessageUpToThisPoint) {
		if (Clock.Enabled)
        {
			if (Clock.Mode == Modes.STANDARD) {
				long time = (mc.world.getWorldTime()) % 24000;

				//0 game time is 6am, so add 6000
				long hours = (time + 6000) / 1000;
				if (hours >= 24) {
					hours = hours - 24;
				}
				long seconds = (long) (((time + 6000) % 1000) * (60.0 / 1000.0));

				if (IsNight()) {
					//night time
					return TextFormatting.GRAY + String.format("%02d", hours) + ":" + String.format("%02d", seconds);
				} else
        		{
        			//day time
                    String daytimeClockString = String.format("%02d", hours) + ":" + String.format("%02d", seconds);

					if (time < bedTime)
						daytimeClockString = TextFormatting.YELLOW + daytimeClockString;
					else
						daytimeClockString = TextFormatting.GOLD + daytimeClockString;

					return daytimeClockString;
        		}
        	}
        	else if(Clock.Mode == Modes.COUNTDOWN)
        	{
                long time = (mc.world.getWorldTime()) % 24000;

        		if(IsNight())
        		{
        			//night time
        			long secondsTillDay = (mobSpawningStopTime - time) / 20;

					long minutes = secondsTillDay / 60;
					long seconds = secondsTillDay - minutes * 60;

					String nighttimeCountdownString = TextFormatting.GRAY + String.format("%02d", minutes) + ":" + String.format("%02d", seconds);
					return nighttimeCountdownString;
				}
        		else
        		{
        			//day time
        			long secondsTillNight;
        			if(time > mobSpawningStopTime)
        				secondsTillNight = (24000 - time + mobSpawningStartTime) / 20;
	    			else
	    				secondsTillNight = (mobSpawningStartTime - time) / 20;
        			
        			long minutes = secondsTillNight / 60;
        			long seconds = secondsTillNight - minutes*60;

                    String daytimeCountdownString = String.format("%02d", minutes) + ":" + String.format("%02d", seconds);

					if (time < bedTime)
						daytimeCountdownString = TextFormatting.YELLOW + daytimeCountdownString;
					else
						daytimeCountdownString = TextFormatting.GOLD + daytimeCountdownString;

					return daytimeCountdownString;
				}
        	}
        	else if(Clock.Mode == Modes.GRAPHIC)
        	{
        		int infoLineWidth = mc.fontRenderer.getStringWidth(infoLineMessageUpToThisPoint);
        		
        		itemRenderer.renderItemAndEffectIntoGUI(new ItemStack(Items.CLOCK), infoLineWidth + InfoLine.infoLineLocX, InfoLine.infoLineLocY);
        		
        		GL11.glDisable(GL11.GL_LIGHTING);	//this is needed because the RenderItem.renderItem() methods enable lighting

				return "    ";    //about the length of the clock graphic
			}
		}

		return "";
	}

	/**
	 * Is night boolean.
	 *
	 * @return the boolean
	 */
	public static boolean IsNight() {
		long time = (mc.world.getWorldTime()) % 24000;
    	return time >= mobSpawningStartTime && time < mobSpawningStopTime;
    }
}
