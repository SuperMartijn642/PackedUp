package com.supermartijn642.packedup.screen;

import com.supermartijn642.core.ClientUtils;
import com.supermartijn642.core.TextComponents;
import com.supermartijn642.core.gui.GuiGraphicsHelper;
import com.supermartijn642.core.gui.widget.BaseContainerWidget;
import com.supermartijn642.core.gui.widget.WidgetRenderContext;
import com.supermartijn642.packedup.PackedUpClient;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * Created 2/7/2020 by SuperMartijn642
 */
public class BackpackContainerScreen extends BaseContainerWidget<BackpackContainer> {

    public static final ResourceLocation CORNERS = ResourceLocation.fromNamespaceAndPath("packedup", "gui/corners");

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
    public void renderBackground(WidgetRenderContext context, GuiGraphicsHelper graphics, int mouseX, int mouseY){
        if(this.container.type.getColumns() == 9)
            graphics.submitDefaultScreenBackground(0, 0, this.width(), this.height());
        else{
            int backpackWidth = this.container.type.getColumns() * 18 + 14;
            int offset = (this.width() - backpackWidth) / 2;
            int height = this.container.type.getRows() * 18 + 23;
            graphics.submitDefaultScreenBackground(offset, 0, backpackWidth, height);
            graphics.submitDefaultScreenBackground(Math.max(0, (backpackWidth - 176) / 2f), height - 9, 176, this.height() - height + 9);
            if(this.container.type.getColumns() > 9){
                graphics.submitSprite(CORNERS, Math.max(0, (backpackWidth - 176) / 2f), height - 3, 3, 3, p -> p.uv(0, 0, 0.5f, 0.5f));
                graphics.submitSprite(CORNERS, Math.max(0, (backpackWidth - 176) / 2f) + 176 - 3, height - 3, 3, 3, p -> p.uv(0.5f, 0, 0.5f, 0.5f));
                graphics.submitRectangle(Math.max(0, (backpackWidth - 176) / 2f), height - 9, 176, 6, p -> p.color(0xffC6C6C6));
            }else{
                graphics.submitSprite(CORNERS, offset, height - 9, 3, 3, p -> p.uv(0, 0.5f, 0.5f, 0.5f));
                graphics.submitSprite(CORNERS, offset + backpackWidth - 3, height - 9, 3, 3, p -> p.uv(0.5f, 0.5f, 0.5f, 0.5f));
                graphics.submitRectangle(offset + 3, height - 9, backpackWidth - 6, 3, p -> p.color(0xffC6C6C6));
            }
        }
        super.renderBackground(context, graphics, mouseX, mouseY);
    }

    @Override
    public void renderForeground(WidgetRenderContext context, GuiGraphicsHelper graphics, int mouseX, int mouseY){
        int offset = (this.container.type.getColumns() - 9) * 18 / 2;
        graphics.submitText(this.displayName, 8 - Math.min(0, offset), 6, p -> p.color(4210752));
        graphics.submitText(ClientUtils.getPlayer().getInventory().getDisplayName(), 8 + Math.max(0, offset), this.height() - 96 + 3, p -> p.color(4210752));

        super.renderForeground(context, graphics, mouseX, mouseY);
    }

    @Override
    public boolean keyPressed(int keyCode, boolean hasBeenHandled){
        hasBeenHandled |= super.keyPressed(keyCode, hasBeenHandled);
        if(!hasBeenHandled && PackedUpClient.OPEN_BAG_KEY.matches(keyCode, -1)){
            this.container.player.closeContainer();
            hasBeenHandled = true;
        }
        return hasBeenHandled;
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
