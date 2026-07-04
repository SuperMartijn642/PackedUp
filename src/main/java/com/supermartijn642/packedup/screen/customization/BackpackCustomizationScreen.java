package com.supermartijn642.packedup.screen.customization;

import com.supermartijn642.core.ClientUtils;
import com.supermartijn642.core.TextComponents;
import com.supermartijn642.core.gui.GuiGraphicsHelper;
import com.supermartijn642.core.gui.WidgetScreen;
import com.supermartijn642.core.gui.widget.ItemBaseWidget;
import com.supermartijn642.core.gui.widget.WidgetRenderContext;
import com.supermartijn642.core.gui.widget.premade.TextFieldWidget;
import com.supermartijn642.packedup.BackpackItem;
import com.supermartijn642.packedup.PackedUp;
import com.supermartijn642.packedup.packets.PacketRename;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

/**
 * Created 4/29/2020 by SuperMartijn642
 */
public class BackpackCustomizationScreen extends ItemBaseWidget {

    private static final int MAX_NAME_CHARACTER_COUNT = 23;
    private static final Component TITLE = TextComponents.translation("packedup.customization_screen.title").get();

    private final InteractionHand hand;
    private TextFieldWidget nameField;

    public BackpackCustomizationScreen(InteractionHand hand){
        super(0, 0, 192, 48,
            () -> ClientUtils.getPlayer().getItemInHand(hand),
            s -> !s.isEmpty() && s.getItem() instanceof BackpackItem
        );
        this.hand = hand;
    }

    @Override
    public Component getNarrationMessage(ItemStack stack){
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
            () -> ClientUtils.displayScreen(WidgetScreen.of(new IconSelectionScreen(this.hand)))
        ));
    }

    @Override
    protected void renderBackground(WidgetRenderContext context, GuiGraphicsHelper graphics, int mouseX, int mouseY, ItemStack stack){
        graphics.submitDefaultScreenBackground(0, 0, this.width(), this.height());
        super.renderBackground(context, graphics, mouseX, mouseY, stack);
    }

    @Override
    public void render(WidgetRenderContext context, GuiGraphicsHelper graphics, int mouseX, int mouseY, ItemStack stack){
        graphics.submitText(TITLE, 9, 7, p -> p.color(4210752));
        super.render(context, graphics, mouseX, mouseY, stack);
    }

    @Override
    public boolean keyPressed(KeyEvent event, boolean hasBeenHandled, ItemStack stack){
        if(!hasBeenHandled && this.nameField.isSelected() && event.isConfirmation()){
            ((LocalPlayer)ClientUtils.getPlayer()).closeContainer();
            hasBeenHandled = true;
        }
        hasBeenHandled |= super.keyPressed(event, hasBeenHandled, stack);
        return hasBeenHandled;
    }
}
