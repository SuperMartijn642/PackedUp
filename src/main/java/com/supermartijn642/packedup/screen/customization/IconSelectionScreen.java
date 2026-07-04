package com.supermartijn642.packedup.screen.customization;

import com.supermartijn642.core.ClientUtils;
import com.supermartijn642.core.TextComponents;
import com.supermartijn642.core.gui.GuiGraphicsHelper;
import com.supermartijn642.core.gui.WidgetScreen;
import com.supermartijn642.core.gui.widget.ItemBaseWidget;
import com.supermartijn642.core.gui.widget.WidgetRenderContext;
import com.supermartijn642.core.util.Holder;
import com.supermartijn642.packedup.BackpackItem;
import com.supermartijn642.packedup.PackedUp;
import com.supermartijn642.packedup.packets.PacketSetIcon;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

/**
 * Created 10/01/2025 by SuperMartijn642
 */
public class IconSelectionScreen extends ItemBaseWidget {

    public static final Identifier SLOT_HIGHLIGHT_TEXTURE = Identifier.fromNamespaceAndPath("packedup", "gui/slot_highlight");
    public static final Identifier BACKGROUND_TEXTURE = Identifier.fromNamespaceAndPath("packedup", "gui/preview_frame");
    private static final Component TITLE = TextComponents.translation("packedup.icon_selection_screen.icon.title").get();

    private final InteractionHand hand;

    public IconSelectionScreen(InteractionHand hand){
        super(0, 0, 176, 151,
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
    public void renderBackground(WidgetRenderContext context, GuiGraphicsHelper graphics, int mouseX, int mouseY, ItemStack stack){
        // Background
        graphics.submitSprite(BACKGROUND_TEXTURE, 0, 0, this.width(), this.height());

        // Slot hover highlight
        forEachSlot((x, y, index) -> {
            if(mouseX >= x && mouseX < x + 18 && mouseY >= y && mouseY < y + 18)
                graphics.submitSprite(SLOT_HIGHLIGHT_TEXTURE, x - 1, y - 1, 20, 20, p -> p.uv(0, 0, 0.5f, 1));
        });
        super.renderBackground(context, graphics, mouseX, mouseY, stack);
    }

    @Override
    public void render(WidgetRenderContext context, GuiGraphicsHelper graphics, int mouseX, int mouseY, ItemStack stack){
        // Items
        ItemStack icon = BackpackItem.getIcon(stack);
        Holder<ItemStack> hoveredStack = new Holder<>();
        Inventory inventory = ClientUtils.getPlayer().getInventory();
        forEachSlot((x, y, index) -> {
            ItemStack item = inventory.getItem(index);
            graphics.submitItem(item, x + 1, y + 1, p -> p.level(ClientUtils.getWorld()));
            if(!icon.isEmpty() && ItemStack.isSameItem(icon, item))
                graphics.submitSprite(SLOT_HIGHLIGHT_TEXTURE, x - 1, y - 1, 20, 20, p -> p.uv(0.5f, 0, 0.5f, 1));
            if(mouseX >= x && mouseX < x + 18 && mouseY >= y && mouseY < y + 18)
                hoveredStack.set(item);
        });

        // Preview
        graphics.poseStack().pushMatrix();
        graphics.poseStack().translate(67, 7);
        graphics.poseStack().scale(42 / 16f, 42 / 16f);
        ItemStack preview = stack;
        if(hoveredStack.get() != null){
            preview = preview.copy();
            BackpackItem.setIcon(preview, hoveredStack.get());
        }
        graphics.submitItem(preview, 0, 0, p -> p.level(ClientUtils.getWorld()));
        graphics.poseStack().popMatrix();

        super.render(context, graphics, mouseX, mouseY, stack);
    }

    @Override
    protected boolean mouseReleased(int mouseX, int mouseY, MouseButtonInfo info, boolean hasBeenHandled, ItemStack stack){
        if(!hasBeenHandled && info.button() == 0){
            Holder<ItemStack> hoveredStack = new Holder<>();
            Inventory inventory = ClientUtils.getPlayer().getInventory();
            forEachSlot((x, y, index) -> {
                if(mouseX >= x && mouseX < x + 18 && mouseY >= y && mouseY < y + 18)
                    hoveredStack.set(inventory.getItem(index));
            });
            if(hoveredStack.get() != null){
                PackedUp.CHANNEL.sendToServer(new PacketSetIcon(this.hand, hoveredStack.get()));
                ClientUtils.displayScreen(WidgetScreen.of(new BackpackCustomizationScreen(this.hand)));
                hasBeenHandled = true;
            }
        }
        return super.mouseReleased(mouseX, mouseY, info, hasBeenHandled, stack);
    }

    @Override
    protected boolean keyPressed(KeyEvent event, boolean hasBeenHandled, ItemStack object){
        if(!hasBeenHandled && (ClientUtils.getMinecraft().options.keyInventory.matches(event) || event.isEscape())){
            ClientUtils.displayScreen(WidgetScreen.of(new BackpackCustomizationScreen(this.hand)));
            hasBeenHandled = true;
        }
        return super.keyPressed(event, hasBeenHandled, object);
    }

    /**
     * Iterates over all slot positions
     */
    private static void forEachSlot(SlotFunction function){
        // Inventory
        for(int row = 0; row < 3; row++){
            for(int column = 0; column < 9; column++){
                int x = 7 + column * 18, y = 68 + row * 18;
                function.apply(x, y, row * 9 + column + 9);
            }
        }
        // Hotbar
        for(int column = 0; column < 9; column++){
            int x = 7 + column * 18, y = 126;
            function.apply(x, y, column);
        }
    }

    private interface SlotFunction {
        void apply(int x, int y, int index);
    }
}
