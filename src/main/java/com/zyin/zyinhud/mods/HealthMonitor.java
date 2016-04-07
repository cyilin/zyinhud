package com.zyin.zyinhud.mods;

import java.util.Timer;
import java.util.TimerTask;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import com.zyin.zyinhud.ZyinHUDSound;
import com.zyin.zyinhud.mods.EatingAid.Modes;
import com.zyin.zyinhud.util.Localization;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

/**
 * Plays a warning sound when the player is low on health.
 */
public class HealthMonitor extends ZyinHUDModBase
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
		 * Oot modes.
		 */
		OOT(Localization.get("healthmonitor.mode.oot"), "lowhealth_OoT"),
		/**
		 * Lttp modes.
		 */
		LTTP(Localization.get("healthmonitor.mode.lttp"), "lowhealth_LttP"),
		/**
		 * Oracle modes.
		 */
		ORACLE(Localization.get("healthmonitor.mode.oracle"), "lowhealth_Oracle"),
		/**
		 * La modes.
		 */
		LA(Localization.get("healthmonitor.mode.la"), "lowhealth_LA"),
		/**
		 * Loz modes.
		 */
		LOZ(Localization.get("healthmonitor.mode.loz"), "lowhealth_LoZ"),
		/**
		 * Aol modes.
		 */
		AOL(Localization.get("healthmonitor.mode.aol"), "lowhealth_AoL");

		private String friendlyName;
		/**
		 * The Sound name.
		 */
		public String soundName;

		private Modes(String friendlyName, String soundName) {
			this.friendlyName = friendlyName;
			this.soundName = soundName;
		}

		/**
		 * Sets the next available mode for this mod
		 *
		 * @return the modes
		 */
		public static Modes ToggleMode() {
			return ToggleMode(true);
		}

		/**
		 * Sets the next available mode for this mod if forward=true, or previous mode if false
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
				return OOT;
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
    
	private static Timer timer = new Timer();

	private static int LowHealthSoundThreshold;
	private static float Volume;
	/**
	 * The constant PlayFasterNearDeath.
	 */
	public static boolean PlayFasterNearDeath;

	private static boolean isPlayingLowHealthSound = false;
	private static int repeatDelay = 1000;

	/**
	 * The constant instance.
	 */
	public static final HealthMonitor instance = new HealthMonitor();

	/**
	 * Instantiates a new Health monitor.
	 */
	public HealthMonitor()
	{
		
	}

	/**
	 * We use a ClientTickEvent instead of a LivingHurtEvent because a LivingHurtEvent will only
	 * fire in single player, whereas a ClientTickEvent fires in both single and multi player.
	 * PlayerTickEvent ticks for every player rendered.
	 * WorldTickEvent doesn't work on servers.
	 *
	 * @param event the event
	 */
	@SubscribeEvent
	public void ClientTickEvent(ClientTickEvent event)
	{
		//only play the sound if it's not playing already
		if(HealthMonitor.Enabled && !isPlayingLowHealthSound)
		{
			PlayLowHealthSoundIfHurt();
		}
	}


	/**
	 * Checks to see if the player has less health than the set threshold, and will play a
	 * warning sound on a 1 second loop until they heal up.
	 */
	protected static void PlayLowHealthSoundIfHurt()
	{
		if(HealthMonitor.Enabled && mc.thePlayer != null)
		{
			int playerHealth = (int)mc.thePlayer.getHealth();
			if(playerHealth < LowHealthSoundThreshold && playerHealth > 0)
			{
				//don't play the sound if the user is looking at a screen or in creative
				if(!mc.playerController.isInCreativeMode() && !mc.isGamePaused())// && mc.inGameHasFocus)
					PlayLowHealthSound();
				
				isPlayingLowHealthSound = true;
				
				int soundDelay = repeatDelay;
				
				if(PlayFasterNearDeath)
					soundDelay = repeatDelay/2 + (int)((float)repeatDelay/2 * ((float)playerHealth / (float)LowHealthSoundThreshold));
				
				TimerTask t = new PlayLowHealthSoundTimerTask();
				timer.schedule(t, soundDelay);
				
				return;
			}
		}
		
		isPlayingLowHealthSound = false;
	}


	/**
	 * Gets the name of the sound resource associated with the current mode.
	 * Sound resouce names are declared in assets/zyinhud/sounds.json.
	 *
	 * @return string
	 */
	protected static String GetSoundNameFromMode()
	{
		return Mode.soundName;
	}

	/**
	 * Plays the low health warning sound right now.
	 */
	public static void PlayLowHealthSound() {
		ZyinHUDSound.PlaySound(GetSoundNameFromMode(), Volume);
	}

	private static class PlayLowHealthSoundTimerTask extends TimerTask {
		/**
		 * Instantiates a new Play low health sound timer task.
		 */
		PlayLowHealthSoundTimerTask() {

		}

		@Override
		public void run() {
			PlayLowHealthSoundIfHurt();
		}
	}

	/**
	 * Set low health sound threshold.
	 *
	 * @param lowHealthSoundThreshold the low health sound threshold
	 */
	public static void SetLowHealthSoundThreshold(int lowHealthSoundThreshold) {
		LowHealthSoundThreshold = MathHelper.clamp_int(lowHealthSoundThreshold, 1, 20);
	}

	/**
	 * Get low health sound threshold int.
	 *
	 * @return the int
	 */
	public static int GetLowHealthSoundThreshold() {
		return LowHealthSoundThreshold;
	}

	/**
	 * Set volume.
	 *
	 * @param volume the volume
	 */
	public static void SetVolume(float volume) {
		Volume = MathHelper.clamp_float(volume, 0, 1);
	}

	/**
	 * Get volume float.
	 *
	 * @return the float
	 */
	public static float GetVolume() {
		return Volume;
	}


	/**
	 * Toggles making the sound play quicker when close to dieing
	 *
	 * @return boolean
	 */
	public static boolean TogglePlayFasterNearDeath() {
		return PlayFasterNearDeath = !PlayFasterNearDeath;
    }
}
