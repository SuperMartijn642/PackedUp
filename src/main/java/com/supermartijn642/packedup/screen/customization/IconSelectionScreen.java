package com.supermartijn642.packedup.screen.customization;

import com.mojang.blaze3d.platform.GlStateManager;
import com.supermartijn642.core.ClientUtils;
import com.supermartijn642.core.TextComponents;
import com.supermartijn642.core.gui.ScreenUtils;
import com.supermartijn642.core.gui.WidgetScreen;
import com.supermartijn642.core.gui.widget.ItemBaseWidget;
import com.supermartijn642.core.util.Holder;
import com.supermartijn642.packedup.BackpackItem;
import com.supermartijn642.packedup.PackedUp;
import com.supermartijn642.packedup.packets.PacketSetIcon;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

/**
 * Created 10/01/2025 by SuperMartijn642
 */
public class IconSelectionScreen extends ItemBaseWidget {

    private static final ResourceLocation SLOT_TEXTURE = new ResourceLocation("supermartijn642corelib", "textures/gui/slot.png");
    private static final ResourceLocation SLOT_HIGHLIGHT_TEXTURE = new ResourceLocation("packedup", "textures/gui/slot_highlight.png");
    private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation("packedup", "textures/gui/preview_frame.png");
    private static final ITextComponent TITLE = TextComponents.translation("packedup.icon_selection_screen.icon.title").get();

    private final Hand hand;

    public IconSelectionScreen(Hand hand){
        super(0, 0, 176, 151,
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
    public void renderBackground(int mouseX, int mouseY, ItemStack stack){
        // Background
        ScreenUtils.bindTexture(BACKGROUND_TEXTURE);
        ScreenUtils.drawTexture(0, 0, this.width(), this.height());

        // Slot hover highlight
        ScreenUtils.bindTexture(SLOT_HIGHLIGHT_TEXTURE);
        forEachSlot((x, y, index) -> {
            if(mouseX >= x && mouseX < x + 18 && mouseY >= y && mouseY < y + 18)
                ScreenUtils.drawTexture(x - 1, y - 1, 20, 20, 0, 0, 0.5f, 1);
        });
        super.renderBackground(mouseX, mouseY, stack);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void render(int mouseX, int mouseY, ItemStack stack){
        // Items
        ItemStack icon = BackpackItem.getIcon(stack);
        Holder<ItemStack> hoveredStack = new Holder<>();
        PlayerInventory inventory = ClientUtils.getPlayer().inventory;
        forEachSlot((x, y, index) -> {
            ItemStack item = inventory.getItem(index);
            ClientUtils.getItemRenderer().renderGuiItem(item, x + 1, y + 1);
            if(!icon.isEmpty() && ItemStack.isSame(icon, item)){
                ScreenUtils.bindTexture(SLOT_HIGHLIGHT_TEXTURE);
                ScreenUtils.drawTexture(x - 1, y - 1, 20, 20, 0.5f, 0, 0.5f, 1);
            }
            if(mouseX >= x && mouseX < x + 18 && mouseY >= y && mouseY < y + 18)
                hoveredStack.set(item);
        });

        // Preview
        GlStateManager.pushMatrix();
        GlStateManager.translatef(67, 7, 0);
        GlStateManager.scalef(42 / 16f, 42 / 16f, 1);
        ItemStack preview = stack;
        if(hoveredStack.get() != null){
            preview = preview.copy();
            BackpackItem.setIcon(preview, hoveredStack.get());
        }
        ClientUtils.getItemRenderer().renderGuiItem(preview, 0, 0);
        GlStateManager.popMatrix();

        super.render(mouseX, mouseY, stack);
    }

    @Override
    protected boolean mouseReleased(int mouseX, int mouseY, int button, boolean hasBeenHandled, ItemStack stack){
        if(!hasBeenHandled && button == 0){
            Holder<ItemStack> hoveredStack = new Holder<>();
            PlayerInventory inventory = ClientUtils.getPlayer().inventory;
            forEachSlot((x, y, index) -> {
                if(mouseX >= x && mouseX < x + 18 && mouseY >= y && mouseY < y + 18)
                    hoveredStack.set(inventory.getItem(index));
            });
            if(hoveredStack.get() != null){
                PackedUp.CHANNEL.sendToServer(new PacketSetIcon(this.hand, hoveredStack.get()));
                ClientUtils.getMinecraft().setScreen(WidgetScreen.of(new BackpackCustomizationScreen(this.hand)));
                hasBeenHandled = true;
            }
        }
        return super.mouseReleased(mouseX, mouseY, button, hasBeenHandled, stack);
    }

    @Override
    protected boolean keyPressed(int keyCode, boolean hasBeenHandled, ItemStack object){
        if(!hasBeenHandled && (ClientUtils.getMinecraft().options.keyInventory.matches(keyCode, 0) || keyCode == 256 /* Escape */)){
            ClientUtils.getMinecraft().setScreen(WidgetScreen.of(new BackpackCustomizationScreen(this.hand)));
            hasBeenHandled = true;
        }
        return super.keyPressed(keyCode, hasBeenHandled, object);
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
