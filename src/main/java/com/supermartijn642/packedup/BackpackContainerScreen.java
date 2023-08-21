package com.supermartijn642.packedup;

import com.supermartijn642.core.ClientUtils;
import com.supermartijn642.core.TextComponents;
import com.supermartijn642.core.gui.ScreenUtils;
import com.supermartijn642.core.gui.widget.BaseContainerWidget;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

/**
 * Created 2/7/2020 by SuperMartijn642
 */
public class BackpackContainerScreen extends BaseContainerWidget<BackpackContainer> {

    private static final ResourceLocation CORNERS = new ResourceLocation("packedup", "textures/corners.png");
    private static final int[] KEY_CODE_MAP = new int[]{-1, 256, 49, 50, 51, 52, 53, 54, 55, 56, 57, 48, 45, 61, 259, 258, 81, 87, 69, 82, 84, 89, 85, 73, 79, 80, 91, 93, 257, 341, 65, 83, 68, 70, 71, 72, 74, 75, 76, 59, 39, 96, 340, 92, 90, 88, 67, 86, 66, 78, 77, 44, 46, 47, 344, 332, 342, 32, 280, 290, 291, 292, 293, 294, 295, 296, 297, 298, 299, 282, 281, 327, 328, 329, 333, 324, 325, 326, 334, 321, 322, 323, 320, 330, -1, -1, -1, 300, 301, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 302, 303, 304, 305, 306, 307, -1, -1, -1, -1, -1, -1, -1, 308, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 336, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 335, 345, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 44, -1, 331, -1, 348, 346, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 284, -1, 268, 265, 266, -1, 263, -1, 262, -1, 269, 264, 267, 260, 261, -1, -1, -1, -1, -1, -1, -1, 343, 347, -1, -1, -1};

    private ITextComponent displayName;

    public BackpackContainerScreen(){
        super(0, 0, 0, 0);
    }

    @Override
    public void initialize(){
        super.initialize();
        this.displayName = trimText(this.container.bagName, this.container.type.getColumns() * 18);
    }

    @Override
    public ITextComponent getNarrationMessage(){
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
    public void renderBackground(int mouseX, int mouseY){
        if(this.container.type.getColumns() == 9)
            ScreenUtils.drawScreenBackground(0, 0, this.width(), this.height());
        else{
            int backpackWidth = this.container.type.getColumns() * 18 + 14;
            int offset = (this.width() - backpackWidth) / 2;
            int height = this.container.type.getRows() * 18 + 23;
            ScreenUtils.drawScreenBackground(offset, 0, backpackWidth, height);
            ScreenUtils.drawScreenBackground(Math.max(0, (backpackWidth - 176) / 2f), height - 9, 176, this.height() - height + 9);
            ScreenUtils.bindTexture(CORNERS);
            if(this.container.type.getColumns() > 9){
                ScreenUtils.drawTexture(Math.max(0, (backpackWidth - 176) / 2f), height - 3, 3, 3, 0, 0, 0.5f, 0.5f);
                ScreenUtils.drawTexture(Math.max(0, (backpackWidth - 176) / 2f) + 176 - 3, height - 3, 3, 3, 0.5f, 0, 0.5f, 0.5f);
                ScreenUtils.fillRect(Math.max(0, (backpackWidth - 176) / 2f), height - 9, 176, 6, 0xffC6C6C6);
            }else{
                ScreenUtils.drawTexture(offset, height - 9, 3, 3, 0, 0.5f, 0.5f, 0.5f);
                ScreenUtils.drawTexture(offset + backpackWidth - 3, height - 9, 3, 3, 0.5f, 0.5f, 0.5f, 0.5f);
                ScreenUtils.fillRect(offset + 3, height - 9, backpackWidth - 6, 3, 0xffC6C6C6);
            }
        }
        super.renderBackground(mouseX, mouseY);
    }

    @Override
    public void renderForeground(int mouseX, int mouseY){
        int offset = (this.container.type.getColumns() - 9) * 18 / 2;
        ScreenUtils.drawString(this.displayName, 8.0F - Math.min(0, offset), 6.0F, 4210752);
        ScreenUtils.drawString(ClientUtils.getPlayer().inventory.getDisplayName(), 8.0F + Math.max(0, offset), this.height() - 96 + 3, 4210752);

        super.renderForeground(mouseX, mouseY);
    }

    @Override
    public boolean keyPressed(int keyCode, boolean hasBeenHandled){
        hasBeenHandled |= super.keyPressed(keyCode, hasBeenHandled);
        if(!hasBeenHandled && PackedUpClient.OPEN_BAG_KEY.getKeyCode() < KEY_CODE_MAP.length && KEY_CODE_MAP[PackedUpClient.OPEN_BAG_KEY.getKeyCode()] == keyCode){
            this.container.player.closeScreen();
            hasBeenHandled = true;
        }
        return hasBeenHandled;
    }

    private static ITextComponent trimText(ITextComponent textComponent, int width){
        String text = TextComponents.format(textComponent);
        FontRenderer font = ClientUtils.getFontRenderer();
        int length = 0;
        while(length < text.length() && font.getStringWidth(text.substring(0, length + 1) + "...") < width)
            length++;
        return TextComponents.string(length < text.length() ? text.substring(0, length) + "..." : text).get();
    }
}
