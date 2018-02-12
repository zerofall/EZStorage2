package com.zerofall.ezstorage.gui.client;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.zerofall.ezstorage.EZStorage;
import com.zerofall.ezstorage.config.EZConfig;
import com.zerofall.ezstorage.gui.server.ContainerStorageCore;
import com.zerofall.ezstorage.jei.JEIUtils;
import com.zerofall.ezstorage.network.MessageCustomClick;
import com.zerofall.ezstorage.ref.RefStrings;
import com.zerofall.ezstorage.tileentity.TileEntityStorageCore;
import com.zerofall.ezstorage.util.EZItemRenderer;
import com.zerofall.ezstorage.util.EZStorageUtils;
import com.zerofall.ezstorage.util.ItemGroup;
import com.zerofall.ezstorage.util.JointList;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.util.ITooltipFlag.TooltipFlags;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.config.GuiCheckBox;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

/** The storage core GUI */
@SideOnly(Side.CLIENT)
public class GuiStorageCore extends GuiContainerEZ {

	TileEntityStorageCore tileEntity;
	EZItemRenderer ezRenderer;

	// scrolling and searching
	int scrollRow = 0;
	private boolean isScrolling = false;
	private boolean wasClicking = false;
	private static final ResourceLocation creativeInventoryTabs = new ResourceLocation("textures/gui/container/creative_inventory/tabs.png");
	private static final ResourceLocation searchBar = new ResourceLocation("textures/gui/container/creative_inventory/tab_item_search.png");
	private static final ResourceLocation sortGui = new ResourceLocation(RefStrings.MODID, "textures/gui/custom_gui.png");
	private float currentScroll;
	
	private GuiTextField searchField;
	private GuiCheckBox jeiSearchToggle;

	private List<ItemGroup> filteredList;

	// buttons
	protected GuiButton modeToggle;
	protected GuiButton craftClear;

	// updating the filter
	private boolean needsUpdate;
	private int updateTicksCurrent;
	private int updateTicksPassed;

	@Override
	public void initGui() {
		super.initGui();
		this.searchField = new GuiTextField(0, this.fontRenderer, this.guiLeft + 10, this.guiTop + 6, 80, this.fontRenderer.FONT_HEIGHT);
		this.searchField.setMaxStringLength(20);
		this.searchField.setEnableBackgroundDrawing(false);
		this.searchField.setTextColor(0xFFFFFF);
		this.searchField.setCanLoseFocus(true);
		this.searchField.setFocused(true);
		this.searchField.setText("");
		this.jeiSearchToggle = new GuiCheckBoxCustom(0, this.guiLeft + 83, this.guiTop + 4, "\u2714", tileEntity.jeiLink);
		this.jeiSearchToggle.visible = false;

		if (JEIUtils.isJEIAvailable()) {
			this.searchField.width = 64;
		} else {
			this.jeiSearchToggle.enabled = false;
		}

		filteredList = new ArrayList<ItemGroup>(this.tileEntity.inventory.inventory);
		buttonList.add(modeToggle = new GuiButton(0, guiLeft - 100, guiTop + 16, 90, 20, ""));
		buttonList.add(craftClear = new ButtonBlue(0, guiLeft + 20, guiTop + 100, 14, 14, "x"));
		buttonList.add(jeiSearchToggle);
		modeToggle.visible = false;
		craftClear.visible = false;
	}

	public GuiStorageCore(EntityPlayer player, World world, int x, int y, int z) {
		super(new ContainerStorageCore(player, world, x, y, z));
		this.tileEntity = ((TileEntityStorageCore) world.getTileEntity(new BlockPos(x, y, z)));
		this.xSize = 195;
		this.ySize = 222;
	}

	public GuiStorageCore(ContainerStorageCore containerStorageCore, World world, int x, int y, int z) {
		super(containerStorageCore);
		this.tileEntity = ((TileEntityStorageCore) world.getTileEntity(new BlockPos(x, y, z)));
		this.xSize = 195;
		this.ySize = 222;
	}

	@Override
	protected void drawBackground() {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.renderEngine.bindTexture(getBackground());
		int x = (this.width - this.xSize) / 2;
		int y = (this.height - this.ySize) / 2;
		drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);

		// sorting gui
		if (this.tileEntity.hasSortBox) {
			this.mc.renderEngine.bindTexture(sortGui);
			drawTexturedModalRect(guiLeft - 108, guiTop, 0, 128, 112, 128);
			modeToggle.visible = true;
		} else {
			modeToggle.visible = false;
		}

		// search box
		this.searchField.setVisible(this.tileEntity.hasSearchBox);
		if (this.tileEntity.hasSearchBox) {
			this.mc.renderEngine.bindTexture(searchBar);
			drawTexturedModalRect(guiLeft + 8, guiTop + 4, 80, 4, this.searchField.width + 10, 12);
			this.searchField.drawTextBox();
		}

		// match the JEI search box to the storage search box
		if (JEIUtils.isJEIAvailable()) {
			jeiSearchToggle.visible = this.tileEntity.hasSearchBox;
			if (jeiSearchToggle.isChecked() && JEIUtils.jeiSearchTextChanged()) {
				searchBoxChange(JEIUtils.getSearchText());
			}
		} else {
			jeiSearchToggle.visible = false;
		}
	}

	/** Marks this inventory as needing a filter update */
	public void markFilterUpdate() {
		needsUpdate = true;
		updateTicksPassed = 0;
		updateTicksCurrent = mc.player.ticksExisted;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		handleScrolling(mouseX, mouseY);

		// check if this inventory needs a filter update
		// then update it after no more than 0.1s have passed
		if (needsUpdate) {
			if (mc.player.ticksExisted > updateTicksCurrent) {
				updateTicksPassed++;
				updateTicksCurrent = mc.player.ticksExisted;
			}

			// update the filtered list
			if (updateTicksPassed >= 2) {
				updateFilteredItems();
				needsUpdate = false;
			}
		}

		DecimalFormat formatter = new DecimalFormat("#,###");
		String totalCount = formatter.format(this.tileEntity.inventory.getTotalCount());
		String max = formatter.format(this.tileEntity.inventory.maxItems);
		String amount = totalCount + "/" + max;

		// right-align text
		int stringWidth = fontRenderer.getStringWidth(amount);

		// scale down text if it is too large
		if (stringWidth > 88) {
			double ScaleFactor = 0.7;
			double RScaleFactor = 1.0 / ScaleFactor;
			GL11.glPushMatrix();
			GL11.glScaled(ScaleFactor, ScaleFactor, ScaleFactor);
			int X = (int) ((187 - stringWidth * ScaleFactor) * RScaleFactor);
			fontRenderer.drawString(amount, X, 10, 4210752);
			GL11.glPopMatrix();
		} else {
			fontRenderer.drawString(amount, 187 - stringWidth, 6, 4210752);
		}

		// sorting mode
		modeToggle.displayString = tileEntity.sortMode.toString();
		if (this.tileEntity.hasSortBox) {
			this.fontRenderer.drawString("Sorting Mode", -100, 6, 4210752);
			GL11.glPushMatrix();
			double scale = 0.7;
			GL11.glScaled(scale, scale, scale);
			this.fontRenderer.drawSplitString(tileEntity.sortMode.getDesc(), (int) (-100 / scale), (int) (42 / scale), (int) (96 / scale),
					4210752);
			GL11.glPopMatrix();
		}

		// JEI toggle box
		List<String> lines = new JointList().join("JEI search link");
		if (jeiSearchToggle.isMouseOver())
			this.drawHoveringText(lines, mouseX - guiLeft, mouseY - guiTop);

		// item list
		int x = 8;
		int y = 18;
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.zLevel = 200.0F;
		this.itemRender.zLevel = 200.0F;
		if (this.ezRenderer == null) {
			this.ezRenderer = new EZItemRenderer(this.mc.getTextureManager(), this.itemRender.getItemModelMesher().getModelManager());
		}
		this.ezRenderer.zLevel = 200.0F;

		boolean finished = false;
		for (int i = 0; i < this.rowsVisible(); i++) {
			x = 8;
			for (int j = 0; j < 9; j++) {
				int index = (i * 9) + j;
				index = scrollRow * 9 + index;
				if (index >= this.filteredList.size()) {
					finished = true;
					break;
				}

				// get the item group
				ItemGroup group = this.filteredList.get(index);
				ItemStack stack = group.itemStack;

				FontRenderer font = null;
				if (!stack.isEmpty())
					font = stack.getItem().getFontRenderer(stack);
				if (font == null)
					font = fontRenderer;
				RenderHelper.enableGUIStandardItemLighting();
				this.itemRender.renderItemAndEffectIntoGUI(stack, x, y);
				ezRenderer.renderItemOverlayIntoGUI(font, stack, x, y, "" + group.count);

				x += 18;
			}
			if (finished) {
				break;
			}
			y += 18;
		}

		int i1 = 175;
		int k = 18;
		int l = k + 108;
		this.mc.getTextureManager().bindTexture(creativeInventoryTabs);
		this.drawTexturedModalRect(i1, k + (int) ((l - k - 17) * this.currentScroll), 232, 0, 12, 15);
		this.zLevel = 0.0F;
		this.itemRender.zLevel = 0.0F;
	}

	/** Perform various tasks when buttons are pushed */
	@Override
	protected void actionPerformed(GuiButton parButton) {
		if (parButton == modeToggle) {
			this.mc.playerController.sendEnchantPacket(this.inventorySlots.windowId, 0);
		} else if (parButton == craftClear) {
			this.mc.playerController.sendEnchantPacket(this.inventorySlots.windowId, 1);
		} else if (parButton == jeiSearchToggle) {
			tileEntity.jeiLink = jeiSearchToggle.isChecked(); // update the checked status (clientside)
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		Integer slot = getSlotAt(mouseX, mouseY);
		if (slot != null) {
			int mode = 0;
			if (GuiScreen.isShiftKeyDown()) {
				mode = 1;
			}
			int index = this.tileEntity.inventory.slotCount();
			if (slot < this.filteredList.size()) {
				ItemGroup group = this.filteredList.get(slot);
				if (group != null) {
					index = this.tileEntity.inventory.indexOf(group);
					if (index < 0) {
						return;
					}
					this.renderToolTip(group, mouseX, mouseY);
				}
			}
		}
	}

	/** Custom tooltips have the exact amount of items at the bottom */
	protected void renderToolTip(ItemGroup group, int x, int y) {
		ItemStack stack = group.itemStack;
		List<String> list = stack.getTooltip(this.mc.player, 
				this.mc.gameSettings.advancedItemTooltips ? TooltipFlags.ADVANCED : TooltipFlags.NORMAL);

		for (int i = 0; i < list.size(); ++i) {
			if (i == 0) {
				list.set(i, stack.getRarity().rarityColor + list.get(i));
			} else {
				list.set(i, TextFormatting.GRAY + list.get(i));
			}
		}

		DecimalFormat formatter = new DecimalFormat("#,###");
		list.add(TextFormatting.ITALIC + "Count: " + formatter.format(group.count));
		FontRenderer font = stack.getItem().getFontRenderer(stack);
		this.drawHoveringText(list, x, y, (font == null ? fontRenderer : font));
	}

	/** Update the filtered items on type */
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (!this.checkHotbarKeys(keyCode)) {
			if (this.tileEntity.hasSearchBox && this.searchField.isFocused() && this.searchField.textboxKeyTyped(typedChar, keyCode)) {
				searchBoxChange(null);
			} else {
				super.keyTyped(typedChar, keyCode);
			}
		}
	}

	/** Call this to change the search box and update the filter and scroll position */
	private void searchBoxChange(String text) {
		if (text != null)
			this.searchField.setText(text);

		updateFilteredItems();
		scrollTo(this.currentScroll = 0); // reset scrolling

		// sync the search bars if applicable
		if (JEIUtils.isJEIAvailable() && this.jeiSearchToggle.isChecked()) {
			JEIUtils.setSearchText(this.searchField.getText());
		}
	}

	/** Update the filtered list of items that the core needs to function correctly */
	private void updateFilteredItems() {
		filteredList = new ArrayList<ItemGroup>(this.tileEntity.inventory.inventory);
		Iterator iterator = this.filteredList.iterator();
		String searchText = this.searchField.getText().toLowerCase();
		boolean oreSearch = false;
		boolean modSearch = false;
		boolean tabSearch = false;

		// search modes
		if (EZConfig.enableSearchModes) {
			// ore dictionary search
			if (searchText.startsWith("$")) {
				oreSearch = true;
				searchText = searchText.substring(1);
			} else

			// mod id and mod name search
			if (searchText.startsWith("@")) {
				modSearch = true;
				searchText = searchText.substring(1);
			} else

			// creative tab name search
			if (searchText.startsWith("%")) {
				tabSearch = true;
				searchText = searchText.substring(1);
			}
		}

		while (iterator.hasNext()) {
			ItemGroup group = (ItemGroup) iterator.next();
			ItemStack itemstack = group.itemStack;
			boolean flag = false;
			Iterator<String> iterator1 = null;
			String compare = "";
			String compare2 = "";

			if (oreSearch) { // searches oredict entries
				int[] oreIds = OreDictionary.getOreIDs(itemstack);
				List<String> ores = new JointList();
				for (int id : oreIds)
					ores.add(OreDictionary.getOreName(id));
				iterator1 = ores.iterator();

			} else if (modSearch) { // searches mod ids and mod names
				compare = itemstack.getItem().getRegistryName().getResourceDomain();
				compare2 = EZStorageUtils.getModNameFromID(compare).toLowerCase();
				compare = compare.toLowerCase();

			} else if (tabSearch) { // searches the item's creative tab
				try {
					compare = itemstack.getItem().getCreativeTab().getTabLabel().toLowerCase();
				} catch (Exception e) {}

			} else { // searches the item's name and tooltip info
				iterator1 = itemstack.getTooltip(this.mc.player, 
						this.mc.gameSettings.advancedItemTooltips ? TooltipFlags.ADVANCED : TooltipFlags.NORMAL).iterator();
			}

			while (true) {

				if (modSearch || tabSearch) { // mod or creative tab search

					flag = compare.contains(searchText) || compare2.contains(searchText);

				} else { // regular or ore search
					if (iterator1.hasNext()) {
						String s = iterator1.next();

						if (!s.toLowerCase().contains(searchText)) {
							continue;
						}

						flag = true;
					}
				}

				if (!flag) {
					iterator.remove();
				}

				break;
			}
		}
	}

	private void handleScrolling(int mouseX, int mouseY) {
		boolean flag = Mouse.isButtonDown(0);

		int k = this.guiLeft;
		int l = this.guiTop;
		int i1 = k + 175;
		int j1 = l + 18;
		int k1 = i1 + 14;
		int l1 = j1 + 108;

		if (!this.wasClicking && flag && mouseX >= i1 && mouseY >= j1 && mouseX < k1 && mouseY < l1) {
			this.isScrolling = true;
		}

		if (!flag) {
			this.isScrolling = false;
		}

		this.wasClicking = flag;

		if (this.isScrolling) {
			this.currentScroll = (mouseY - j1 - 7.5F) / (l1 - j1 - 15.0F);
			this.currentScroll = MathHelper.clamp(this.currentScroll, 0.0F, 1.0F);
			scrollTo(this.currentScroll);
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {

		Integer slot = getSlotAt(mouseX, mouseY);
		if (slot != null) {

			int mode = 0;
			if (GuiScreen.isShiftKeyDown()) {
				mode = 1;
			}
			int index = this.tileEntity.inventory.slotCount();
			if (slot < this.filteredList.size()) {
				ItemGroup group = this.filteredList.get(slot);
				if (group != null) {
					index = this.tileEntity.inventory.indexOf(group);
					if (index < 0) {
						return;
					}
				}
			}

			EZStorage.nw.sendToServer(new MessageCustomClick(index, mouseButton, mode));
			ContainerStorageCore container = (ContainerStorageCore) this.inventorySlots;
			container.customSlotClick(index, mouseButton, mode, this.mc.player);
		} else {
			int elementX = this.searchField.x;
			int elementY = this.searchField.y;
			if (mouseX >= elementX && mouseX <= elementX + this.searchField.width && mouseY >= elementY
					&& mouseY <= elementY + this.searchField.height) {
				if (mouseButton == 1 || GuiScreen.isShiftKeyDown()) {
					this.searchField.setText("");
				}
				this.searchField.setFocused(true);
			} else {
				this.searchField.setFocused(false);
			}
		}
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	private Integer getSlotAt(int x, int y) {
		int startX = this.guiLeft + 7;
		int startY = this.guiTop + 17;

		int clickedX = x - startX;
		int clickedY = y - startY;

		if (clickedX >= 0 && clickedY >= 0) {
			int column = clickedX / 18;
			if (column < 9) {
				int row = clickedY / 18;
				if (row < this.rowsVisible()) {
					int slot = (row * 9) + column + (scrollRow * 9);
					return slot;
				}
			}
		}
		return null;
	}

	@Override
	public void handleMouseInput() throws IOException {

		super.handleMouseInput();
		int i = Mouse.getEventDWheel();

		if (i != 0) {
			int j = this.tileEntity.inventory.slotCount() / 9 - this.rowsVisible() + 1;

			if (i > 0) {
				i = 1;
			}

			if (i < 0) {
				i = -1;
			}

			this.currentScroll = (float) (this.currentScroll - (double) i / (double) j);
			this.currentScroll = MathHelper.clamp(this.currentScroll, 0.0F, 1.0F);
			scrollTo(this.currentScroll);
		}

	}

	private void scrollTo(float scroll) {
		int i = (this.tileEntity.inventory.slotCount() + 8) / 9 - this.rowsVisible();
		int j = (int) (scroll * i + 0.5D);
		if (j < 0) {
			j = 0;
		}
		this.scrollRow = j;
	}

	protected ResourceLocation getBackground() {
		return new ResourceLocation(RefStrings.MODID + ":textures/gui/storage_scroll_gui.png");
	}

	public int rowsVisible() {
		return 6;
	}
}
