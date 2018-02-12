package com.zerofall.ezstorage.util;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.zerofall.ezstorage.gui.server.InventoryExtractList;
import com.zerofall.ezstorage.tileentity.TileEntityExtractPort.EnumListMode;
import com.zerofall.ezstorage.tileentity.TileEntityStorageCore;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

/** The EZStorage inventory system */
public class EZInventory {

	public TileEntityStorageCore tile;
	public List<ItemGroup> inventory;
	public long maxItems = 0;
	
	private List<ItemGroup> ignore;

	public EZInventory(TileEntityStorageCore tile) {
		this.inventory = new ArrayList<ItemGroup>();
		this.ignore = new ArrayList<ItemGroup>();
		this.tile = tile;
	}

	/** Input a stack to the system (always sort, e.g. from player action) */
	public @Nonnull ItemStack input(ItemStack itemStack) {
		return input(itemStack, true);
	}

	/** Input a stack to the system with sorting option */
	public @Nonnull ItemStack input(ItemStack itemStack, boolean sort) {
		// Inventory is full
		if (getTotalCount() >= maxItems) {
			return itemStack;
		}
		long space = maxItems - getTotalCount();

		// Only part of the stack can fit
		int amount = (int) Math.min(space, itemStack.getCount());
		ItemStack result = mergeStack(itemStack, amount);

		// sort or no sort?
		if (sort) {
			tile.sortInventory();
		} else {
			tile.updateInventory();
		}

		return result;
	}

	/** Sort the inventory (block updates are separate) */
	public void sort() {
		tile.sortMode.sortInventory(this.inventory);
	}

	/** Attempt a stack merge */
	private @Nonnull ItemStack mergeStack(ItemStack itemStack, int amount) {
		for (ItemGroup group : inventory) {
			if (stacksEqual(group.itemStack, itemStack)) {
				group.count += amount;
				itemStack.shrink(amount);
				if (itemStack.getCount() <= 0) {
					return ItemStack.EMPTY;
				} else {
					return itemStack;
				}
			}
		}

		// need to add a space
		inventory.add(new ItemGroup(itemStack.copy(), amount));
		itemStack.shrink(amount);

		if (itemStack.getCount() <= 0) {
			return ItemStack.EMPTY;
		} else {
			return itemStack;
		}
	}

	/** Extract items from the inventory */
	// Type: 0= full stack, 1= half stack, 2= single
	public @Nonnull ItemStack getItemsAt(int index, int type) {
		return getItemsAt(index, type, -1);
	}

	/** Extract items from the inventory with precision */
	public @Nonnull ItemStack getItemsAt(int index, int type, int size) {
		return getItemsAt(index, type, size, false);
	}

	/** Extract items from the inventory with precision and peek support */
	public @Nonnull ItemStack getItemsAt(int index, int type, int size, boolean peek) {
		if (index >= inventory.size()) {
			return ItemStack.EMPTY;
		}
		ItemGroup group = inventory.get(index);
		ItemStack stack = group.itemStack.copy();
		if (size > 1) {
			if (type == 1) {
				size = size / 2;
			} else if (type == 2) {
				size = 1;
			}
		}

		return extractStack(group, size, peek);
	}

	/** Extract items on whitelist / blacklist match */
	public @Nonnull ItemStack getItemsExtractList(EnumListMode mode, boolean roundRobin, InventoryExtractList list, int size, boolean peek) {

		// inventory is empty, treat it like IGNORE mode
		if (list.isEmpty())
			return getItemsAt(0, 0, size, peek);

		// not empty
		for (int i = 0; i < list.getSizeInventory(); i++) {
			ItemStack comp = list.getStackInSlot(i);
			if (comp.isEmpty())
				continue; // ignore empty slots

			for (ItemGroup g : this.inventory) {
				if(!roundRobin || !ignore.contains(g)) {
					if (EZInventory.stacksEqualOreDict(comp, g.itemStack)) {
						if (mode == EnumListMode.BLACKLIST) {
							continue;
						} else {
							if(roundRobin) ignore.add(g);
							return extractStack(g, size, peek);
						}
					} else {
						if (mode == EnumListMode.WHITELIST) {
							continue;
						} else {
							if(roundRobin) ignore.add(g);
							return extractStack(g, size, peek);
						}
					}
				}
			}
			if(roundRobin) ignore.clear();	// no more matches for the round robin mode
		}
		return ItemStack.EMPTY;
	}

	/** Extract items on whitelist / blacklist match */
	public ItemStack getItemsExtractList(EnumListMode mode, boolean roundRobin, InventoryExtractList list, int size) {
		return getItemsExtractList(mode, roundRobin, list, size, false);
	}

	/** Peeks items on whitelist / blacklist match */
	public ItemStack peekItemsExtractList(EnumListMode mode, boolean roundRobin, InventoryExtractList list) {
		return getItemsExtractList(mode, roundRobin, list, -1, true);
	}

	/** Extracts an itemstack from an item group */
	private @Nonnull ItemStack extractStack(ItemGroup group, int stackSize, boolean peek) {
		ItemStack stack = group.itemStack.copy();
		if (stackSize < 0) {
			stackSize = (int) Math.min(stack.getMaxStackSize(), group.count);
		} else {
			stackSize = Math.min(stack.getMaxStackSize(), (int) Math.min(stackSize, group.count));
		}
		stack.setCount(stackSize);

		if (!peek) { // remove items when not peeking
			group.count -= stackSize;
			if (group.count <= 0) {
				inventory.remove(group);
				tile.sortInventory();
			} else {
				tile.updateInventory();
			}
		}

		return stack;
	}

	/** Get items and decrease their stack size in the inventory */
	public @Nonnull ItemStack getItems(ItemStack[] itemStacks) {
		for (ItemGroup group : inventory) {
			for (ItemStack itemStack : itemStacks) {
				if (stacksEqualOreDict(group.itemStack, itemStack)) {
					if (group.count >= itemStack.getCount()) {
						ItemStack stack = group.itemStack.copy();
						stack.setCount(itemStack.getCount());
						group.count -= itemStack.getCount();
						if (group.count <= 0) {
							inventory.remove(group);
						}
						return stack;
					}
					return ItemStack.EMPTY;
				}
			}
		}
		return ItemStack.EMPTY;
	}

	/** Get the size of the inventory */
	public int slotCount() {
		return inventory.size();
	}

	/** Check stacks for equality to join them in the inventory */
	public static boolean stacksEqual(ItemStack stack1, ItemStack stack2) {
		if (stack1.isEmpty() && stack2.isEmpty()) {
			return true;
		}
		if (stack1.isEmpty() || stack2.isEmpty()) {
			return false;
		}
		if (stack1.getItem() == stack2.getItem()) {
			if (stack1.getItemDamage() == stack2.getItemDamage()) {
				if ((!stack1.hasTagCompound() && !stack2.hasTagCompound())
						|| (stack1.hasTagCompound() && stack1.getTagCompound().equals(stack2.getTagCompound()))) {
					return true;
				}
			}
		}
		return false;
	}

	/** Check stacks for oredict equality (wildcard damage support) */
	public static boolean stacksEqualOreDict(ItemStack stack1, ItemStack stack2) {
		boolean first = stacksEqual(stack1, stack2);
		boolean second = stack1.getItem() == stack2.getItem();
		boolean third = stack1.getItemDamage() == OreDictionary.WILDCARD_VALUE || stack2.getItemDamage() == OreDictionary.WILDCARD_VALUE;
		return first || (second && third);
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
		for (ItemGroup g : inventory) {
			if (stacksEqual(g.itemStack, group.itemStack)) {
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
