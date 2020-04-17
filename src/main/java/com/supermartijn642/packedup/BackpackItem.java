package com.supermartijn642.packedup;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
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
        if(!worldIn.isRemote){
            ItemStack stack = playerIn.getHeldItem(handIn);
            NBTTagCompound compound = stack.getTagCompound();
            if(compound == null)
                compound = new NBTTagCompound();
            if(!compound.hasKey("packedup:invIndex")){
                compound.setInteger("packedup:invIndex", BackpackStorageManager.createInventoryIndex(this.type));
                stack.setTagCompound(compound);
            }else{
                BackpackInventory inventory = BackpackStorageManager.getInventory(compound.getInteger("packedup:invIndex"));
                int rows = inventory.getSlots() / 9;
                if(rows != this.type.getRows())
                    inventory.adjustSize(this.type.getRows() * 9);
            }
            playerIn.openGui(PackedUp.instance, this.type.getRows(), worldIn, (int)playerIn.posX, (int)playerIn.posY, (int)playerIn.posZ);
        }
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }
}
