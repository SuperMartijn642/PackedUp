package com.supermartijn642.packedup;

import com.mojang.blaze3d.vertex.PoseStack;
import com.supermartijn642.core.ClientUtils;
import com.supermartijn642.core.TextComponents;
import com.supermartijn642.core.gui.ScreenUtils;
import com.supermartijn642.core.gui.widget.BaseContainerWidget;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * Created 2/7/2020 by SuperMartijn642
 */
public class BackpackContainerScreen extends BaseContainerWidget<BackpackContainer> {

    private static final ResourceLocation CORNERS = new ResourceLocation("packedup", "textures/corners.png");

    private Component displayName;

    public BackpackContainerScreen(){
        super(0, 0, 0, 0);
    }

    @Override
    public void initialize(){
        super.initialize();
        this.displayName = trimText(this.container.bagName, this.container.type.getColumns() * 18);
    }

    @Override
    public Component getNarrationMessage(){
        return this.displayName;
    }

    @Override
    public int width(){
        return this.container.type.getColumns() > 9 ? 14 + this.container.type.getColumns() * 18 : 176;
    }

    @Override
    public int height(){
        return 112 + 18 * this.container.type.getRows();
    }

    @Override
    public void renderBackground(PoseStack poseStack, int mouseX, int mouseY){
        if(this.container.type.getColumns() == 9)
            ScreenUtils.drawScreenBackground(poseStack, 0, 0, this.width(), this.height());
        else{
            int backpackWidth = this.container.type.getColumns() * 18 + 14;
            int offset = (this.width() - backpackWidth) / 2;
            int height = this.container.type.getRows() * 18 + 23;
            ScreenUtils.drawScreenBackground(poseStack, offset, 0, backpackWidth, height);
            ScreenUtils.drawScreenBackground(poseStack, Math.max(0, (backpackWidth - 176) / 2f), height - 9, 176, this.height() - height + 9);
            ScreenUtils.bindTexture(CORNERS);
            if(this.container.type.getColumns() > 9){
                ScreenUtils.drawTexture(poseStack, Math.max(0, (backpackWidth - 176) / 2f), height - 3, 3, 3, 0, 0, 0.5f, 0.5f);
                ScreenUtils.drawTexture(poseStack, Math.max(0, (backpackWidth - 176) / 2f) + 176 - 3, height - 3, 3, 3, 0.5f, 0, 0.5f, 0.5f);
                ScreenUtils.fillRect(poseStack, Math.max(0, (backpackWidth - 176) / 2f), height - 9, 176, 6, 0xffC6C6C6);
            }else{
                ScreenUtils.drawTexture(poseStack, offset, height - 9, 3, 3, 0, 0.5f, 0.5f, 0.5f);
                ScreenUtils.drawTexture(poseStack, offset + backpackWidth - 3, height - 9, 3, 3, 0.5f, 0.5f, 0.5f, 0.5f);
                ScreenUtils.fillRect(poseStack, offset + 3, height - 9, backpackWidth - 6, 3, 0xffC6C6C6);
            }
        }
        super.renderBackground(poseStack, mouseX, mouseY);
    }

    @Override
    public void renderForeground(PoseStack poseStack, int mouseX, int mouseY){
        int offset = (this.container.type.getColumns() - 9) * 18 / 2;
        ScreenUtils.drawString(poseStack, this.displayName, 8.0F - Math.min(0, offset), 6.0F, 4210752);
        ScreenUtils.drawString(poseStack, ClientUtils.getPlayer().getInventory().getDisplayName(), 8.0F + Math.max(0, offset), this.height() - 96 + 3, 4210752);

        super.renderForeground(poseStack, mouseX, mouseY);
    }

    private static Component trimText(Component textComponent, int width){
        String text = TextComponents.format(textComponent);
        Font font = ClientUtils.getFontRenderer();
        int length = 0;
        while(length < text.length() && font.width(text.substring(0, length + 1) + "...") < width)
            length++;
        return TextComponents.string(length < text.length() ? text.substring(0, length) + "..." : text).get();
    }
}
