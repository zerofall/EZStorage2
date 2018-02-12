package com.zerofall.ezstorage.network;

import java.util.List;

import com.zerofall.ezstorage.EZStorage;
import com.zerofall.ezstorage.gui.server.ContainerStorageCoreCrafting;
import com.zerofall.ezstorage.tileentity.TileEntityStorageCore;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.oredict.OreDictionary;

/** The JEI crafting recipe server sync message */
public class MessageRecipeSync implements IMessage {

	private NBTTagCompound recipe;

	public MessageRecipeSync() {}

	public MessageRecipeSync(NBTTagCompound recipe) {
		this.recipe = recipe;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		recipe = ByteBufUtils.readTag(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeTag(buf, this.recipe);
	}

	/** Draw items from the server's inventory to fulfill the crafting matrix request */
	public static class Handler implements IMessageHandler<MessageRecipeSync, MessageCraftingSync> {

		ItemStack[][] recipe;

		@Override
		public MessageCraftingSync onMessage(MessageRecipeSync message, MessageContext ctx) {
			EntityPlayerMP player = ctx.getServerHandler().player;
			if (player != null)
				((WorldServer) player.world).addScheduledTask(() -> handle(player, message));
			return null;
		}

		/** Do the operation on the server thread */
		public void handle(EntityPlayerMP player, MessageRecipeSync message) {
			Container container = player.openContainer;
			if (container instanceof ContainerStorageCoreCrafting) {
				ContainerStorageCoreCrafting con = (ContainerStorageCoreCrafting) container;
				TileEntityStorageCore tileEntity = con.tileEntity;

				// Empty grid into inventory
				con.clearGrid(player);

				this.recipe = new ItemStack[9][];
				for (int x = 0; x < this.recipe.length; x++) {
					NBTTagList list = message.recipe.getTagList("#" + x, 10);
					if (list.tagCount() > 0) {
						NBTTagCompound tag = list.getCompoundTagAt(0);
						boolean hasOre = tag.hasKey("ore");
						if (hasOre) { // sent an oredict entry
							List<ItemStack> items = OreDictionary.getOres(tag.getString("ore"));
							this.recipe[x] = new ItemStack[items.size()];
							for (int y = 0; y < items.size(); y++) {
								this.recipe[x][y] = items.get(y).copy();
							}
						} else { // sent an itemstack list
							this.recipe[x] = new ItemStack[list.tagCount()];
							for (int y = 0; y < list.tagCount(); y++) {
								this.recipe[x][y] = new ItemStack(list.getCompoundTagAt(y));
							}
						}
					}
				}
				for (int i = 0; i < this.recipe.length; i++) {
					if (this.recipe[i] != null && this.recipe[i].length > 0) {
						Slot slot = con.getSlotFromInventory(con.craftMatrix, i);
						if (slot != null) {
							ItemStack retreived = tileEntity.inventory.getItems(this.recipe[i]);
							if (!retreived.isEmpty()) {
								slot.putStack(retreived);
							}
						}
					}
				}

				// resort and update the tile entity
				tileEntity.sortInventory();

				// reply with a crafting matrix sync message
				EZStorage.nw.sendTo(new MessageCraftingSync(con.craftMatrix), player);
			}
		}

	}

}
