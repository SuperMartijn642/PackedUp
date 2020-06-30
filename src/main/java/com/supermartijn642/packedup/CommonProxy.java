package com.supermartijn642.packedup;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fml.network.NetworkHooks;

/**
 * Created 2/7/2020 by SuperMartijn642
 */
public class CommonProxy {

    public void init(){

    }

    public PlayerEntity getClientPlayer(){
        return null;
    }

    public static void openBackpackInventory(ItemStack stack, PlayerEntity player, int bagSlot){
        BackpackType type = ((BackpackItem)stack.getItem()).type;
        CompoundNBT compound = stack.getOrCreateTag();
        if(!compound.contains("packedup:invIndex") || BackpackStorageManager.getInventory(compound.getInt("packedup:invIndex")) == null){
            compound.putInt("packedup:invIndex", BackpackStorageManager.createInventoryIndex(type));
            stack.setTag(compound);
        }else{
            BackpackInventory inventory = BackpackStorageManager.getInventory(compound.getInt("packedup:invIndex"));
            int rows = inventory.getSlots() / 9;
            if(rows != type.getRows())
                inventory.adjustSize(type.getRows());
        }
        int inventoryIndex = compound.getInt("packedup:invIndex");
        BackpackInventory inventory = BackpackStorageManager.getInventory(inventoryIndex);
        NetworkHooks.openGui((ServerPlayerEntity)player, new BackpackItem.ContainerProvider(stack.getDisplayName(), bagSlot, inventoryIndex, inventory), a -> {
            a.writeInt(bagSlot);
            a.writeInt(inventoryIndex);
            a.writeInt(inventory.rows);
            a.writeInt(inventory.bagsInThisBag.size());
            inventory.bagsInThisBag.forEach(a::writeInt);
            a.writeInt(inventory.bagsThisBagIsIn.size());
            inventory.bagsThisBagIsIn.forEach(a::writeInt);
            a.writeInt(inventory.layer);
        });
    }

}
