package com.zerofall.ezstorage.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.zerofall.ezstorage.EZStorage;
import com.zerofall.ezstorage.gui.GuiHandler;
import com.zerofall.ezstorage.init.EZItems;
import com.zerofall.ezstorage.registry.IRegistryBlock;
import com.zerofall.ezstorage.tileentity.TileEntitySecurityBox;

/** A block that can be added to the system to secure it against intruders */
public class BlockSecurityBox extends EZBlockContainer {

	public BlockSecurityBox() {
		super("security_box", Material.IRON);
		this.setBlockUnbreakable(); // no conventional breakability
	}
	
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntitySecurityBox();
	}
	
	/** Add the owner to the allowed players list */
	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
		if(placer instanceof EntityPlayer) {
			EntityPlayer p = (EntityPlayer)placer;
			TileEntitySecurityBox tile = (TileEntitySecurityBox)worldIn.getTileEntity(pos);
			tile.addAllowedPlayer(p);
			tile.markDirty();
		}
	}
	
	/** Destroy the block / open its gui */
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		TileEntitySecurityBox tile = (TileEntitySecurityBox)worldIn.getTileEntity(pos);
		if(tile.isPlayerAllowed(playerIn)) { // allowed player actions:
			if(heldItem != null && heldItem.getItem() == EZItems.key) { // try to break the block
				if(!worldIn.isRemote) worldIn.destroyBlock(pos, true); // permitted block breaking
				return true;
			} else { // simply access the block GUI
				playerIn.openGui(EZStorage.instance, GuiHandler.SECURITY, worldIn, pos.getX(), pos.getY(), pos.getZ());
				return true;
			}
		}
		return false;
	}

}
