package com.zerofall.ezstorage.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.zerofall.ezstorage.block.BlockSecurityBox;
import com.zerofall.ezstorage.block.StorageMultiblock;
import com.zerofall.ezstorage.tileentity.TileEntitySecurityBox;

public class EZStorageUtils {
	
	public static List<BlockRef> getNeighbors(int xCoord, int yCoord, int zCoord, World world) {
		List<BlockRef> blockList = new ArrayList<BlockRef>();
		blockList.add(new BlockRef(world.getBlockState(new BlockPos(xCoord - 1, yCoord, zCoord)).getBlock(), xCoord - 1, yCoord, zCoord));
		blockList.add(new BlockRef(world.getBlockState(new BlockPos(xCoord + 1, yCoord, zCoord)).getBlock(), xCoord + 1, yCoord, zCoord));
		blockList.add(new BlockRef(world.getBlockState(new BlockPos(xCoord, yCoord - 1, zCoord)).getBlock(), xCoord, yCoord - 1, zCoord));
		blockList.add(new BlockRef(world.getBlockState(new BlockPos(xCoord, yCoord + 1, zCoord)).getBlock(), xCoord, yCoord + 1, zCoord));
		blockList.add(new BlockRef(world.getBlockState(new BlockPos(xCoord, yCoord, zCoord - 1)).getBlock(), xCoord, yCoord, zCoord - 1));
		blockList.add(new BlockRef(world.getBlockState(new BlockPos(xCoord - 1, yCoord, zCoord)).getBlock(), xCoord - 1, yCoord, zCoord));
		blockList.add(new BlockRef(world.getBlockState(new BlockPos(xCoord, yCoord, zCoord + 1)).getBlock(), xCoord, yCoord, zCoord + 1));
		return blockList;
	}
	
	public static void notifyBlockUpdate(TileEntity entity) {
		notifyBlockUpdate(entity.getWorld(), entity.getPos());
	}

	public static void notifyBlockUpdate(World world, BlockPos pos) {
		world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
	}
	
	/** Instead of searching for a core, find a secure block */
	public static TileEntitySecurityBox findSecurityBox(BlockRef br, World world, Set<BlockRef> scanned) {
		if(scanned == null) scanned = new HashSet<BlockRef>();
		List<BlockRef> neighbors = EZStorageUtils.getNeighbors(br.pos.getX(), br.pos.getY(), br.pos.getZ(), world);
		for (BlockRef blockRef : neighbors) {
			if (blockRef.block instanceof StorageMultiblock) {
				if (blockRef.block instanceof BlockSecurityBox) {
					return (TileEntitySecurityBox)world.getTileEntity(blockRef.pos);
				} else {
					if (scanned.add(blockRef) == true) {
						TileEntitySecurityBox entity = findSecurityBox(blockRef, world, scanned);
						if (entity != null) {
							return entity;
						}
					}
				}
			}
		}
		return null;
	}
	
	/** Get up to maxCount of nearby players */
	public static List<EntityPlayer> getNearbyPlayers(World world, BlockPos pos, double distance, int maxCount) {
		int count = 0;
		JointList<EntityPlayer> list = new JointList();
		for(EntityPlayer p : world.playerEntities) {
			if(count < maxCount && p.getDistanceSq(pos) < distance * distance) {
				list.add(p);
				count++;
			}
		}
		return list;
	}
}
