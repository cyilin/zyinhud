package com.zyin.zyinhud.mods;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.entity.Render;

/**
 * The type Zyin hud mod base.
 */
public abstract class ZyinHUDModBase
{
	/**
	 * The constant mc.
	 */
	protected static final Minecraft mc = Minecraft.getMinecraft();
	/**
	 * The constant itemRenderer.
	 */
	static final RenderItem itemRenderer = mc.getRenderItem();
	
	//We can't move the static variable Enabled to this base mod because then if one mod sets it to false
	//then ALL mods will be set to false
}
