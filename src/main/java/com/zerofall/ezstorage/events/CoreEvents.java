package com.zerofall.ezstorage.events;

import com.zerofall.ezstorage.tileentity.TileEntityStorageCore;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;

/** Main mod events */
public class CoreEvents {

	public static long serverTicks = 0;
	public static long clientTicks = 0;

	/** Make sure that only empty systems can be broken */
	@SubscribeEvent
	public void onBlockBreak(BreakEvent e) {
		if (!e.getWorld().isRemote) {
			TileEntity tileentity = e.getWorld().getTileEntity(e.getPos());
			if (tileentity instanceof TileEntityStorageCore) {
				TileEntityStorageCore core = (TileEntityStorageCore) tileentity;
				if (core.inventory.getTotalCount() > 0) {
					e.setCanceled(true);
				}
			}
		}
	}

	/** Keep track of server ticks */
	@SubscribeEvent
	public void onServerTick(ServerTickEvent e) {
		serverTicks++;
	}

	/** Keep track of client ticks */
	@SubscribeEvent
	public void onClientTick(ClientTickEvent e) {
		clientTicks++;
	}

}
