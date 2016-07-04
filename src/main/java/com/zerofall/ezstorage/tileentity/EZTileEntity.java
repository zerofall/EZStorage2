package com.zerofall.ezstorage.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;

/** The parent tile entity for this mod */
public abstract class EZTileEntity extends TileEntity implements ITickable {
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		return writeDataToNBT(compound);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		readDataFromNBT(compound);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		readFromNBT(pkt.getNbtCompound());
	}
	
	/** New required method for 1.9.4+ tile entities */
	@Override
	public NBTTagCompound getUpdateTag() {
		NBTTagCompound nbtTag = new NBTTagCompound();
		return writeToNBT(nbtTag);
	}
	
	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(this.pos, 1, getUpdateTag());
	}
	
	/** Writes custom data to the NBT tag after writing basic tile entity data */
	public abstract NBTTagCompound writeDataToNBT(NBTTagCompound paramNBTTagCompound);
	
	/** Reads custom data from the NBT tag after reading basic tile entity data */
	public abstract void readDataFromNBT(NBTTagCompound paramNBTTagCompound);
}
