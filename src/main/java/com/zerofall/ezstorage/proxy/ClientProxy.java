package com.zerofall.ezstorage.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

import com.zerofall.ezstorage.init.EZBlocks;
import com.zerofall.ezstorage.init.EZItems;

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
