package com.zerofall.ezstorage.proxy;

import net.minecraft.entity.player.EntityPlayer;

/** The mod's shared proxy */
public class CommonProxy {

	/** Register stuff related to rendering */
	public void registerRenders() {}

	/** Gets the client player clientside, or null serverside */
	public EntityPlayer getClientPlayer() {
		return null;
	}

	/** Marks the system as needing a filter update clientside */
	public void markGuiDirty() {}
}
