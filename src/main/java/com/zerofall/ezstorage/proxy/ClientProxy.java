package com.zerofall.ezstorage.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;

import com.zerofall.ezstorage.gui.client.GuiStorageCore;
import com.zerofall.ezstorage.init.EZBlocks;
import com.zerofall.ezstorage.init.EZItems;

/** The mod's client proxy */
public class ClientProxy extends CommonProxy {
	
	@Override
	public void registerRenders() {
		EZBlocks.registerRenders();
		EZItems.registerRenders();
	}
	
	@Override
	public EntityPlayer getClientPlayer() {
		return Minecraft.getMinecraft().thePlayer;
	}
	
}
