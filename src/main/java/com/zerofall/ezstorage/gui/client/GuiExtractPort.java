package com.zerofall.ezstorage.gui.client;

import java.io.IOException;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.zerofall.ezstorage.gui.server.ContainerExtractPort;
import com.zerofall.ezstorage.gui.server.SlotExtractList;
import com.zerofall.ezstorage.init.EZBlocks;
import com.zerofall.ezstorage.ref.RefStrings;
import com.zerofall.ezstorage.tileentity.TileEntityExtractPort;
import com.zerofall.ezstorage.util.JointList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.config.GuiCheckBox;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/** Extraction port GUI */
@SideOnly(Side.CLIENT)
public class GuiExtractPort extends GuiContainer {

	public static final ResourceLocation extractGuiTextures = new ResourceLocation(RefStrings.MODID, "textures/gui/extractPort.png");
	private TileEntityExtractPort tileExtract;
	private GuiButton listMode;
	private GuiCheckBox roundRobin;
	
	// pretty much fully-custom display behavior
	private Slot theSlot;
	private ItemStack draggedStack;
    private int dragSplittingRemnant;
    private boolean isRightMouseClick;
    private ItemStack returningStack;
    private long returningStackTime;
    private int touchUpX;
    private int touchUpY;
    private Slot returningStackDestSlot;
    private Slot clickedSlot;
    private int dragSplittingLimit;
	private ItemStack shiftClickedSlot;
	private long lastClickTime;
	private Slot lastClickSlot;
	private int lastClickButton;
	private boolean doubleClick;
    private boolean ignoreMouseUp;
	private int dragSplittingButton;
	private Slot currentDragTargetSlot;
	private long dragItemDropDelay;

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
		buttonList.add(roundRobin = new GuiCheckBoxCustom(0, k + 83, l + 42, "\u2714", tileExtract.roundRobin));
	}
	

	@Override
	protected void actionPerformed(GuiButton parButton) {
		if (parButton == listMode) {
			this.mc.playerController.sendEnchantPacket(this.inventorySlots.windowId, 0);
		}
		if (parButton == roundRobin) {
			this.mc.playerController.sendEnchantPacket(this.inventorySlots.windowId, 10 + (roundRobin.isChecked() ? 1 : 0));
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		// show the titles for each section
		String string = EZBlocks.extract_port.getLocalizedName();
		this.fontRendererObj.drawString(string, this.xSize / 2 - this.fontRendererObj.getStringWidth(string) / 2, 6, 0x404040);
		this.fontRendererObj.drawString(I18n.format("container.inventory", new Object[0]), 8, this.ySize - 94, 0x404040);

		// now update the button based on the selected mode
		listMode.displayString = tileExtract.listMode.toString();
		
		// round robin toggle box
		List<String> lines = new JointList().join("Enable round-robin extraction mode");
		if (roundRobin.isMouseOver())
			this.drawHoveringText(lines, mouseX - guiLeft, mouseY - guiTop);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(extractGuiTextures);
		int k = (this.width - this.xSize) / 2;
		int l = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);
	}
	
    /**
     * Returns whether the mouse is over the given slot.
     */
    private boolean isMouseOverSlot(Slot slotIn, int mouseX, int mouseY)
    {
        return this.isPointInRegion(slotIn.xDisplayPosition, slotIn.yDisplayPosition, 16, 16, mouseX, mouseY);
    }
    
    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreenSuper(int mouseX, int mouseY, float partialTicks)
    {
        for (int i = 0; i < this.buttonList.size(); ++i)
        {
            this.buttonList.get(i).drawButton(this.mc, mouseX, mouseY);
        }

        for (int j = 0; j < this.labelList.size(); ++j)
        {
            this.labelList.get(j).drawLabel(this.mc, mouseX, mouseY);
        }
    }
	
    /**
     * Draws the screen and all the components in it.
     */
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        int i = this.guiLeft;
        int j = this.guiTop;
        this.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
        GlStateManager.disableRescaleNormal();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        drawScreenSuper(mouseX, mouseY, partialTicks);
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.pushMatrix();
        GlStateManager.translate(i, j, 0.0F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableRescaleNormal();
        this.theSlot = null;
        int k = 240;
        int l = 240;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        for (int i1 = 0; i1 < this.inventorySlots.inventorySlots.size(); ++i1)
        {
            Slot slot = this.inventorySlots.inventorySlots.get(i1);
            this.drawSlot(slot);

            if (this.isMouseOverSlot(slot, mouseX, mouseY) && slot.canBeHovered())
            {
                this.theSlot = slot;
                GlStateManager.disableLighting();
                GlStateManager.disableDepth();
                int j1 = slot.xDisplayPosition;
                int k1 = slot.yDisplayPosition;
                GlStateManager.colorMask(true, true, true, false);
                this.drawGradientRect(j1, k1, j1 + 16, k1 + 16, -2130706433, -2130706433);
                GlStateManager.colorMask(true, true, true, true);
                GlStateManager.enableLighting();
                GlStateManager.enableDepth();
            }
        }

        RenderHelper.disableStandardItemLighting();
        this.drawGuiContainerForegroundLayer(mouseX, mouseY);
        RenderHelper.enableGUIStandardItemLighting();
        InventoryPlayer inventoryplayer = this.mc.thePlayer.inventory;
        ItemStack itemstack = this.draggedStack == null ? inventoryplayer.getItemStack() : this.draggedStack;

        if (itemstack != null)
        {
            int j2 = 8;
            int k2 = this.draggedStack == null ? 8 : 16;
            String s = null;

            if (this.draggedStack != null && this.isRightMouseClick)
            {
                itemstack = itemstack.copy();
                itemstack.stackSize = MathHelper.ceiling_float_int(itemstack.stackSize / 2.0F);
            }
            else if (this.dragSplitting && this.dragSplittingSlots.size() > 1)
            {
                itemstack = itemstack.copy();
                itemstack.stackSize = this.dragSplittingRemnant;

                if (itemstack.stackSize == 0)
                {
                    s = "" + TextFormatting.YELLOW + "0";
                }
            }

            this.drawItemStack(itemstack, mouseX - i - 8, mouseY - j - k2, s);
        }

        if (this.returningStack != null)
        {
            float f = (Minecraft.getSystemTime() - this.returningStackTime) / 100.0F;

            if (f >= 1.0F)
            {
                f = 1.0F;
                this.returningStack = null;
            }

            int l2 = this.returningStackDestSlot.xDisplayPosition - this.touchUpX;
            int i3 = this.returningStackDestSlot.yDisplayPosition - this.touchUpY;
            int l1 = this.touchUpX + (int)(l2 * f);
            int i2 = this.touchUpY + (int)(i3 * f);
            this.drawItemStack(this.returningStack, l1, i2, (String)null);
        }

        GlStateManager.popMatrix();

        if (inventoryplayer.getItemStack() == null && this.theSlot != null && this.theSlot.getHasStack())
        {
            ItemStack itemstack1 = this.theSlot.getStack();
            this.renderToolTip(itemstack1, mouseX, mouseY);
        }

        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
        RenderHelper.enableStandardItemLighting();
    }
    
    /**
     * Draws the given slot: any item in it, the slot's background, the hovered highlight, etc.
     */
    private void drawSlot(Slot slotIn)
    {
        int i = slotIn.xDisplayPosition;
        int j = slotIn.yDisplayPosition;
        ItemStack itemstack = slotIn.getStack();
        boolean flag = false;
        boolean flag1 = slotIn == this.clickedSlot && this.draggedStack != null && !this.isRightMouseClick;
        ItemStack itemstack1 = this.mc.thePlayer.inventory.getItemStack();
        String s = null;

        if (slotIn == this.clickedSlot && this.draggedStack != null && this.isRightMouseClick && itemstack != null)
        {
            itemstack = itemstack.copy();
            itemstack.stackSize /= 2;
        }
        else if (this.dragSplitting && this.dragSplittingSlots.contains(slotIn) && itemstack1 != null)
        {
            if (this.dragSplittingSlots.size() == 1)
            {
                return;
            }

            if (Container.canAddItemToSlot(slotIn, itemstack1, true) && this.inventorySlots.canDragIntoSlot(slotIn))
            {
                itemstack = itemstack1.copy();
                flag = true;
                Container.computeStackSize(this.dragSplittingSlots, this.dragSplittingLimit, itemstack, slotIn.getStack() == null ? 0 : slotIn.getStack().stackSize);

                if (itemstack.stackSize > itemstack.getMaxStackSize())
                {
                    s = TextFormatting.YELLOW + "" + itemstack.getMaxStackSize();
                    itemstack.stackSize = itemstack.getMaxStackSize();
                }

                if (itemstack.stackSize > slotIn.getItemStackLimit(itemstack))
                {
                    s = TextFormatting.YELLOW + "" + slotIn.getItemStackLimit(itemstack);
                    itemstack.stackSize = slotIn.getItemStackLimit(itemstack);
                }
            }
            else
            {
                this.dragSplittingSlots.remove(slotIn);
                this.updateDragSplitting();
            }
        }

        this.zLevel = 100.0F;
        this.itemRender.zLevel = 100.0F;

        if (itemstack == null && slotIn.canBeHovered())
        {
            TextureAtlasSprite textureatlassprite = slotIn.getBackgroundSprite();

            if (textureatlassprite != null)
            {
                GlStateManager.disableLighting();
                this.mc.getTextureManager().bindTexture(slotIn.getBackgroundLocation());
                this.drawTexturedModalRect(i, j, textureatlassprite, 16, 16);
                GlStateManager.enableLighting();
                flag1 = true;
            }
        }

        if (!flag1)
        {
            if (flag)
            {
                drawRect(i, j, i + 16, j + 16, -2130706433);
            }

            GlStateManager.enableDepth();
            this.itemRender.renderItemAndEffectIntoGUI(this.mc.thePlayer, itemstack, i, j);
            if(!(slotIn instanceof SlotExtractList)) this.itemRender.renderItemOverlayIntoGUI(this.fontRendererObj, itemstack, i, j, s);
        }

        this.itemRender.zLevel = 0.0F;
        this.zLevel = 0.0F;
    }

    /**
     * Draws an ItemStack.
     *  
     * The z index is increased by 32 (and not decreased afterwards), and the item is then rendered at z=200.
     */
    private void drawItemStack(ItemStack stack, int x, int y, String altText)
    {
        GlStateManager.translate(0.0F, 0.0F, 32.0F);
        this.zLevel = 200.0F;
        this.itemRender.zLevel = 200.0F;
        net.minecraft.client.gui.FontRenderer font = null;
        if (stack != null) font = stack.getItem().getFontRenderer(stack);
        if (font == null) font = fontRendererObj;
        this.itemRender.renderItemAndEffectIntoGUI(stack, x, y);
        this.itemRender.renderItemOverlayIntoGUI(font, stack, x, y - (this.draggedStack == null ? 0 : 8), altText);
        this.zLevel = 0.0F;
        this.itemRender.zLevel = 0.0F;
    }
    
    private void updateDragSplitting()
    {
        ItemStack itemstack = this.mc.thePlayer.inventory.getItemStack();

        if (itemstack != null && this.dragSplitting)
        {
            this.dragSplittingRemnant = itemstack.stackSize;

            for (Slot slot : this.dragSplittingSlots)
            {
                ItemStack itemstack1 = itemstack.copy();
                int i = slot.getStack() == null ? 0 : slot.getStack().stackSize;
                Container.computeStackSize(this.dragSplittingSlots, this.dragSplittingLimit, itemstack1, i);

                if (itemstack1.stackSize > itemstack1.getMaxStackSize())
                {
                    itemstack1.stackSize = itemstack1.getMaxStackSize();
                }

                if (itemstack1.stackSize > slot.getItemStackLimit(itemstack1))
                {
                    itemstack1.stackSize = slot.getItemStackLimit(itemstack1);
                }

                this.dragSplittingRemnant -= itemstack1.stackSize - i;
            }
        }
    }
    
    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    protected void mouseClickedSuper(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        if (mouseButton == 0)
        {
            for (int i = 0; i < this.buttonList.size(); ++i)
            {
                GuiButton guibutton = this.buttonList.get(i);

                if (guibutton.mousePressed(this.mc, mouseX, mouseY))
                {
                    GuiScreenEvent.ActionPerformedEvent.Pre event = new GuiScreenEvent.ActionPerformedEvent.Pre(this, guibutton, this.buttonList);
                    if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event))
                        break;
                    guibutton = event.getButton();
                    ReflectionHelper.setPrivateValue(GuiScreen.class, this, guibutton, "selectedButton", "field_146290_a");
                    guibutton.playPressSound(this.mc.getSoundHandler());
                    this.actionPerformed(guibutton);
                    if (this.equals(this.mc.currentScreen))
                        MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Post(this, event.getButton(), this.buttonList));
                }
            }
        }
    }
    
    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    @Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        mouseClickedSuper(mouseX, mouseY, mouseButton);
        boolean flag = this.mc.gameSettings.keyBindPickBlock.isActiveAndMatches(mouseButton - 100);
        Slot slot = this.getSlotAtPosition(mouseX, mouseY);
        long i = Minecraft.getSystemTime();
        this.doubleClick = this.lastClickSlot == slot && i - this.lastClickTime < 250L && this.lastClickButton == mouseButton;
        this.ignoreMouseUp = false;

        if (mouseButton == 0 || mouseButton == 1 || flag)
        {
            int j = this.guiLeft;
            int k = this.guiTop;
            boolean flag1 = mouseX < j || mouseY < k || mouseX >= j + this.xSize || mouseY >= k + this.ySize;
            if (slot != null) flag1 = false; // Forge, prevent dropping of items through slots outside of GUI boundaries
            int l = -1;

            if (slot != null)
            {
                l = slot.slotNumber;
            }

            if (flag1)
            {
                l = -999;
            }

            if (this.mc.gameSettings.touchscreen && flag1 && this.mc.thePlayer.inventory.getItemStack() == null)
            {
                this.mc.displayGuiScreen((GuiScreen)null);
                return;
            }

            if (l != -1)
            {
                if (this.mc.gameSettings.touchscreen)
                {
                    if (slot != null && slot.getHasStack())
                    {
                        this.clickedSlot = slot;
                        this.draggedStack = null;
                        this.isRightMouseClick = mouseButton == 1;
                    }
                    else
                    {
                        this.clickedSlot = null;
                    }
                }
                else if (!this.dragSplitting)
                {
                    if (this.mc.thePlayer.inventory.getItemStack() == null)
                    {
                        if (this.mc.gameSettings.keyBindPickBlock.isActiveAndMatches(mouseButton - 100))
                        {
                            this.handleMouseClick(slot, l, mouseButton, ClickType.CLONE);
                        }
                        else
                        {
                            boolean flag2 = l != -999 && (Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54));
                            ClickType clicktype = ClickType.PICKUP;

                            if (flag2)
                            {
                                this.shiftClickedSlot = slot != null && slot.getHasStack() ? slot.getStack() : null;
                                clicktype = ClickType.QUICK_MOVE;
                            }
                            else if (l == -999)
                            {
                                clicktype = ClickType.THROW;
                            }

                            this.handleMouseClick(slot, l, mouseButton, clicktype);
                        }

                        this.ignoreMouseUp = true;
                    }
                    else
                    {
                        this.dragSplitting = true;
                        this.dragSplittingButton = mouseButton;
                        this.dragSplittingSlots.clear();

                        if (mouseButton == 0)
                        {
                            this.dragSplittingLimit = 0;
                        }
                        else if (mouseButton == 1)
                        {
                            this.dragSplittingLimit = 1;
                        }
                        else if (this.mc.gameSettings.keyBindPickBlock.isActiveAndMatches(mouseButton - 100))
                        {
                            this.dragSplittingLimit = 2;
                        }
                    }
                }
            }
        }

        this.lastClickSlot = slot;
        this.lastClickTime = i;
        this.lastClickButton = mouseButton;
    }

    /**
     * Called when a mouse button is pressed and the mouse is moved around. Parameters are : mouseX, mouseY,
     * lastButtonClicked & timeSinceMouseClick.
     */
    @Override
	protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick)
    {
        Slot slot = this.getSlotAtPosition(mouseX, mouseY);
        ItemStack itemstack = this.mc.thePlayer.inventory.getItemStack();

        if (this.clickedSlot != null && this.mc.gameSettings.touchscreen)
        {
            if (clickedMouseButton == 0 || clickedMouseButton == 1)
            {
                if (this.draggedStack == null)
                {
                    if (slot != this.clickedSlot && this.clickedSlot.getStack() != null)
                    {
                        this.draggedStack = this.clickedSlot.getStack().copy();
                    }
                }
                else if (this.draggedStack.stackSize > 1 && slot != null && Container.canAddItemToSlot(slot, this.draggedStack, false))
                {
                    long i = Minecraft.getSystemTime();

                    if (this.currentDragTargetSlot == slot)
                    {
                        if (i - this.dragItemDropDelay > 500L)
                        {
                            this.handleMouseClick(this.clickedSlot, this.clickedSlot.slotNumber, 0, ClickType.PICKUP);
                            this.handleMouseClick(slot, slot.slotNumber, 1, ClickType.PICKUP);
                            this.handleMouseClick(this.clickedSlot, this.clickedSlot.slotNumber, 0, ClickType.PICKUP);
                            this.dragItemDropDelay = i + 750L;
                            --this.draggedStack.stackSize;
                        }
                    }
                    else
                    {
                        this.currentDragTargetSlot = slot;
                        this.dragItemDropDelay = i;
                    }
                }
            }
        }
        else if (this.dragSplitting && slot != null && itemstack != null && itemstack.stackSize > this.dragSplittingSlots.size() && Container.canAddItemToSlot(slot, itemstack, true) && slot.isItemValid(itemstack) && this.inventorySlots.canDragIntoSlot(slot))
        {
            this.dragSplittingSlots.add(slot);
            this.updateDragSplitting();
        }
    }
    
    /**
     * Called when a mouse button is released.
     */
    protected void mouseReleasedSuper(int mouseX, int mouseY, int state)
    {
    	GuiButton selectedButton = ReflectionHelper.getPrivateValue(GuiScreen.class, this, "selectedButton", "field_146290_a");
        if (selectedButton != null && state == 0)
        {
            selectedButton.mouseReleased(mouseX, mouseY);
            ReflectionHelper.setPrivateValue(GuiScreen.class, this, null, "selectedButton", "field_146290_a");
        }
    }

    /**
     * Called when a mouse button is released.
     */
    @Override
	protected void mouseReleased(int mouseX, int mouseY, int state)
    {
        mouseReleasedSuper(mouseX, mouseY, state); //Forge, Call parent to release buttons
        Slot slot = this.getSlotAtPosition(mouseX, mouseY);
        int i = this.guiLeft;
        int j = this.guiTop;
        boolean flag = mouseX < i || mouseY < j || mouseX >= i + this.xSize || mouseY >= j + this.ySize;
        if (slot != null) flag = false; // Forge, prevent dropping of items through slots outside of GUI boundaries
        int k = -1;

        if (slot != null)
        {
            k = slot.slotNumber;
        }

        if (flag)
        {
            k = -999;
        }

        if (this.doubleClick && slot != null && state == 0 && this.inventorySlots.canMergeSlot((ItemStack)null, slot))
        {
            if (isShiftKeyDown())
            {
                if (slot != null && slot.inventory != null && this.shiftClickedSlot != null)
                {
                    for (Slot slot2 : this.inventorySlots.inventorySlots)
                    {
                        if (slot2 != null && slot2.canTakeStack(this.mc.thePlayer) && slot2.getHasStack() && slot2.isSameInventory(slot) && Container.canAddItemToSlot(slot2, this.shiftClickedSlot, true))
                        {
                            this.handleMouseClick(slot2, slot2.slotNumber, state, ClickType.QUICK_MOVE);
                        }
                    }
                }
            }
            else
            {
                this.handleMouseClick(slot, k, state, ClickType.PICKUP_ALL);
            }

            this.doubleClick = false;
            this.lastClickTime = 0L;
        }
        else
        {
            if (this.dragSplitting && this.dragSplittingButton != state)
            {
                this.dragSplitting = false;
                this.dragSplittingSlots.clear();
                this.ignoreMouseUp = true;
                return;
            }

            if (this.ignoreMouseUp)
            {
                this.ignoreMouseUp = false;
                return;
            }

            if (this.clickedSlot != null && this.mc.gameSettings.touchscreen)
            {
                if (state == 0 || state == 1)
                {
                    if (this.draggedStack == null && slot != this.clickedSlot)
                    {
                        this.draggedStack = this.clickedSlot.getStack();
                    }

                    boolean flag2 = Container.canAddItemToSlot(slot, this.draggedStack, false);

                    if (k != -1 && this.draggedStack != null && flag2)
                    {
                        this.handleMouseClick(this.clickedSlot, this.clickedSlot.slotNumber, state, ClickType.PICKUP);
                        this.handleMouseClick(slot, k, 0, ClickType.PICKUP);

                        if (this.mc.thePlayer.inventory.getItemStack() != null)
                        {
                            this.handleMouseClick(this.clickedSlot, this.clickedSlot.slotNumber, state, ClickType.PICKUP);
                            this.touchUpX = mouseX - i;
                            this.touchUpY = mouseY - j;
                            this.returningStackDestSlot = this.clickedSlot;
                            this.returningStack = this.draggedStack;
                            this.returningStackTime = Minecraft.getSystemTime();
                        }
                        else
                        {
                            this.returningStack = null;
                        }
                    }
                    else if (this.draggedStack != null)
                    {
                        this.touchUpX = mouseX - i;
                        this.touchUpY = mouseY - j;
                        this.returningStackDestSlot = this.clickedSlot;
                        this.returningStack = this.draggedStack;
                        this.returningStackTime = Minecraft.getSystemTime();
                    }

                    this.draggedStack = null;
                    this.clickedSlot = null;
                }
            }
            else if (this.dragSplitting && !this.dragSplittingSlots.isEmpty())
            {
                this.handleMouseClick((Slot)null, -999, Container.getQuickcraftMask(0, this.dragSplittingLimit), ClickType.QUICK_CRAFT);

                for (Slot slot1 : this.dragSplittingSlots)
                {
                    this.handleMouseClick(slot1, slot1.slotNumber, Container.getQuickcraftMask(1, this.dragSplittingLimit), ClickType.QUICK_CRAFT);
                }

                this.handleMouseClick((Slot)null, -999, Container.getQuickcraftMask(2, this.dragSplittingLimit), ClickType.QUICK_CRAFT);
            }
            else if (this.mc.thePlayer.inventory.getItemStack() != null)
            {
                if (this.mc.gameSettings.keyBindPickBlock.isActiveAndMatches(state - 100))
                {
                    this.handleMouseClick(slot, k, state, ClickType.CLONE);
                }
                else
                {
                    boolean flag1 = k != -999 && (Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54));

                    if (flag1)
                    {
                        this.shiftClickedSlot = slot != null && slot.getHasStack() ? slot.getStack() : null;
                    }

                    this.handleMouseClick(slot, k, state, flag1 ? ClickType.QUICK_MOVE : ClickType.PICKUP);
                }
            }
        }

        if (this.mc.thePlayer.inventory.getItemStack() == null)
        {
            this.lastClickTime = 0L;
        }

        this.dragSplitting = false;
    }
    
    /**
     * Returns the slot at the given coordinates or null if there is none.
     */
    private Slot getSlotAtPosition(int x, int y)
    {
        for (int i = 0; i < this.inventorySlots.inventorySlots.size(); ++i)
        {
            Slot slot = this.inventorySlots.inventorySlots.get(i);

            if (this.isMouseOverSlot(slot, x, y))
            {
                return slot;
            }
        }

        return null;
    }
    
    /**
     * Returns the slot that is currently displayed under the mouse.
     */
    @Override
    public Slot getSlotUnderMouse()
    {
        return this.theSlot;
    }

}