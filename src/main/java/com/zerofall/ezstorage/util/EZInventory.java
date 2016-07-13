package com.zerofall.ezstorage.util;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import com.zerofall.ezstorage.tileentity.TileEntityStorageCore;

/** The EZStorage inventory system */
public class EZInventory {
	
	public TileEntityStorageCore tile;
	public List<ItemGroup> inventory;
	public long maxItems = 0;
	
	public EZInventory(TileEntityStorageCore tile) {
		this.inventory = new ArrayList<ItemGroup>();
		this.tile = tile;
	}
	
	/** Input a stack to the system */
	public ItemStack input(ItemStack itemStack) {
		// Inventory is full
		if (getTotalCount() >= maxItems) {
			return itemStack;
		}
		long space = maxItems - getTotalCount();
		
		// Only part of the stack can fit
		int amount = (int)Math.min(space, (long)itemStack.stackSize);
		ItemStack result = mergeStack(itemStack, amount);
		tile.sortInventory();
		return result;
	}
	
	/** Sort the inventory (block updates are separate) */
	public void sort() {
		tile.sortMode.sortInventory(this.inventory);
	}
	
	/** Attempt a stack merge */
	private ItemStack mergeStack(ItemStack itemStack, int amount) {
		for (ItemGroup group : inventory) {
			if (stacksEqual(group.itemStack, itemStack)) {
				group.count += amount;
				itemStack.stackSize -= amount;
				if (itemStack.stackSize <= 0) {
					return null;
				} else {
					return itemStack;
				}
			}
		}
		
		// need to add a space
		inventory.add(new ItemGroup(itemStack, amount));
		itemStack.stackSize -= amount;
		
		if (itemStack.stackSize <= 0) {
			return null;
		} else {
			return itemStack;
		}
	}
	
	/** Extract items from the inventory */
	//Type: 0= full stack, 1= half stack, 2= single
	public ItemStack getItemsAt(int index, int type) {
		if (index >= inventory.size()) {
			return null;
		}
		ItemGroup group = inventory.get(index);
		ItemStack stack = group.itemStack.copy();
		int size = (int)Math.min((long)stack.getMaxStackSize(), group.count);
		if (size > 1) {
			if (type == 1) {
				size = size/2;
			} else if (type == 2) {
				size = 1;
			}
		}
		stack.stackSize = size;
		group.count -= size;
		
		// when an item is depleted, remove it and sort the inventory
		if (group.count <= 0) {
			inventory.remove(index);
			tile.sortInventory();
		}
		
		return stack;
	}
	
	/** Get items and decrease their stack size in the inventory */
	public ItemStack getItems(ItemStack[] itemStacks) {
		for (ItemGroup group : inventory) {
			for (ItemStack itemStack : itemStacks) {
				if (stacksEqualOreDict(group.itemStack, itemStack)) {
					if (group.count >= itemStack.stackSize) {
						ItemStack stack = group.itemStack.copy();
						stack.stackSize = itemStack.stackSize;
						group.count -= itemStack.stackSize;
						if (group.count <= 0) {
							inventory.remove(group);
						}
						return stack;
					}
					return null;
				}
			}
		}
		return null;
	}
	
	/** Get items without decreasing their stack size in the inventory */
	public ItemStack getItemsNoDecrease(ItemStack[] itemStacks) {
		for (ItemGroup group : inventory) {
			for (ItemStack itemStack : itemStacks) {
				if (stacksEqualOreDict(group.itemStack, itemStack)) {
					if (group.count >= itemStack.stackSize) {
						ItemStack stack = group.itemStack.copy();
						stack.stackSize = itemStack.stackSize;
						return stack;
					}
					return null;
				}
			}
		}
		return null;
	}
	
	/** Get the size of the inventory */
	public int slotCount() {
		return inventory.size();
	}
	
	/** Check stacks for equality to join them in the inventory */
	public static boolean stacksEqual(ItemStack stack1, ItemStack stack2) {
		if (stack1 == null && stack2 == null) {
			return true;
		}
		if (stack1 == null || stack2 == null) {
			return false;
		}
		if (stack1.getItem() == stack2.getItem()) {
			if (stack1.getItemDamage() == stack2.getItemDamage()) {
				if((!stack1.hasTagCompound() && !stack2.hasTagCompound()) || (stack1.hasTagCompound() && stack1.getTagCompound().equals(stack2.getTagCompound()))) {
					return true;
				}
			}
		}
		return false;
	}
	
	/** Check stacks for oredict equality (wildcard damage support) */
	public static boolean stacksEqualOreDict(ItemStack stack1, ItemStack stack2) {
		boolean first = stacksEqual(stack1, stack2);
		boolean nullCheck = stack1 != null && stack2 != null;
		boolean second = stack1.getItem() == stack2.getItem();
		boolean third = stack1.getItemDamage() == OreDictionary.WILDCARD_VALUE || stack2.getItemDamage() == OreDictionary.WILDCARD_VALUE;
		return first || (nullCheck && second && third);
	}
	
	/** Get the total item count */
	public long getTotalCount() {
		long count = 0;
		for (ItemGroup group : inventory) {
			count += group.count;
		}
		return count;
	}
	
	/** Gets the index of an itemgroup in the inventory */
	public int indexOf(ItemGroup group) {
		int n = 0;
		for(ItemGroup g : inventory) {
			if(stacksEqual(g.itemStack, group.itemStack)) {
				return n;
			}
			n++;
		}
		return -1;
	}
	
	/** Convert this inventory to a string */
	@Override
	public String toString() {
		return inventory.toString();
	}
}
