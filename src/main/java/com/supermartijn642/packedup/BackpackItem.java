package com.supermartijn642.packedup;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

/**
 * Created 2/7/2020 by SuperMartijn642
 */
public class BackpackItem extends Item {

    public BackpackType type;

    public BackpackItem(BackpackType type){
        super();
        this.type = type;
        this.setRegistryName(type.getRegistryName());
        this.setUnlocalizedName(PackedUp.MODID + ":" + type.getRegistryName());

        this.setMaxStackSize(1);
        if(type.isEnabled())
            this.setCreativeTab(CreativeTabs.SEARCH);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn){
        ItemStack stack = playerIn.getHeldItem(handIn);
        if(!playerIn.isSneaking()){
            if(!worldIn.isRemote && stack.getItem() instanceof BackpackItem){
                int bagSlot = handIn == EnumHand.MAIN_HAND ? playerIn.inventory.currentItem : -1;
                CommonProxy.openBackpackInventory(stack,playerIn,bagSlot);
            }
        }else if(worldIn.isRemote){
            ClientProxy.openScreen(stack.getItem().getItemStackDisplayName(stack), stack.getDisplayName());
        }
        return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
    }
}
