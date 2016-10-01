package com.zerofall.ezstorage.util;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.ModelManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;

/** Render items in the storage core GUI */
public class EZItemRenderer extends RenderItem {

	public EZItemRenderer(TextureManager textureManager, ModelManager modelManager) {
		super(textureManager, modelManager, null);
	}

	@Override
	public void renderItemOverlayIntoGUI(FontRenderer fr, ItemStack stack, int xPosition, int yPosition, String text) {
		if (stack != null) {
			float ScaleFactor = 0.5f;
			float RScaleFactor = 1.0f / ScaleFactor;
			int offset = 0;

			boolean unicodeFlag = fr.getUnicodeFlag();
			fr.setUnicodeFlag(false);

			long amount = Long.parseLong(text);

			if (amount != 0) {
				if (stack.getItem().showDurabilityBar(stack)) {
					double health = stack.getItem().getDurabilityForDisplay(stack);
					int j = (int) Math.round(13.0D - health * 13.0D);
					int i = (int) Math.round(255.0D - health * 255.0D);
					GL11.glDisable(GL11.GL_LIGHTING);
					GL11.glDisable(GL11.GL_DEPTH_TEST);
					GL11.glDisable(GL11.GL_TEXTURE_2D);
					GL11.glDisable(GL11.GL_ALPHA_TEST);
					GL11.glDisable(GL11.GL_BLEND);
					Tessellator tessellator = Tessellator.getInstance();
					VertexBuffer vertexbuffer = tessellator.getBuffer();
					this.draw(vertexbuffer, xPosition + 2, yPosition + 13, 13, 2, 0, 0, 0, 255);
					this.draw(vertexbuffer, xPosition + 2, yPosition + 13, 12, 1, (255 - i) / 4, 64, 0, 255);
					this.draw(vertexbuffer, xPosition + 2, yPosition + 13, j, 1, 255 - i, i, 0, 255);
					GL11.glEnable(GL11.GL_BLEND); // Reenable blend so enchanted
													// stuff stops being black
					GL11.glEnable(GL11.GL_ALPHA_TEST);
					GL11.glEnable(GL11.GL_TEXTURE_2D);
					GL11.glEnable(GL11.GL_LIGHTING);
					GL11.glEnable(GL11.GL_DEPTH_TEST);
					GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				}

				String var6 = String.valueOf(Math.abs(amount));

				if (amount > 999999999999L) {
					var6 = String.valueOf((int) Math.floor(amount / 1000000000000.0)) + 'T';
				} else if (amount > 9999999999L) {
					var6 = "." + String.valueOf((int) Math.floor(amount / 1000000000000.0)) + 'T';
				} else if (amount > 999999999L) {
					var6 = String.valueOf((int) Math.floor(amount / 1000000000.0)) + 'B';
				} else if (amount > 99999999L) {
					var6 = "." + (int) Math.floor(amount / 100000000.0) + 'B';
				} else if (amount > 999999L) {
					var6 = String.valueOf((int) Math.floor(amount / 1000000.0)) + 'M';
				} else if (amount > 99999L) {
					var6 = "." + (int) Math.floor(amount / 100000.0) + 'M';
				} else if (amount > 9999L) {
					var6 = String.valueOf((int) Math.floor(amount / 1000.0)) + 'K';
				}

				GL11.glDisable(GL11.GL_LIGHTING);
				GL11.glDisable(GL11.GL_DEPTH_TEST);
				GL11.glPushMatrix();
				GL11.glScaled(ScaleFactor, ScaleFactor, ScaleFactor);
				int X = (int) (((float) xPosition + offset + 16.0f - fr.getStringWidth(var6) * ScaleFactor) * RScaleFactor);
				int Y = (int) (((float) yPosition + offset + 16.0f - 7.0f * ScaleFactor) * RScaleFactor);
				fr.drawStringWithShadow(var6, X, Y, 16777215);
				GL11.glPopMatrix();
				GL11.glEnable(GL11.GL_LIGHTING);
				GL11.glEnable(GL11.GL_DEPTH_TEST);
			}

			fr.setUnicodeFlag(unicodeFlag);
		}

	}

	/** Draw with the WorldRenderer */
	private void draw(VertexBuffer renderer, int x, int y, int width, int height, int red, int green, int blue, int alpha) {
		renderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
		renderer.pos(x + 0, y + 0, 0.0D).color(red, green, blue, alpha).endVertex();
		renderer.pos(x + 0, y + height, 0.0D).color(red, green, blue, alpha).endVertex();
		renderer.pos(x + width, y + height, 0.0D).color(red, green, blue, alpha).endVertex();
		renderer.pos(x + width, y + 0, 0.0D).color(red, green, blue, alpha).endVertex();
		Tessellator.getInstance().draw();
	}

}