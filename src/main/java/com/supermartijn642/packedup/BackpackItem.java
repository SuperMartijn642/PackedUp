package com.supermartijn642.packedup;

import com.supermartijn642.core.TextComponents;
import com.supermartijn642.core.item.BaseItem;
import com.supermartijn642.core.item.ItemProperties;
import com.supermartijn642.packedup.compat.Compatibility;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

/**
 * Created 2/7/2020 by SuperMartijn642
 */
public class BackpackItem extends BaseItem implements ICapabilityProvider {

    public BackpackType type;

    public BackpackItem(BackpackType type){
        super(type.isEnabled() ? ItemProperties.create().maxStackSize(1).group(PackedUp.ITEM_GROUP) : ItemProperties.create().maxStackSize(1));
        this.type = type;
    }

    @Override
    public ItemUseResult interact(ItemStack stack, EntityPlayer player, EnumHand hand, World level){
        if(!player.isSneaking()){
            if(!level.isRemote && stack.getItem() instanceof BackpackItem){
                int bagSlot = hand == EnumHand.MAIN_HAND ? player.inventory.currentItem : -1;
                PackedUpCommon.openBackpackInventory(stack, player, bagSlot);
            }
        }else if(level.isRemote)
            PackedUpClient.openBackpackRenameScreen(TextComponents.item(stack.getItem()).format(), TextComponents.itemStack(stack).format());
        return ItemUseResult.success(stack);
    }

    @Override
    protected void appendItemInformation(ItemStack stack, @Nullable IBlockAccess level, Consumer<ITextComponent> info, boolean advanced){
        info.accept(TextComponents.translation("packedup.backpacks.info.one", TextComponents.string(Integer.toString(this.type.getSlots())).color(TextFormatting.GOLD).get()).color(TextFormatting.AQUA).get());
        ITextComponent key = PackedUpClient.getKeyBindCharacter();
        if(key != null)
            info.accept(TextComponents.translation("packedup.backpacks.info.two", key).color(TextFormatting.AQUA).get());

        if(advanced){
            NBTTagCompound compound = stack.getTagCompound();
            if(compound != null && compound.hasKey("packedup:invIndex"))
                info.accept(TextComponents.translation("packedup.backpacks.info.inventory_index", compound.getInteger("packedup:invIndex")).get());
        }

        super.appendItemInformation(stack, level, info, advanced);
    }

    @Override
    public void getSubItems(CreativeTabs group, NonNullList<ItemStack> items){
        if(this.type.isEnabled())
            super.getSubItems(group, items);
    }

    @Override
    public boolean isFireResistant(){
        return !PackedUpConfig.canBackpacksBurn.get();
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt){
        return this;
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing){
        return Compatibility.BAUBLES.isBaubleCapability(capability);
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing){
        return Compatibility.BAUBLES.isBaubleCapability(capability) ? Compatibility.BAUBLES.getBaubleCapability(capability, this) : null;
    }
}
