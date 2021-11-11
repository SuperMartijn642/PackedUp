package com.supermartijn642.packedup;

import com.supermartijn642.core.TextComponents;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Created 2/7/2020 by SuperMartijn642
 */
public class BackpackItem extends Item implements ICapabilityProvider {

    public BackpackType type;

    public BackpackItem(BackpackType type){
        super();
        this.type = type;
        this.setRegistryName(type.getRegistryName());
        this.setUnlocalizedName(PackedUp.MODID + "." + type.getRegistryName());

        this.setMaxStackSize(1);
        this.setCreativeTab(PackedUp.ITEM_GROUP);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn){
        ItemStack stack = playerIn.getHeldItem(handIn);
        if(!playerIn.isSneaking()){
            if(!worldIn.isRemote && stack.getItem() instanceof BackpackItem){
                int bagSlot = handIn == EnumHand.MAIN_HAND ? playerIn.inventory.currentItem : -1;
                CommonProxy.openBackpackInventory(stack, playerIn, bagSlot);
            }
        }else if(worldIn.isRemote)
            ClientProxy.openScreen(TextComponents.item(stack.getItem()).format(), TextComponents.itemStack(stack).format());
        return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn){
        tooltip.add(TextComponents.translation("packedup.backpacks.info.one", TextComponents.string(Integer.toString(this.type.getSlots())).color(TextFormatting.GOLD).get()).color(TextFormatting.AQUA).format());
        ITextComponent key = ClientProxy.getKeyBindCharacter();
        if(key != null)
            tooltip.add(TextComponents.translation("packedup.backpacks.info.two", key).color(TextFormatting.AQUA).format());

        if(flagIn.isAdvanced()){
            NBTTagCompound compound = stack.getTagCompound();
            if(compound != null && compound.hasKey("packedup:invIndex"))
                tooltip.add(TextComponents.translation("packedup.backpacks.info.inventory_index", compound.getInteger("packedup:invIndex")).format());
        }

        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    @Override
    protected boolean isInCreativeTab(CreativeTabs targetTab){
        return this.type.isEnabled() && super.isInCreativeTab(targetTab);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt){
        return this;
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing){
        return PackedUp.baubles.isBaubleCapability(capability);
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing){
        return PackedUp.baubles.isBaubleCapability(capability) ? PackedUp.baubles.getBaubleCapability(capability, this) : null;
    }
}
