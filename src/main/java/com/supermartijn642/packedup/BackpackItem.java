package com.supermartijn642.packedup;

import com.supermartijn642.core.TextComponents;
import com.supermartijn642.core.item.BaseItem;
import com.supermartijn642.core.item.ItemProperties;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
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
    public ItemUseResult interact(ItemStack stack, PlayerEntity player, Hand hand, World level){
        if(!player.isSneaking()){
            if(!level.isClientSide && stack.getItem() instanceof BackpackItem){
                int bagSlot = hand == Hand.MAIN_HAND ? player.inventory.selected : -1;
                PackedUpCommon.openBackpackInventory(stack, player, bagSlot);
            }
        }else if(level.isClientSide)
            PackedUpClient.openBackpackRenameScreen(TextComponents.item(stack.getItem()).format(), TextComponents.itemStack(stack).format());
        return ItemUseResult.success(stack);
    }

    @Override
    protected void appendItemInformation(ItemStack stack, @Nullable IBlockReader level, Consumer<ITextComponent> info, boolean advanced){
        info.accept(TextComponents.translation("packedup.backpacks.info.one", TextComponents.string(Integer.toString(this.type.getSlots())).color(TextFormatting.GOLD).get()).color(TextFormatting.AQUA).get());
        ITextComponent key = PackedUpClient.getKeyBindCharacter();
        if(key != null)
            info.accept(TextComponents.translation("packedup.backpacks.info.two", key).color(TextFormatting.AQUA).get());

        if(advanced){
            CompoundNBT compound = stack.getOrCreateTag();
            if(compound.contains("packedup:invIndex"))
                info.accept(TextComponents.translation("packedup.backpacks.info.inventory_index", compound.getInt("packedup:invIndex")).get());
        }

        super.appendItemInformation(stack, level, info, advanced);
    }

    @Override
    public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> items){
        if(this.type.isEnabled())
            super.fillItemCategory(group, items);
    }

    @Override
    public boolean isFireResistant(){
        return !PackedUpConfig.canBackpacksBurn.get();
    }
}
