package com.supermartijn642.packedup;

import com.supermartijn642.core.TextComponents;
import com.supermartijn642.core.item.BaseItem;
import com.supermartijn642.core.item.ItemProperties;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

/**
 * Created 2/7/2020 by SuperMartijn642
 */
public class BackpackItem extends BaseItem {

    public static final DataComponentType<Integer> INVENTORY_ID = DataComponentType.<Integer>builder().persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.INT).build();
    @SuppressWarnings("ClassEscapesDefinedScope")
    public static final DataComponentType<Icon> ICON_ITEM = DataComponentType.<Icon>builder().persistent(ItemStack.OPTIONAL_CODEC.xmap(Icon::new, Icon::stack)).networkSynchronized(ItemStack.OPTIONAL_STREAM_CODEC.map(Icon::new, Icon::stack)).build();

    public static ItemStack getIcon(ItemStack stack){
        Icon icon = stack.get(ICON_ITEM);
        return icon == null ? ItemStack.EMPTY : icon.stack;
    }

    public static void setIcon(ItemStack stack, ItemStack icon){
        if(icon.isEmpty())
            stack.remove(ICON_ITEM);
        else{
            if(icon.has(ICON_ITEM)){
                icon = icon.copy();
                icon.remove(ICON_ITEM);
            }
            stack.set(ICON_ITEM, new Icon(icon));
        }
    }

    public BackpackType type;

    public BackpackItem(BackpackType type){
        super(type.isEnabled() ? ItemProperties.create().maxStackSize(1).group(PackedUp.ITEM_GROUP) : ItemProperties.create().maxStackSize(1));
        this.type = type;
    }

    @Override
    public ItemUseResult interact(ItemStack stack, Player player, InteractionHand hand, Level level){
        if(!player.isCrouching()){
            if(!level.isClientSide && stack.getItem() instanceof BackpackItem){
                int bagSlot = hand == InteractionHand.MAIN_HAND ? player.getInventory().selected : -1;
                PackedUpCommon.openBackpackInventory(stack, player, bagSlot);
            }
        }else if(level.isClientSide)
            PackedUpClient.openBackpackRenameScreen(hand);
        return ItemUseResult.success(stack);
    }

    @Override
    protected void appendItemInformation(ItemStack stack, Consumer<Component> info, boolean advanced){
        info.accept(TextComponents.translation("packedup.backpacks.info.one", TextComponents.string(Integer.toString(this.type.getSlots())).color(ChatFormatting.GOLD).get()).color(ChatFormatting.AQUA).get());
        Component key = PackedUpClient.getKeyBindCharacter();
        if(key != null)
            info.accept(TextComponents.translation("packedup.backpacks.info.two", key).color(ChatFormatting.AQUA).get());

        if(advanced){
            if(stack.has(INVENTORY_ID))
                info.accept(TextComponents.translation("packedup.backpacks.info.inventory_index", stack.get(INVENTORY_ID)).get());
        }

        super.appendItemInformation(stack, info, advanced);
    }

    @Override
    public boolean isInCreativeGroup(CreativeModeTab tab){
        return this.type.isEnabled() && super.isInCreativeGroup(tab);
    }

    private record Icon(ItemStack stack) {
        @Override
        public boolean equals(Object o){
            if(!(o instanceof Icon icon)) return false;

            return ItemStack.isSameItemSameComponents(this.stack, icon.stack);
        }

        @Override
        public int hashCode(){
            return ItemStack.hashItemAndComponents(this.stack);
        }
    }
}
