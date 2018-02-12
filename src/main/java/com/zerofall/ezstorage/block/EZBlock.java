package com.zerofall.ezstorage.block;

import com.zerofall.ezstorage.EZStorage;
import com.zerofall.ezstorage.registry.IRegistryBlock;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/** Superclass for all mod blocks */
public abstract class EZBlock extends Block implements IRegistryBlock {

	protected EZBlock(String name, Material materialIn) {
		super(materialIn);
		this.setRegistryName(name);
		this.setCreativeTab(EZStorage.instance.creativeTab);
		this.setHardness(2.0f);
	}
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, 
			EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		ItemStack heldItem = player != null ? player.getHeldItem(hand) : ItemStack.EMPTY;
		return onBlockActivated(world, pos, state, player, hand, heldItem, side, hitX, hitY, hitZ);
	}
	
	/** Wrapper for old method signature */
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, ItemStack heldItem,
			EnumFacing side, float hitX, float hitY, float hitZ) {
		return super.onBlockActivated(worldIn, pos, state, playerIn, hand, side, hitX, hitY, hitZ);
	}

}
