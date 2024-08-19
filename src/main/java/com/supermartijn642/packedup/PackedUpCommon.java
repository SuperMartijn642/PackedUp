package com.supermartijn642.packedup;

import com.supermartijn642.core.CommonUtils;
import com.supermartijn642.core.TextComponents;
import com.supermartijn642.packedup.extensions.PackedUpPlayer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created 2/7/2020 by SuperMartijn642
 */
public class PackedUpCommon {

    public static void openBackpackInventory(ItemStack stack, Player player, int bagSlot){
        // Check the item nbt for a backpack id from before 1.20.5
        updateOldBackpack(stack);

        BackpackType type = ((BackpackItem)stack.getItem()).type;
        Component name = TextComponents.itemStack(stack).get();
        Integer inventoryIndex = stack.get(BackpackItem.INVENTORY_ID);
        if(inventoryIndex == null || BackpackStorageManager.getInventory(inventoryIndex) == null){
            inventoryIndex = BackpackStorageManager.createInventoryIndex(type);
            stack.set(BackpackItem.INVENTORY_ID, inventoryIndex);
        }else
            BackpackStorageManager.getInventory(inventoryIndex).adjustSize(type);
        BackpackInventory inventory = BackpackStorageManager.getInventory(inventoryIndex);
        CommonUtils.openContainer(new BackpackContainer(player, bagSlot, name, inventoryIndex, type, inventory.bagsInThisBag, inventory.bagsThisBagIsIn, inventory.layer));
    }

    public static void onPlayerDeath(Player player){
        if(PackedUpConfig.keepBackpacksOnDeath.get()){
            List<ItemStack> stacksToBeSaved = new ArrayList<>();
            for(int index = 0; index < player.getInventory().getContainerSize(); index++){
                ItemStack stack = player.getInventory().getItem(index);
                if(!stack.isEmpty() && stack.getItem() instanceof BackpackItem){
                    stacksToBeSaved.add(stack.copy());
                    player.getInventory().setItem(index, ItemStack.EMPTY);
                }
            }
            ((PackedUpPlayer)player).packedupSetBackpacks(stacksToBeSaved);
        }
    }

    public static void onPlayerClone(Player newPlayer, Player oldPlayer){
        List<ItemStack> backpacks = ((PackedUpPlayer)oldPlayer).packedupGetBackpacks();
        if(backpacks != null){
            backpacks.forEach(newPlayer.getInventory()::placeItemBackInInventory);
            ((PackedUpPlayer)oldPlayer).packedupSetBackpacks(null);
        }
    }

    /**
     * Upgrades the old pre-1.20.5 nbt data to the new data component
     */
    private static void updateOldBackpack(ItemStack stack){
        // Check the item nbt for a backpack id from before 1.20.6
        if(stack.has(DataComponents.CUSTOM_DATA)){
            CustomData data = stack.get(DataComponents.CUSTOM_DATA);
            //noinspection deprecation
            if(data != null && data.getUnsafe().contains("packedup:invIndex", Tag.TAG_INT)){
                stack.set(BackpackItem.INVENTORY_ID, data.copyTag().getInt("packedup:invIndex"));
                //noinspection deprecation
                if(data.getUnsafe().size() <= 1)
                    stack.remove(DataComponents.CUSTOM_DATA);
                else
                    stack.set(DataComponents.CUSTOM_DATA, data.update(t -> t.remove("packedup:invIndex")));
            }
        }
    }
}
