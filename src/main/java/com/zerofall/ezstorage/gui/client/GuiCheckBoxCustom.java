package com.zerofall.ezstorage.gui.client;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.client.config.GuiCheckBox;
import net.minecraftforge.fml.client.config.GuiUtils;

/** Custom check box without text on the right */
public class GuiCheckBoxCustom extends GuiCheckBox {

	private int boxWidth;
	private String checkStr;

	public GuiCheckBoxCustom(int id, int xPos, int yPos, String checkStr, boolean isChecked) {
		super(id, xPos, yPos, "", isChecked);
		this.boxWidth = 11;
		this.checkStr = checkStr;
	}

	/** Draws this button to the screen. */
	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
		if (this.visible) {
			this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.boxWidth
					&& mouseY < this.y + this.height;
			GuiUtils.drawContinuousTexturedBox(BUTTON_TEXTURES, this.x, this.y, 0, 46, this.boxWidth, this.height, 200, 20, 2, 3, 2,
					2, this.zLevel);
			this.mouseDragged(mc, mouseX, mouseY);
			int color = 14737632;

			if (packedFGColour != 0) {
				color = packedFGColour;
			} else if (!this.enabled) {
				color = 10526880;
			}

			if (this.isChecked())
				this.drawCenteredString(mc.fontRenderer, checkStr, this.x + this.boxWidth / 2 + 1, this.y + 1, 14737632);
		}
	}

}
