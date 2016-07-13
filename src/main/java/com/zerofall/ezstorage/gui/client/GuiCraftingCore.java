package com.zerofall.ezstorage.gui.client;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import com.zerofall.ezstorage.gui.server.ContainerStorageCoreCrafting;
import com.zerofall.ezstorage.ref.RefStrings;

public class GuiCraftingCore extends GuiStorageCore {

	public GuiCraftingCore(EntityPlayer player, World world, int x, int y, int z) {
		super(new ContainerStorageCoreCrafting(player, world, x, y, z), world, x, y, z);
		this.xSize = 195;
		this.ySize = 244;
	}
	
	@Override
    public void initGui() {
		super.initGui();
		craftClear.visible = true;
	}

	@Override
	public int rowsVisible() {
		return 4;
	}
	
	@Override
	protected ResourceLocation getBackground() {
		return new ResourceLocation(RefStrings.MODID + ":textures/gui/storageCraftingGui.png");
	}
}
