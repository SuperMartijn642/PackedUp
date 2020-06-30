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
        this.xSize = 176 + Math.max(0, (container.rows - 9) * 18);
        this.ySize = 112 + 18 * Math.min(8, container.rows);
    }

    @Override
    public void func_230430_a_(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks){
        this.func_230446_a_(matrixStack);
        super.func_230430_a_(matrixStack, mouseX, mouseY, partialTicks);
        this.func_230459_a_(matrixStack, mouseX, mouseY);
    }



    @Override
    protected void func_230450_a_(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY){
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.field_230706_i_.getTextureManager().bindTexture(this.texture);
        int i = (this.field_230708_k_ - this.xSize) / 2;
        int j = (this.field_230709_l_ - this.ySize) / 2;
        this.func_238474_b_(matrixStack, i, j, 0, 0, this.xSize, this.ySize);
    }

    @Override
    protected void func_230451_b_(MatrixStack matrixStack, int mouseX, int mouseY){
        if(container.rows >= 9)
            return;
        this.field_230712_o_.func_238422_b_(matrixStack, this.field_230704_d_, 8.0F, 6.0F, 4210752);
        this.field_230712_o_.func_238422_b_(matrixStack, this.playerInventory.getDisplayName(), 8.0F, (float)(this.ySize - 96 + 2), 4210752);
    }
}
