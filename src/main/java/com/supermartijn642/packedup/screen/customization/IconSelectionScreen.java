package com.supermartijn642.packedup.screen.customization;

import com.supermartijn642.core.ClientUtils;
import com.supermartijn642.core.TextComponents;
import com.supermartijn642.core.gui.ScreenUtils;
import com.supermartijn642.core.gui.WidgetScreen;
import com.supermartijn642.core.gui.widget.ItemBaseWidget;
import com.supermartijn642.core.util.Holder;
import com.supermartijn642.packedup.BackpackItem;
import com.supermartijn642.packedup.PackedUp;
import com.supermartijn642.packedup.packets.PacketSetIcon;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import org.lwjgl.input.Keyboard;

/**
 * Created 10/01/2025 by SuperMartijn642
 */
public class IconSelectionScreen extends ItemBaseWidget {

    private static final ResourceLocation SLOT_TEXTURE = new ResourceLocation("supermartijn642corelib", "textures/gui/slot.png");
    private static final ResourceLocation SLOT_HIGHLIGHT_TEXTURE = new ResourceLocation("packedup", "textures/gui/slot_highlight.png");
    private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation("packedup", "textures/gui/preview_frame.png");
    private static final ITextComponent TITLE = TextComponents.translation("packedup.icon_selection_screen.icon.title").get();

    private final EnumHand hand;

    public IconSelectionScreen(EnumHand hand){
        super(0, 0, 176, 151,
            () -> ClientUtils.getPlayer().getHeldItem(hand),
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

    @Override
    public void render(int mouseX, int mouseY, ItemStack stack){
        // Items
        ItemStack icon = BackpackItem.getIcon(stack);
        Holder<ItemStack> hoveredStack = new Holder<>();
        InventoryPlayer inventory = ClientUtils.getPlayer().inventory;
        forEachSlot((x, y, index) -> {
            ItemStack item = inventory.getStackInSlot(index);
            GlStateManager.pushMatrix();
            GlStateManager.translate(x + 1 + 16, y + 1, 100);
            GlStateManager.scale(16, -16, 16);
            GlStateManager.translate(-0.5f, -0.5f, -0.5f);
            ClientUtils.getItemRenderer().renderItem(item, ItemCameraTransforms.TransformType.GUI);
            GlStateManager.popMatrix();
            if(!icon.isEmpty() && icon.isItemEqual(item)){
                ScreenUtils.bindTexture(SLOT_HIGHLIGHT_TEXTURE);
                ScreenUtils.drawTexture(x - 1, y - 1, 20, 20, 0.5f, 0, 0.5f, 1);
            }
            if(mouseX >= x && mouseX < x + 18 && mouseY >= y && mouseY < y + 18)
                hoveredStack.set(item);
        });

        // Preview
        GlStateManager.pushMatrix();
        GlStateManager.translate(67, 7, 0);
        GlStateManager.scale(42 / 16f, 42 / 16f, 1);
        ItemStack preview = stack;
        if(hoveredStack.get() != null){
            preview = preview.copy();
            BackpackItem.setIcon(preview, hoveredStack.get());
        }
        ClientUtils.getItemRenderer().renderItemIntoGUI(preview, 0, 0);
        GlStateManager.popMatrix();

        super.render(mouseX, mouseY, stack);
    }

    @Override
    protected boolean mouseReleased(int mouseX, int mouseY, int button, boolean hasBeenHandled, ItemStack stack){
        if(!hasBeenHandled && button == 0){
            Holder<ItemStack> hoveredStack = new Holder<>();
            InventoryPlayer inventory = ClientUtils.getPlayer().inventory;
            forEachSlot((x, y, index) -> {
                if(mouseX >= x && mouseX < x + 18 && mouseY >= y && mouseY < y + 18)
                    hoveredStack.set(inventory.getStackInSlot(index));
            });
            if(hoveredStack.get() != null){
                PackedUp.CHANNEL.sendToServer(new PacketSetIcon(this.hand, hoveredStack.get()));
                ClientUtils.getMinecraft().displayGuiScreen(WidgetScreen.of(new BackpackCustomizationScreen(this.hand)));
                hasBeenHandled = true;
            }
        }
        return super.mouseReleased(mouseX, mouseY, button, hasBeenHandled, stack);
    }

    @Override
    protected boolean keyPressed(int keyCode, boolean hasBeenHandled, ItemStack object){
        if(!hasBeenHandled && (ClientUtils.getMinecraft().gameSettings.keyBindInventory.isActiveAndMatches(Keyboard.getEventKey()) || keyCode == 256 /* Escape */)){
            ClientUtils.getMinecraft().displayGuiScreen(WidgetScreen.of(new BackpackCustomizationScreen(this.hand)));
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
