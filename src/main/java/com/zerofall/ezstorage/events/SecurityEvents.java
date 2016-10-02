package com.zerofall.ezstorage.events;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.zerofall.ezstorage.block.StorageMultiblock;
import com.zerofall.ezstorage.tileentity.TileEntitySecurityBox;
import com.zerofall.ezstorage.util.BlockRef;
import com.zerofall.ezstorage.util.EZStorageUtils;

/** Security event handling */
public class SecurityEvents {

	/** Right click event */
	@SubscribeEvent
	public void onRightClickSystem(RightClickBlock e) {
		securityCanceler(e);
	}

	/** Left click event */
	@SubscribeEvent
	public void onRightClickSystem(LeftClickBlock e) {
		securityCanceler(e);
	}

	/** This method will cancel unauthorized access to the storage system */
	private void securityCanceler(PlayerInteractEvent e) {
		World w = e.getWorld();
		IBlockState state = w.getBlockState(e.getPos());
		Block b = state.getBlock();
		TileEntitySecurityBox tile;
		EntityPlayer p = e.getEntityPlayer();

		if (b instanceof StorageMultiblock) {
			if ((tile = EZStorageUtils.findSecurityBox(new BlockRef(b, e.getPos().getX(), e.getPos().getY(), e.getPos().getZ()), w, null)) != null) {
				if (!tile.isPlayerAllowed(p))
					e.setCanceled(true); // cancel everything
			}
		}
	}

	/** Block placed */
	@SubscribeEvent
	public void onBlockPlaced(PlaceEvent e) {
		List<BlockRef> blocks = EZStorageUtils.getNeighbors(e.getPos().getX(), e.getPos().getY(), e.getPos().getZ(), e.getWorld());
		TileEntitySecurityBox tile;
		for (BlockRef b : blocks) {
			if (b.block instanceof StorageMultiblock) {
				if ((tile = EZStorageUtils.findSecurityBox(new BlockRef(b.block, b.pos.getX(), b.pos.getY(), b.pos.getZ()), e.getWorld(),
						null)) != null) {
					if (!tile.isPlayerAllowed(e.getPlayer()))
						e.setCanceled(true); // cancel everything
				}
			}
		}
	}

}
