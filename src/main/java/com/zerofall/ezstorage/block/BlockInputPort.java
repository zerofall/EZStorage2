package com.zerofall.ezstorage.block;

import com.zerofall.ezstorage.tileentity.TileEntityInputPort;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockInputPort extends EZBlockContainer {
	
	public BlockInputPort() {
		super("input_port", Material.IRON);
	}
	
	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileEntityInputPort();
	}
	
}