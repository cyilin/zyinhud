package com.zyin.zyinhud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;

/**
 * The type Zyin hud sound.
 */
public class ZyinHUDSound
{
	private static final Minecraft mc = Minecraft.getMinecraft();

	/**
	 * Plays a zyinhud sound with the given resource name.
	 *
	 * @param name the name
	 */
	public static void PlaySound(String name)
	{
		mc.getSoundHandler().playSound(new PositionedSoundRecord(new ResourceLocation("zyinhud:" + name), SoundCategory.MASTER, 0.25F, 1.0F, false, 0, ISound.AttenuationType.NONE, 0.0F, 0.0F, 0.0F));
		//PositionedSoundRecord.create(new ResourceLocation("zyinhud:" + name), 1.0F));
		//new PositionedSoundRecord(soundResource, 0.25F, pitch, false, 0, ISound.AttenuationType.NONE, 0.0F, 0.0F, 0.0F);
	}

	/**
	 * Plays a zyinhud sound with the given resource name the specified volume
	 *
	 * @param name   the name
	 * @param volume 0-100% (0.0F to 1.0F) cannot go above 100%
	 */
	public static void PlaySound(String name, float volume)
	{
		mc.getSoundHandler().playSound(new PositionedSoundRecord(new ResourceLocation("zyinhud:" + name), SoundCategory.MASTER, volume, 1.0F, false, 0, ISound.AttenuationType.LINEAR, (float) mc.thePlayer.posX, (float) mc.thePlayer.posY, (float) mc.thePlayer.posZ));
	}

	/**
	 * Plays the sound that a GuiButton makes.
	 */
	public static void PlayButtonPress()
	{
		mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
	}

	/**
	 * Plays the "plop" sound that a chicken makes when laying an egg.
	 */
	public static void PlayPlopSound()
	{
		mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.ENTITY_CHICKEN_EGG , 1.0F));
	}
}
