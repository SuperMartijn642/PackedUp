package com.supermartijn642.packedup;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fmllegacy.network.NetworkHooks;

/**
 * Created 2/7/2020 by SuperMartijn642
 */
public class CommonProxy {

    public static void openBackpackInventory(ItemStack stack, Player player, int bagSlot){
        BackpackType type = ((BackpackItem)stack.getItem()).type;
        CompoundTag compound = stack.getOrCreateTag();
        if(!compound.contains("packedup:invIndex") || BackpackStorageManager.getInventory(compound.getInt("packedup:invIndex")) == null){
            compound.putInt("packedup:invIndex", BackpackStorageManager.createInventoryIndex(type));
            stack.setTag(compound);
        }else
            BackpackStorageManager.getInventory(compound.getInt("packedup:invIndex")).adjustSize(type);
        int inventoryIndex = compound.getInt("packedup:invIndex");
        BackpackInventory inventory = BackpackStorageManager.getInventory(inventoryIndex);
        NetworkHooks.openGui((ServerPlayer)player, new BackpackItem.ContainerProvider(stack.getHoverName(), bagSlot, inventoryIndex, inventory, type), a -> {
            a.writeInt(bagSlot);
            a.writeInt(inventoryIndex);
            a.writeInt(type.ordinal());
            a.writeInt(inventory.bagsInThisBag.size());
            inventory.bagsInThisBag.forEach(a::writeInt);
            a.writeInt(inventory.bagsThisBagIsIn.size());
            inventory.bagsThisBagIsIn.forEach(a::writeInt);
            a.writeInt(inventory.layer);
        });
    }

}
