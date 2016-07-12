package com.zerofall.ezstorage.network;

import io.netty.buffer.ByteBuf;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.zerofall.ezstorage.gui.server.ContainerStorageCoreCrafting;
import com.zerofall.ezstorage.util.JointList;

/** A message to sync the crafting matrix to the client */
public class MessageCraftingSync implements IMessage {
	
	private List<ItemStack> stackList;
	
	public MessageCraftingSync() {}
	
	public MessageCraftingSync(InventoryCrafting matrix) {
		stackList = new JointList();
		for(int i = 0; i < matrix.getSizeInventory(); i++) {
			stackList.add(matrix.getStackInSlot(i));
		}
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		NBTTagCompound tag = ByteBufUtils.readTag(buf);
		int count = tag.getInteger("count");
		NBTTagList list = tag.getTagList("items", 10);
		stackList = new JointList();
		for(int i = 0; i < count; i++) {
			NBTTagCompound item = list.getCompoundTagAt(i);
			stackList.add(ItemStack.loadItemStackFromNBT(item));
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("count", stackList.size());
		NBTTagList list = new NBTTagList();
		for(ItemStack s : stackList) {
			NBTTagCompound nTag = new NBTTagCompound();
			if(s != null) s.writeToNBT(nTag);
			list.appendTag(nTag);
		}
		tag.setTag("items", list);
		ByteBufUtils.writeTag(buf, tag);
	}
	
	/** Update the clientside crafting matrix */
	public static class Handler implements IMessageHandler<MessageCraftingSync, IMessage> {
		@Override
		public IMessage onMessage(MessageCraftingSync message, MessageContext ctx) {
			EntityPlayer player = Minecraft.getMinecraft().thePlayer;
			if(player.openContainer != null && player.openContainer instanceof ContainerStorageCoreCrafting) {
				InventoryCrafting craft = ((ContainerStorageCoreCrafting)player.openContainer).craftMatrix;
				int i = 0;
				for(ItemStack s : message.stackList) {
					craft.setInventorySlotContents(i++, s);
				}
			}
			return null; // end of message chain
		}
		
	}

}
