package com.zerofall.ezstorage.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.zerofall.ezstorage.tileentity.TileEntityStorageCore;
import com.zerofall.ezstorage.util.BlockRef;

/** The access terminal block */
public class BlockAccessTerminal extends StorageMultiblock {

	public BlockAccessTerminal() {
		super("access_terminal", Material.IRON);
	}

	/** Activate the core's gui from afar */
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, ItemStack heldItem,
			EnumFacing side, float hitX, float hitY, float hitZ) {
		TileEntityStorageCore core;
		if ((core = this.findCore(new BlockRef(this, pos.getX(), pos.getY(), pos.getZ()), worldIn, null)) != null) {
			IBlockState state1 = worldIn.getBlockState(core.getPos());
			BlockStorageCore block = (BlockStorageCore) state1.getBlock();
			return block.onBlockActivated(worldIn, core.getPos(), state1, playerIn, hand, heldItem, side, hitX, hitY, hitZ);
		}
		return false;
	}

}
