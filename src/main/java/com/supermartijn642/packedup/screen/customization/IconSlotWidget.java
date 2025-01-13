package com.supermartijn642.packedup.screen.customization;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.supermartijn642.core.ClientUtils;
import com.supermartijn642.core.TextComponents;
import com.supermartijn642.core.gui.ScreenUtils;
import com.supermartijn642.core.gui.widget.BaseWidget;
import com.supermartijn642.packedup.BackpackItem;
import com.supermartijn642.packedup.PackedUp;
import com.supermartijn642.packedup.packets.PacketSetIcon;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Created 11/01/2025 by SuperMartijn642
 */
public class IconSlotWidget extends BaseWidget {

    private static final ResourceLocation ICON_SLOT_TEXTURE = new ResourceLocation("packedup", "textures/gui/icon_slot.png");

    private final Hand hand;
    private final Supplier<ItemStack> backpackSupplier;
    private final Runnable selectionScreen;

    public IconSlotWidget(int x, int y, Hand hand, Supplier<ItemStack> backpack, Runnable selectionScreen){
        super(x, y, 20, 20);
        this.hand = hand;
        this.backpackSupplier = backpack;
        this.selectionScreen = selectionScreen;
    }

    @Override
    public ITextComponent getNarrationMessage(){
        return TextComponents.translation("packedup.customization_screen.icon").get();
    }

    @Override
    public boolean mouseReleased(int mouseX, int mouseY, int button, boolean hasBeenHandled){
        if(!hasBeenHandled && this.isFocused()){
            if(button == 0) // Left click
                this.selectionScreen.run();
            else if(button == 1){ // Right click
                PackedUp.CHANNEL.sendToServer(new PacketSetIcon(this.hand, ItemStack.EMPTY));
                hasBeenHandled = true;
            }
        }
        return super.mousePressed(mouseX, mouseY, button, hasBeenHandled);
    }

    @Override
    protected void getTooltips(Consumer<ITextComponent> tooltips){
        // Get the icon item
        ItemStack icon = BackpackItem.getIcon(this.backpackSupplier.get());
        tooltips.accept(TextComponents.translation("packedup.customization_screen.icon").get());
        if(icon.isEmpty())
            tooltips.accept(TextComponents.translation("packedup.customization_screen.icon.none").italic().color(TextFormatting.DARK_GRAY).get());
        else
            tooltips.accept(TextComponents.itemStack(icon).italic().color(TextFormatting.GRAY).get());
        tooltips.accept(TextComponents.translation("packedup.customization_screen.icon.select", TextComponents.translation("packedup.customization_screen.icon.left_click").color(TextFormatting.GOLD).get()).color(TextFormatting.WHITE).get());
        tooltips.accept(TextComponents.translation("packedup.customization_screen.icon.clear", TextComponents.translation("packedup.customization_screen.icon.right_click").color(TextFormatting.GOLD).get()).color(TextFormatting.WHITE).get());
    }

    @Override
    public void renderBackground(MatrixStack poseStack, int mouseX, int mouseY){
        super.renderBackground(poseStack, mouseX, mouseY);
        ScreenUtils.bindTexture(ICON_SLOT_TEXTURE);
        ScreenUtils.drawTexture(poseStack, this.x, this.y, this.width, this.height, this.isFocused() ? 0.5f : 0, 0, 0.5f, 1);
    }

    @Override
    public void render(MatrixStack poseStack, int mouseX, int mouseY){
        super.render(poseStack, mouseX, mouseY);
        ClientUtils.getItemRenderer().renderGuiItem(this.backpackSupplier.get(), this.x + 2, this.y + 2);
    }
}
