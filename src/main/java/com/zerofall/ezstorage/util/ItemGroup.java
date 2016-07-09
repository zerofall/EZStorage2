package com.zerofall.ezstorage.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.minecraft.item.ItemStack;

/** Like an ItemStack, but with a discrete count */
public class ItemGroup {
	
	public ItemStack itemStack;
	public long count;
	
	// whether or not this ItemGroup is highlighted
	public boolean highlighted;
	
	public ItemGroup(ItemStack itemStack) {
		this.itemStack = itemStack;
		this.count = itemStack.stackSize;
	}
	
	public ItemGroup(ItemStack itemStack, long count) {
		this.itemStack = itemStack;
		this.count = count;
	}
	
	@Override
	public String toString() {
		return itemStack.getDisplayName() + ":" + count;
	}
	
	/** Sort modes */
	public static enum EnumSortMode {
		COUNT(CountComparator.class, "Count Down"),
		INVERSE_COUNT(InverseCountComparator.class, "Count Up"),
		NAME(NameComparator.class, "A - Z"),
		INVERSE_NAME(InverseNameComparator.class, "Z - A");
		
		private Class<? extends ItemGroupComparator> sortClass;
		private String name;
		
		private EnumSortMode(Class<? extends ItemGroupComparator> sortClass, String name) {
			this.sortClass = sortClass;
			this.name = name;
		}
		
		/** Get the mode from an integer (corrects overflow) */
		public static EnumSortMode fromInt(int mode) {
			return values()[mode % values().length];
		}

		/** Rotate the sort mode */
		public EnumSortMode rotateMode() {
			return fromInt(this.ordinal() + 1);
		}
		
		/** Sort the inventory with this mode */
		public void sortInventory(List<ItemGroup> inventory) {
			try {
				Collections.sort(inventory, sortClass.newInstance());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/** Get the name of this sort mode */
		@Override
		public String toString() {
			return name;
		}
	}
	
	/** Sorting parent class */
	public static abstract class ItemGroupComparator implements Comparator<ItemGroup> {}
	
	/** Sort by count, then fall back to alphabetical */
	public static class CountComparator extends ItemGroupComparator {
		@Override
		public int compare(ItemGroup group1, ItemGroup group2) {
			Long l1 = (Long)group1.count;
			Long l2 = (Long)group2.count;
			if(l1 != l2) {
				return l2.compareTo(l1);
			} else {
				String n1 = group1.itemStack.getDisplayName();
				String n2 = group2.itemStack.getDisplayName();
				return n1.compareTo(n2);
			}
		}
	}
	
	/** Sort by inverse count, then fall back to inverse alphabetical */
	public static class InverseCountComparator extends ItemGroupComparator {
		@Override
		public int compare(ItemGroup group1, ItemGroup group2) {
			Long l1 = (Long)group1.count;
			Long l2 = (Long)group2.count;
			if(l1 != l2) {
				return l1.compareTo(l2);
			} else {
				String n1 = group1.itemStack.getDisplayName();
				String n2 = group2.itemStack.getDisplayName();
				return n2.compareTo(n1);
			}
		}
	}
	
	/** Sort alphabetically, then fall back to item count */
	public static class NameComparator extends ItemGroupComparator {
		@Override
		public int compare(ItemGroup group1, ItemGroup group2) {
			String n1 = group1.itemStack.getDisplayName();
			String n2 = group2.itemStack.getDisplayName();
			if(!n1.equals(n2)) {
				return n1.compareTo(n2);
			} else {
				Long l1 = (Long)group1.count;
				Long l2 = (Long)group2.count;
				return l2.compareTo(l1);
			}
		}
	}
	
	/** Sort inverse alphabetically, then fall back to inverse item count */
	public static class InverseNameComparator extends ItemGroupComparator {
		@Override
		public int compare(ItemGroup group1, ItemGroup group2) {
			String n1 = group1.itemStack.getDisplayName();
			String n2 = group2.itemStack.getDisplayName();
			if(!n1.equals(n2)) {
				return n2.compareTo(n1);
			} else {
				Long l1 = (Long)group1.count;
				Long l2 = (Long)group2.count;
				return l1.compareTo(l2);
			}
		}
	}
}
