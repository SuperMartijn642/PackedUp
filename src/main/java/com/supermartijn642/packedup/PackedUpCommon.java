package com.supermartijn642.packedup;

import com.supermartijn642.core.CommonUtils;
import com.supermartijn642.core.TextComponents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created 2/7/2020 by SuperMartijn642
 */
@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME)
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

    @SubscribeEvent
    public static void onPlayerDeath(LivingDropsEvent e){
        if(e.getEntity() instanceof Player && PackedUpConfig.keepBackpacksOnDeath.get() && !e.isCanceled()){
            List<ItemEntity> stacksToBeSaved = e.getDrops().stream()
                .filter(itemEntity -> itemEntity.isAlive() && !itemEntity.getItem().isEmpty() && itemEntity.getItem().getItem() instanceof BackpackItem)
                .collect(Collectors.toList());

            if(!stacksToBeSaved.isEmpty()){
                stacksToBeSaved.forEach(e.getDrops()::remove);

                ListTag itemData = new ListTag();
                stacksToBeSaved.stream().map(ItemEntity::getItem)
                    .forEach(stack -> itemData.add(stack.save(e.getEntity().registryAccess())));

                e.getEntity().getPersistentData().put("packedup:backpacks", itemData);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone e){
        if(e.getOriginal().getPersistentData().contains("packedup:backpacks", Tag.TAG_LIST)){
            ListTag itemData = e.getOriginal().getPersistentData().getList("packedup:backpacks", Tag.TAG_COMPOUND);
            itemData.stream()
                .filter(CompoundTag.class::isInstance)
                .map(CompoundTag.class::cast)
                .map(tag -> ItemStack.parseOptional(e.getEntity().registryAccess(), tag))
                .forEach(stack -> e.getEntity().getInventory().placeItemBackInInventory(stack));
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
