package com.zerofall.ezstorage.tileentity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.FMLLog;

import org.apache.logging.log4j.Logger;

import com.zerofall.ezstorage.EZStorage;
import com.zerofall.ezstorage.block.BlockCraftingBox;
import com.zerofall.ezstorage.block.BlockInputPort;
import com.zerofall.ezstorage.block.BlockOutputPort;
import com.zerofall.ezstorage.block.BlockSearchBox;
import com.zerofall.ezstorage.block.BlockSortBox;
import com.zerofall.ezstorage.block.BlockStorage;
import com.zerofall.ezstorage.block.BlockStorageCore;
import com.zerofall.ezstorage.block.StorageMultiblock;
import com.zerofall.ezstorage.init.EZBlocks;
import com.zerofall.ezstorage.network.MessageFilterUpdate;
import com.zerofall.ezstorage.util.BlockRef;
import com.zerofall.ezstorage.util.EZInventory;
import com.zerofall.ezstorage.util.EZStorageUtils;
import com.zerofall.ezstorage.util.ItemGroup;
import com.zerofall.ezstorage.util.ItemGroup.EnumSortMode;

/** The storage core tile entity */
public class TileEntityStorageCore extends EZTileEntity {
	
	public EZInventory inventory;
	
	Set<BlockRef> multiblock = new HashSet<BlockRef>();
	public boolean disabled = false;
	private boolean firstTick = false;
	public boolean hasCraftBox = false;
	public boolean hasSearchBox = false;
	public EnumSortMode sortMode = EnumSortMode.COUNT;
	public boolean hasSortBox = false;
	
	public TileEntityStorageCore() {
		inventory = new EZInventory(this);
	}
	
	/** Inputs a stack to the inventory */
	public ItemStack input(ItemStack stack) {
		ItemStack result = this.inventory.input(stack);
		sortInventory();
		return result;
	}
	
	/** Gets the first full stack in the inventory */
	public ItemStack getRandomStack() {
		ItemStack result = this.inventory.getItemsAt(0, 0);
		sortInventory();
		return result;
	}
	
	/** Sorts the inventory on change and tells clients to update their filtered lists */
	public void sortInventory() {
		if(!this.worldObj.isRemote) {
			this.inventory.sort();
			updateTileEntity();
			EZStorage.networkWrapper.sendToDimension(new MessageFilterUpdate(this), worldObj.provider.getDimension());
		}
	}
	
	/** Updates the tile entity position in the world and marks it to be saved */
	public void updateTileEntity() {
		EZStorageUtils.notifyBlockUpdate(this);
		this.markDirty();
	}

	@Override
	public NBTTagCompound writeDataToNBT(NBTTagCompound nbt) {
		NBTTagList nbttaglist = new NBTTagList();
		for (int i = 0; i < this.inventory.slotCount(); ++i) {
			ItemGroup group = this.inventory.inventory.get(i);
			if (group != null && group.itemStack != null && group.count > 0) {
				NBTTagCompound tag = new NBTTagCompound();
				tag.setByte("Index", (byte) i);
				group.itemStack.writeToNBT(tag);
				tag.setLong("InternalCount", group.count);
				nbttaglist.appendTag(tag);
			}
		}
		nbt.setTag("Internal", nbttaglist);
		nbt.setLong("InternalMax", this.inventory.maxItems);
		nbt.setBoolean("hasSearchBox", this.hasSearchBox);
		nbt.setBoolean("isDisabled", this.disabled);
		nbt.setInteger("sortMode", this.sortMode.ordinal());
		nbt.setBoolean("hasSortBox", this.hasSortBox);
		return nbt; 
	}

	@Override
	public void readDataFromNBT(NBTTagCompound nbt) {
		NBTTagList nbttaglist = nbt.getTagList("Internal", 10);

		if (nbttaglist != null) {
			inventory.inventory = new ArrayList<ItemGroup>();
			for (int i = 0; i < nbttaglist.tagCount(); ++i) {
				NBTTagCompound tag = nbttaglist.getCompoundTagAt(i);
				int j = tag.getByte("Index") & 255;
				ItemStack stack = ItemStack.loadItemStackFromNBT(tag);
				long count = tag.getLong("InternalCount");
				ItemGroup group = new ItemGroup(stack, count);
				this.inventory.inventory.add(group);
			}
		}
		long maxItems = nbt.getLong("InternalMax");
		this.inventory.maxItems = maxItems;
		this.hasSearchBox = nbt.getBoolean("hasSearchBox");
		this.disabled = nbt.getBoolean("isDisabled");
		this.sortMode = EnumSortMode.fromInt(nbt.getInteger("sortMode"));
		this.hasSortBox = nbt.getBoolean("hasSortBox");
	}
	
	/** Scans the multiblock structure for valid blocks */
	public void scanMultiblock() {
		inventory.maxItems = 0;
		this.hasCraftBox = false;
		this.hasSearchBox = false;
		this.hasSortBox = false;
		multiblock = new HashSet<BlockRef>();
		BlockRef ref = new BlockRef(this);
		multiblock.add(ref);
		getValidNeighbors(ref);
		for (BlockRef blockRef : multiblock) {
			if (blockRef.block instanceof BlockStorage) {
				BlockStorage sb = (BlockStorage)blockRef.block;
				inventory.maxItems += sb.getCapacity();
			}
		}
		EZStorageUtils.notifyBlockUpdate(this);
	}
	
	
	/**
	 * Recursive function that scans a block's neighbors, and adds valid blocks to the multiblock list
	 * @param br
	 */
	private void getValidNeighbors(BlockRef br) {
		List<BlockRef> neighbors = EZStorageUtils.getNeighbors(br.pos.getX(), br.pos.getY(), br.pos.getZ(), worldObj);
		for (BlockRef blockRef : neighbors) {
			if (blockRef.block instanceof StorageMultiblock) {
				if (multiblock.add(blockRef) == true && validateSystem() == true) {
					if (blockRef.block instanceof BlockInputPort) {
						TileEntityInputPort entity = (TileEntityInputPort)this.worldObj.getTileEntity(blockRef.pos);
						entity.core = this;
					}
					if (blockRef.block instanceof BlockOutputPort) {
						TileEntityOutputPort entity = (TileEntityOutputPort)this.worldObj.getTileEntity(blockRef.pos);
						entity.core = this;
					}
					if (blockRef.block instanceof BlockCraftingBox) {
						this.hasCraftBox = true;
					}
					if (blockRef.block instanceof BlockSearchBox) {
						this.hasSearchBox = true;
					}
					if (blockRef.block instanceof BlockSortBox) {
						this.hasSortBox = true;
					}
					getValidNeighbors(blockRef);
				}
			}
		}
	}
	
	/** Makes sure the storage system doesn't have multiple storage cores */
	public boolean validateSystem() {
		int count = 0;
		for (BlockRef ref : multiblock) {
			if (ref.block instanceof BlockStorageCore) {
				count ++;
			}
			if (count > 1) {
				if (worldObj.isRemote) {
					if (Minecraft.getMinecraft() != null && Minecraft.getMinecraft().thePlayer != null) {
						Minecraft.getMinecraft().thePlayer.addChatMessage(new TextComponentString("You can only have 1 Storage Core per system!"));
					}
				} else if (worldObj.getBlockState(pos).getBlock() == EZBlocks.storage_core) {
					worldObj.setBlockToAir(getPos());
					worldObj.spawnEntityInWorld(new EntityItem(worldObj, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(EZBlocks.storage_core)));
				}
				return false;
			}
		}
		return true;
	}
	
	public boolean isPartOfMultiblock(BlockRef blockRef) {
		if (multiblock != null) {
			if (multiblock.contains(blockRef)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void update() {
		if (!firstTick) {
			if (worldObj != null) {
				firstTick = true;
				scanMultiblock();
			}
		}
		
	}
	
}
