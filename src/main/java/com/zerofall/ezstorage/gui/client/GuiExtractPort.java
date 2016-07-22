package com.zerofall.ezstorage.gui.client;

import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.opengl.GL11;

import com.zerofall.ezstorage.gui.server.ContainerExtractPort;
import com.zerofall.ezstorage.init.EZBlocks;
import com.zerofall.ezstorage.network.EZNetwork;
import com.zerofall.ezstorage.ref.RefStrings;
import com.zerofall.ezstorage.tileentity.TileEntityExtractPort;
import com.zerofall.ezstorage.tileentity.TileEntitySecurityBox;
import com.zerofall.ezstorage.tileentity.TileEntitySecurityBox.SecurePlayer;
import com.zerofall.ezstorage.util.EZStorageUtils;
import com.zerofall.ezstorage.util.JointList;

/** Extraction port GUI */
@SideOnly(Side.CLIENT)
public class GuiExtractPort extends GuiContainer {
	
	public static final ResourceLocation extractGuiTextures = new ResourceLocation(RefStrings.MODID, "textures/gui/extractPort.png");
	private TileEntityExtractPort tileExtract;
	private GuiButton listMode;

	public GuiExtractPort(InventoryPlayer invPlayer, TileEntityExtractPort tile, BlockPos pos) {
		super(new ContainerExtractPort(invPlayer, tile));
		this.tileExtract = tile;
		this.tileExtract.setPos(pos);
		this.ySize = 151;
	}
	
	@Override
	public void initGui() {
		super.initGui();
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        buttonList.add(listMode = new GuiButton(0, k + 99, l + 42, 70, 20, ""));
	}
	
	@Override
	protected void actionPerformed(GuiButton parButton) {
		if(parButton == listMode) {
			this.mc.playerController.sendEnchantPacket(this.inventorySlots.windowId, 0);
		}
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		// show the titles for each section
		String string = EZBlocks.extract_port.getLocalizedName();
		this.fontRendererObj.drawString(string, this.xSize / 2 - this.fontRendererObj.getStringWidth(string) / 2, 6, 0x404040);
		this.fontRendererObj.drawString(I18n.format("container.inventory", new Object[0]), 8, this.ySize - 94, 0x404040);

		// now update the button based on the selected mode
		listMode.displayString = tileExtract.listMode.toString();
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
		 GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	        this.mc.getTextureManager().bindTexture(extractGuiTextures);
	        int k = (this.width - this.xSize) / 2;
	        int l = (this.height - this.ySize) / 2;
	        this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
	}

}