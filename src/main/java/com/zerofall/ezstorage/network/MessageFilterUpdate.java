package com.zerofall.ezstorage.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.zerofall.ezstorage.EZStorage;
import com.zerofall.ezstorage.gui.server.ContainerStorageCore;

/**
 * A message to tell clients to update their filtered lists if they are on the
 * same storage GUI
 */
public class MessageFilterUpdate implements IMessage {

	private BlockPos pos;

	public MessageFilterUpdate() {}

	public MessageFilterUpdate(TileEntity t) {
		pos = t.getPos();
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		pos = BlockPos.fromLong(buf.readLong());
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeLong(pos.toLong());
	}

	/** Update the clientside filtered list */
	public static class Handler implements IMessageHandler<MessageFilterUpdate, IMessage> {

		@Override
		public IMessage onMessage(MessageFilterUpdate message, MessageContext ctx) {
			EntityPlayer p = EZStorage.proxy.getClientPlayer();
			if (p != null)
				Minecraft.getMinecraft().addScheduledTask(() -> handle(p, message));
			return null;
		}

		/** Do the filter sync */
		@SideOnly(Side.CLIENT)
		public void handle(EntityPlayer p, MessageFilterUpdate message) {
			if (p.openContainer != null && p.openContainer instanceof ContainerStorageCore) {
				ContainerStorageCore c = (ContainerStorageCore) p.openContainer;
				if (message.pos.equals(c.tileEntity.getPos())) {
					EZStorage.proxy.markGuiDirty();
				}
			}
		}

	}

}
