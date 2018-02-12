package com.zerofall.ezstorage.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.zerofall.ezstorage.gui.server.ContainerStorageCore;

/** Custom click message to be processed on the server */
public class MessageCustomClick implements IMessage {

	private int index;
	private int button;
	private int mode;

	public MessageCustomClick() {}

	public MessageCustomClick(int index, int button, int mode) {
		this.index = index;
		this.button = button;
		this.mode = mode;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		index = ByteBufUtils.readVarInt(buf, 5);
		button = ByteBufUtils.readVarInt(buf, 5);
		mode = ByteBufUtils.readVarInt(buf, 5);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeVarInt(buf, index, 5);
		ByteBufUtils.writeVarInt(buf, button, 5);
		ByteBufUtils.writeVarInt(buf, mode, 5);
	}

	/** Handle the serverside custom slot click */
	public static class Handler implements IMessageHandler<MessageCustomClick, IMessage> {

		@Override
		public IMessage onMessage(MessageCustomClick message, MessageContext ctx) {
			EntityPlayerMP player = ctx.getServerHandler().player;
			if (player != null)
				((WorldServer) player.world).addScheduledTask(() -> handle(player, message));
			return null; // no response in this case
		}

		/** Actually perform the slot click server-side */
		public void handle(EntityPlayerMP player, MessageCustomClick message) {
			Container container = player.openContainer;
			if (container != null && container instanceof ContainerStorageCore) {
				ContainerStorageCore storageContainer = (ContainerStorageCore) container;
				storageContainer.customSlotClick(message.index, message.button, message.mode, player);
			}
		}

	}

}
