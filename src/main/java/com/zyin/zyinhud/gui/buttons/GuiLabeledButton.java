package com.zyin.zyinhud.gui.buttons;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

/**
 * A normal GuiButton but with label text to the left of the usual button text.
 */
public class GuiLabeledButton extends GuiButton
{
	/**
	 * The Button label.
	 */
	public String buttonLabel = null;

	/**
	 * Instantiates a new Gui labeled button.
	 *
	 * @param buttonId    the button id
	 * @param x           the x
	 * @param y           the y
	 * @param widthIn     the width in
	 * @param heightIn    the height in
	 * @param buttonText  the button text
	 * @param buttonLabel the button label
	 */
	public GuiLabeledButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText, String buttonLabel)
	{
		super(buttonId, x, y, widthIn, heightIn, buttonText);
		this.buttonLabel = buttonLabel;
	}
	
	@Override
	public void func_191745_a(Minecraft mc, int mouseX, int mouseY, float partialTicks)
	{
		super.func_191745_a(mc, mouseX, mouseY, partialTicks);
		
		if(buttonLabel != null)
			mc.fontRenderer.drawStringWithShadow(buttonLabel, this.x + 3, this.y + (height-mc.fontRenderer.FONT_HEIGHT)/2 + 1, 0x55ffffff);	//func_175063_a() is drawStringWithShadow()
	}
}
