package com.zerofall.ezstorage.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.zerofall.ezstorage.EZStorage;
import com.zerofall.ezstorage.gui.GuiHandler;
import com.zerofall.ezstorage.tileentity.TileEntityExtractPort;

public class BlockExtractPort extends EZBlockContainer {

	public BlockExtractPort() {
		super("extract_port", Material.IRON);
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileEntityExtractPort();
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, ItemStack heldItem,
			EnumFacing side, float hitX, float hitY, float hitZ) {
		playerIn.openGui(EZStorage.instance, GuiHandler.EXTRACT, worldIn, pos.getX(), pos.getY(), pos.getZ());
		return true;
	}

}
