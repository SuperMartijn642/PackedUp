package com.supermartijn642.packedup;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

/**
 * Created 2/7/2020 by SuperMartijn642
 */
public class BackpackContainerScreen extends GuiContainer {

    private ResourceLocation texture;
    private String title;
    private ITextComponent playerInventory;

    private int rows;

    public BackpackContainerScreen(BackpackContainer container, EntityPlayer player){
        super(container);
        this.texture = new ResourceLocation("packedup", "textures/inventory/rows" + container.rows + ".png");
        this.xSize = 176 + Math.max(0, (container.rows - 9) * 18);
        this.ySize = 112 + 18 * Math.min(8, container.rows);
        this.rows = container.rows;

        if(container.bagSlot < 0 || !(player.inventory.getStackInSlot(container.bagSlot).getItem() instanceof BackpackItem))
            this.title = PackedUp.basicbackpack.getItemStackDisplayName(ItemStack.EMPTY);
        else
            this.title = player.inventory.getStackInSlot(container.bagSlot).getDisplayName();
        this.playerInventory = player.inventory.getDisplayName();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks){
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY){
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(this.texture);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY){
        if(this.rows >= 9)
            return;
        this.fontRenderer.drawString(this.title, 8, 6, 4210752);
        this.fontRenderer.drawString(this.playerInventory.getFormattedText(), 8, this.ySize - 96 + 2, 4210752);
    }
}
