package com.zerofall.ezstorage.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import com.zerofall.ezstorage.gui.client.GuiCraftingCore;
import com.zerofall.ezstorage.gui.client.GuiSecurityBox;
import com.zerofall.ezstorage.gui.client.GuiStorageCore;
import com.zerofall.ezstorage.gui.server.ContainerSecurityBox;
import com.zerofall.ezstorage.gui.server.ContainerStorageCore;
import com.zerofall.ezstorage.gui.server.ContainerStorageCoreCrafting;
import com.zerofall.ezstorage.tileentity.TileEntitySecurityBox;

/** The mod gui handler */
public class GuiHandler implements IGuiHandler {
	
	public static final int STORAGE = 1;
	public static final int CRAFTING  = 2;
	public static final int SECURITY = 3;

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));
        if(tileEntity != null) {
        	if (ID == STORAGE) {
        		return new ContainerStorageCore(player, world, x, y, z);
        	} else if (ID == CRAFTING) {
        		return new ContainerStorageCoreCrafting(player, world, x, y, z);
        	} else if (ID == SECURITY) {
        		return new ContainerSecurityBox(player.inventory, (TileEntitySecurityBox)tileEntity);
        	}
        }
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));
        if(tileEntity != null) {
        	if (ID == STORAGE) {
        		return new GuiStorageCore(player, world, x, y, z);
        	} else if (ID == CRAFTING) {
        		return new GuiCraftingCore(player, world, x, y, z);
        	} else if (ID == SECURITY) {
        		return new GuiSecurityBox(player.inventory, (TileEntitySecurityBox)tileEntity, new BlockPos(x, y, z));
        	}
        }
		return null;
	}

}
