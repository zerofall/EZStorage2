package com.zerofall.ezstorage.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import com.zerofall.ezstorage.gui.server.InventoryExtractList;

/** The extraction port, a virtual output inventory */
public class TileEntityExtractPort extends TileEntityItemHandler {

	public InventoryExtractList extractList = new InventoryExtractList("extract_port", 9);
	public ItemStack buffer;
	
	public EnumListMode listMode = EnumListMode.IGNORE;
	public boolean roundRobin;
	
	@Override
	public void update() {
		super.update();
		
		if(!worldObj.isRemote && this.hasCore()) {
			// take from the core every tick if the buffer is empty
			if(buffer == null && !worldObj.isBlockPowered(pos)) {
				buffer = core.getFirstStack(1, listMode, roundRobin, extractList);
				this.markDirty();
			}
			// refresh the buffer once a second
			if(buffer != null && worldObj.getTotalWorldTime() % 20 == 0) {
				core.input(buffer);
				buffer = null;
				this.markDirty();
			}
		}
	}

	@Override
	public NBTTagCompound writeDataToNBT(NBTTagCompound nbt) {
		nbt.setInteger("listMode", listMode.ordinal());
		
		NBTTagList list = new NBTTagList();
		for (int i = 0; i < extractList.getSizeInventory(); i++) {
			NBTTagCompound stackTag = new NBTTagCompound();
			ItemStack slot = extractList.getStackInSlot(i);
			if(slot != null) slot.writeToNBT(stackTag);
			list.appendTag(stackTag);
		}
		nbt.setTag("slots", list);
		
		if(buffer != null) {
			NBTTagCompound bufTag = new NBTTagCompound();
			buffer.writeToNBT(bufTag);
			nbt.setTag("buffer", bufTag);
		}
		
		nbt.setBoolean("roundRobin", roundRobin);
		
		return nbt;
	}

	@Override
	public void readDataFromNBT(NBTTagCompound nbt) {
		listMode = EnumListMode.fromInt(nbt.getInteger("listMode"));
		
		NBTTagList list = nbt.getTagList("slots", 10);
		for(int i = 0; i < extractList.getSizeInventory(); i++) {
			NBTTagCompound stackTag = list.getCompoundTagAt(i);
			ItemStack slot = ItemStack.loadItemStackFromNBT(stackTag);
			extractList.setInventorySlotContents(i, slot);
		}
		
		NBTTagCompound bufTag = (NBTTagCompound)nbt.getTag("buffer");
		if(bufTag != null) {
			buffer = ItemStack.loadItemStackFromNBT(bufTag);
		}
		
		roundRobin = nbt.getBoolean("roundRobin");
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TextComponentString("extract_port");
	}

	@Override
	public int getSizeInventory() {
		return 1;
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		return buffer;
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		ItemStack ret = buffer;
		if(buffer.stackSize <= count) buffer = null;
		return ret;
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		return decrStackSize(index, getInventoryStackLimit());
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		buffer = stack;
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		int[] slots = new int[1];
		slots[0] = 0;
		return slots;
	}

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
		return false;
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
		return true;
	}

	@Override
	public String getName() {
		return "extract_port";
	}

	/** List modes */
	public static enum EnumListMode {
		IGNORE("Ignore"),
		WHITELIST("Whitelist"),
		BLACKLIST("Blacklist"),
		DISABLED("Disabled");

		private String name;

		private EnumListMode(String name) {
			this.name = name;
		}

		/** Get the mode from an integer (corrects overflow) */
		public static EnumListMode fromInt(int mode) {
			return values()[mode % values().length];
		}

		/** Rotate the list mode */
		public EnumListMode rotateMode() {
			return fromInt(this.ordinal() + 1);
		}

		/** Get the name of this list mode */
		@Override
		public String toString() {
			return name;
		}
	}

}
