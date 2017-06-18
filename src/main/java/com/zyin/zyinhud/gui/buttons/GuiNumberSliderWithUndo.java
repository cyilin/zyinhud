package com.zyin.zyinhud.gui.buttons;

import com.zyin.zyinhud.gui.buttons.GuiNumberSlider.Modes;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraftforge.fml.client.config.GuiUtils;

/**
 * The type Gui number slider with undo.
 */
public class GuiNumberSliderWithUndo extends GuiNumberSlider
{
	/**
	 * The Undo symbol.
	 */
	String undoSymbol = GuiUtils.UNDO_CHAR;
	/**
	 * The Undo symbol x.
	 */
	int undoSymbolX;
	/**
	 * The Undo symbol y.
	 */
	int undoSymbolY;
	/**
	 * The Undo symbol width.
	 */
	int undoSymbolWidth = 5;
	/**
	 * The Undo symbol height.
	 */
	int undoSymbolHeight = 7;

	/**
	 * The Default value.
	 */
	float defaultValue;

	/**
	 * Instantiates a new Gui number slider with undo.
	 *
	 * @param id            the id
	 * @param x             the x
	 * @param y             the y
	 * @param width         the width
	 * @param height        the height
	 * @param displayString the display string
	 * @param minValue      the min value
	 * @param maxValue      the max value
	 * @param currentValue  the current value
	 * @param defaultValue  the default value
	 * @param mode          the mode
	 */
	public GuiNumberSliderWithUndo(int id, int x, int y, int width, int height, String displayString, float minValue, float maxValue, float currentValue, float defaultValue, Modes mode)
	{
		super(id, x, y, width, height, displayString, minValue, maxValue, currentValue, mode);
		this.defaultValue = defaultValue;
	}
	
	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks)
	{
		super.drawButton(mc, mouseX, mouseY, partialTicks);

		int undoSymbolColor = 0xffffff;
    	undoSymbolX = this.x + width - (undoSymbolWidth+1);
    	undoSymbolY =  this.y + height - (undoSymbolHeight+1);
		
		//if mouseovered the undo symbol
		if(IsUndoMouseovered(mouseX, mouseY))
		{
			undoSymbolColor = 0x55ffff;	//0x55ffff is the same as EnumChatFormatting.AQUA
		}
		
		mc.fontRenderer.drawStringWithShadow(undoSymbol, undoSymbolX, undoSymbolY, undoSymbolColor);	//func_175063_a() is drawStringWithShadow()
	}

	/**
	 * Is undo mouseovered boolean.
	 *
	 * @param mouseX the mouse x
	 * @param mouseY the mouse y
	 * @return the boolean
	 */
	protected boolean IsUndoMouseovered(int mouseX, int mouseY)
	{
		return mouseX > undoSymbolX && mouseX < undoSymbolX + undoSymbolWidth
			&& mouseY > undoSymbolY && mouseY < undoSymbolY + undoSymbolHeight;
	}

	/**
	 * Undo button clicked.
	 */
	protected void UndoButtonClicked()
	{
		sliderValue = (defaultValue - minValue) / (maxValue - minValue);
		UpdateLabel();
	}
	
	public boolean mousePressed(Minecraft mc, int mouseX, int mouseY)
    {
		//this is the mousePressed method for GuiButton, we want to skip the dragging behavior of GuiNumberSlider
		//boolean mousePressed = this.enabled && this.visible && mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
		
		//boolean mousePressed = ((GuiButton)this).mousePressed(mc, mouseX, mouseY);
		
        if(this.enabled && this.visible && IsUndoMouseovered(mouseX, mouseY))
        {
        	UndoButtonClicked();
        	return true;
        }
        else
        {
        	return super.mousePressed(mc, mouseX, mouseY);
        }
    }
}
