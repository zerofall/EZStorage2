package com.zerofall.ezstorage.proxy;

import com.zerofall.ezstorage.registry.RegistryHelper;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;

/** The mod's shared proxy */
public class CommonProxy {
	
	/** Register the events used to register stuff */
	public void initRegistryEvents() {
		MinecraftForge.EVENT_BUS.register(new RegistryHelper());
	}

	/** Register stuff related to rendering */
	public void registerRenders() {}

	/** Gets the client player clientside, or null serverside */
	public EntityPlayer getClientPlayer() {
		return null;
	}

	/** Marks the system as needing a filter update clientside */
	public void markGuiDirty() {}

}
