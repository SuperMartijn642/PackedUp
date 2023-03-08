package com.supermartijn642.packedup;

import com.supermartijn642.core.TextComponents;
import com.supermartijn642.core.item.BaseItem;
import com.supermartijn642.core.item.ItemProperties;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * Created 2/7/2020 by SuperMartijn642
 */
public class BackpackItem extends BaseItem {

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
            PackedUpClient.openBackpackRenameScreen(TextComponents.item(stack.getItem()).format(), TextComponents.itemStack(stack).format());
        return ItemUseResult.success(stack);
    }

    @Override
    protected void appendItemInformation(ItemStack stack, @Nullable BlockGetter level, Consumer<Component> info, boolean advanced){
        info.accept(TextComponents.translation("packedup.backpacks.info.one", TextComponents.string(Integer.toString(this.type.getSlots())).color(ChatFormatting.GOLD).get()).color(ChatFormatting.AQUA).get());
        Component key = PackedUpClient.getKeyBindCharacter();
        if(key != null)
            info.accept(TextComponents.translation("packedup.backpacks.info.two", key).color(ChatFormatting.AQUA).get());

        if(advanced){
            CompoundTag compound = stack.getOrCreateTag();
            if(compound.contains("packedup:invIndex"))
                info.accept(TextComponents.translation("packedup.backpacks.info.inventory_index", compound.getInt("packedup:invIndex")).get());
        }

        super.appendItemInformation(stack, level, info, advanced);
    }

    @Override
    public boolean isInCreativeGroup(CreativeModeTab tab){
        return this.type.isEnabled() && super.isInCreativeGroup(tab);
    }

    @Override
    public boolean isFireResistant(){
        return !PackedUpConfig.canBackpacksBurn.get();
    }
}
