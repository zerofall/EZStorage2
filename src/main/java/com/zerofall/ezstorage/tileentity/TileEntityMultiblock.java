package com.zerofall.ezstorage.tileentity;

import com.zerofall.ezstorage.block.StorageMultiblock;

/** Multiblock tile entity with default core-searching functionality */
public abstract class TileEntityMultiblock extends TileEntityBase {
	
	/** The storage core */
	public TileEntityStorageCore core;
	
	/** The period in ticks at which to scan given that scanning is possible */
	private static final int updatePeriod = 10;
	
	/** Scan the multiblock if the storage core is null */
	@Override
	public void update() {
		if(!worldObj.isRemote && core == null && worldObj.getTotalWorldTime() % updatePeriod == 0) {
			StorageMultiblock block = (StorageMultiblock)worldObj.getBlockState(pos).getBlock();
			core = block.attemptMultiblock(worldObj, pos);
		}
	}
	
}
