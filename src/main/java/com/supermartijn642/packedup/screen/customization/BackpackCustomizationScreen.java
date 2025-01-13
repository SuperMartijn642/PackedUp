package com.supermartijn642.packedup.screen.customization;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.supermartijn642.core.ClientUtils;
import com.supermartijn642.core.TextComponents;
import com.supermartijn642.core.gui.ScreenUtils;
import com.supermartijn642.core.gui.WidgetScreen;
import com.supermartijn642.core.gui.widget.ItemBaseWidget;
import com.supermartijn642.core.gui.widget.premade.TextFieldWidget;
import com.supermartijn642.packedup.BackpackItem;
import com.supermartijn642.packedup.PackedUp;
import com.supermartijn642.packedup.packets.PacketRename;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;

/**
 * Created 4/29/2020 by SuperMartijn642
 */
public class BackpackCustomizationScreen extends ItemBaseWidget {

    private static final int MAX_NAME_CHARACTER_COUNT = 23;
    private static final ITextComponent TITLE = TextComponents.translation("packedup.customization_screen.title").get();

    private final Hand hand;
    private TextFieldWidget nameField;

    public BackpackCustomizationScreen(Hand hand){
        super(0, 0, 192, 48,
            () -> ClientUtils.getPlayer().getItemInHand(hand),
            s -> !s.isEmpty() && s.getItem() instanceof BackpackItem
        );
        this.hand = hand;
    }

    @Override
    public ITextComponent getNarrationMessage(ItemStack stack){
        return TITLE;
    }

    @Override
    protected void addWidgets(ItemStack stack){
        String currentName = TextComponents.itemStack(stack).format();
        String defaultName = TextComponents.item(stack.getItem()).format();
        this.nameField = this.addWidget(new TextFieldWidget(34, 20, 150, 20, currentName, MAX_NAME_CHARACTER_COUNT,
            name -> PackedUp.CHANNEL.sendToServer(new PacketRename(this.hand, name))
        ));
        this.nameField.setSuggestion(defaultName);
        this.nameField.setFocused(true);
        this.addWidget(new IconSlotWidget(8, 20, this.hand,
            () -> this.object,
            () -> ClientUtils.getMinecraft().setScreen(WidgetScreen.of(new IconSelectionScreen(this.hand)))
        ));
    }

    @Override
    protected void renderBackground(MatrixStack poseStack, int mouseX, int mouseY, ItemStack stack){
        ScreenUtils.drawScreenBackground(poseStack, 0, 0, this.width(), this.height());
        super.renderBackground(poseStack, mouseX, mouseY, stack);
    }

    @Override
    public void render(MatrixStack poseStack, int mouseX, int mouseY, ItemStack stack){
        ScreenUtils.drawString(poseStack, TITLE, 9, 7, 4210752);
        super.render(poseStack, mouseX, mouseY, stack);
    }

    @Override
    public boolean keyPressed(int keyCode, boolean hasBeenHandled, ItemStack stack){
        if(!hasBeenHandled && this.nameField.isSelected() && keyCode == 257 /* Enter */){
            ClientUtils.getPlayer().closeContainer();
            hasBeenHandled = true;
        }
        hasBeenHandled |= super.keyPressed(keyCode, hasBeenHandled, stack);
        return hasBeenHandled;
    }
}
