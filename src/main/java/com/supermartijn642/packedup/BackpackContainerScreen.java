package com.supermartijn642.packedup;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

/**
 * Created 2/7/2020 by SuperMartijn642
 */
public class BackpackContainerScreen extends ContainerScreen<BackpackContainer> {

    private final ResourceLocation texture;

    public BackpackContainerScreen(BackpackContainer container, PlayerInventory inventory, ITextComponent name){
        super(container, inventory, name);
        this.texture = new ResourceLocation("packedup", "textures/inventory/rows" + container.rows + ".png");
        this.imageWidth = 176 + Math.max(0, (container.rows - 9) * 18);
        this.imageHeight = 112 + 18 * Math.min(8, container.rows);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks){
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY){
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bind(this.texture);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        this.blit(matrixStack, i, j, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY){
        if(this.menu.rows >= 9)
            return;
        this.font.draw(matrixStack, this.title, 8.0F, 6.0F, 4210752);
        this.font.draw(matrixStack, this.inventory.getDisplayName(), 8.0F, (float)(this.imageHeight - 96 + 2), 4210752);
    }
}
