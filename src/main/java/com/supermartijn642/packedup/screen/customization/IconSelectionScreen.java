package com.supermartijn642.packedup.screen.customization;

import com.mojang.blaze3d.vertex.PoseStack;
import com.supermartijn642.core.ClientUtils;
import com.supermartijn642.core.TextComponents;
import com.supermartijn642.core.gui.ScreenUtils;
import com.supermartijn642.core.gui.WidgetScreen;
import com.supermartijn642.core.gui.widget.ItemBaseWidget;
import com.supermartijn642.core.util.Holder;
import com.supermartijn642.packedup.BackpackItem;
import com.supermartijn642.packedup.PackedUp;
import com.supermartijn642.packedup.packets.PacketSetIcon;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

/**
 * Created 10/01/2025 by SuperMartijn642
 */
public class IconSelectionScreen extends ItemBaseWidget {

    private static final ResourceLocation SLOT_TEXTURE = new ResourceLocation("supermartijn642corelib", "textures/gui/slot.png");
    private static final ResourceLocation SLOT_HIGHLIGHT_TEXTURE = new ResourceLocation("packedup", "textures/gui/slot_highlight.png");
    private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation("packedup", "textures/gui/preview_frame.png");
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
    public void renderBackground(PoseStack poseStack, int mouseX, int mouseY, ItemStack stack){
        // Background
        ScreenUtils.bindTexture(BACKGROUND_TEXTURE);
        ScreenUtils.drawTexture(poseStack, 0, 0, this.width(), this.height());

        // Slot hover highlight
        ScreenUtils.bindTexture(SLOT_HIGHLIGHT_TEXTURE);
        forEachSlot((x, y, index) -> {
            if(mouseX >= x && mouseX < x + 18 && mouseY >= y && mouseY < y + 18)
                ScreenUtils.drawTexture(poseStack, x - 1, y - 1, 20, 20, 0, 0, 0.5f, 1);
        });
        super.renderBackground(poseStack, mouseX, mouseY, stack);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, ItemStack stack){
        // Items
        ItemStack icon = BackpackItem.getIcon(stack);
        Holder<ItemStack> hoveredStack = new Holder<>();
        Inventory inventory = ClientUtils.getPlayer().getInventory();
        forEachSlot((x, y, index) -> {
            ItemStack item = inventory.getItem(index);
            ClientUtils.getItemRenderer().renderGuiItem(poseStack, item, x + 1, y + 1);
            if(!icon.isEmpty() && ItemStack.isSame(icon, item)){
                ScreenUtils.bindTexture(SLOT_HIGHLIGHT_TEXTURE);
                ScreenUtils.drawTexture(poseStack, x - 1, y - 1, 20, 20, 0.5f, 0, 0.5f, 1);
            }
            if(mouseX >= x && mouseX < x + 18 && mouseY >= y && mouseY < y + 18)
                hoveredStack.set(item);
        });

        // Preview
        poseStack.pushPose();
        poseStack.translate(67, 7, 0);
        poseStack.scale(42 / 16f, 42 / 16f, 1);
        ItemStack preview = stack;
        if(hoveredStack.get() != null){
            preview = preview.copy();
            BackpackItem.setIcon(preview, hoveredStack.get());
        }
        ClientUtils.getItemRenderer().renderGuiItem(poseStack, preview, 0, 0);
        poseStack.popPose();

        super.render(poseStack, mouseX, mouseY, stack);
    }

    @Override
    protected boolean mouseReleased(int mouseX, int mouseY, int button, boolean hasBeenHandled, ItemStack stack){
        if(!hasBeenHandled && button == 0){
            Holder<ItemStack> hoveredStack = new Holder<>();
            Inventory inventory = ClientUtils.getPlayer().getInventory();
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
