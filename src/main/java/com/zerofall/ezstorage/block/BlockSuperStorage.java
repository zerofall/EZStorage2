package com.zerofall.ezstorage.block;

import net.minecraft.block.material.Material;

import com.zerofall.ezstorage.config.EZConfig;
import com.zerofall.ezstorage.registry.IRegistryBlock;

/** A super storage box */
public class BlockSuperStorage extends BlockStorage implements IRegistryBlock {
	
	public BlockSuperStorage() {
		super("super_storage_box", Material.IRON);
	}
	
	@Override
	public int getCapacity() {
		return EZConfig.superCapacity;
	}

}
