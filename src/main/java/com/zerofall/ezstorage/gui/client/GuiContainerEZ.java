package com.zerofall.ezstorage.gui.client;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;

/** Custom gui container class that draws a darkened background correctly */
public abstract class GuiContainerEZ extends GuiContainer {

	public GuiContainerEZ(Container inventorySlotsIn) {
		super(inventorySlotsIn);
	}
	
	@Override
	protected final void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
		this.drawDefaultBackground();
		drawBackground();
	}
	
	protected abstract void drawBackground();

}
