package com.supermartijn642.packedup;

import com.mojang.serialization.DataResult;
import com.supermartijn642.core.CommonUtils;
import com.supermartijn642.core.TextComponents;
import com.supermartijn642.packedup.screen.BackpackContainer;
import com.supermartijn642.packedup.storage.BackpackInventory;
import com.supermartijn642.packedup.storage.BackpackStorageManager;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

/**
 * Created 2/7/2020 by SuperMartijn642
 */
@Mod.EventBusSubscriber
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
        if(e.getEntity() instanceof Player && PackedUpConfig.keepBackpacksOnDeath.get()){
            List<ItemEntity> stacksToBeSaved = e.getDrops().stream()
                .filter(itemEntity -> itemEntity.isAlive() && !itemEntity.getItem().isEmpty() && itemEntity.getItem().getItem() instanceof BackpackItem)
                .toList();

            if(!stacksToBeSaved.isEmpty()){
                stacksToBeSaved.forEach(e.getDrops()::remove);

                ListTag itemData = new ListTag();
                RegistryOps<Tag> ops = e.getEntity().registryAccess().createSerializationContext(NbtOps.INSTANCE);
                stacksToBeSaved.stream().map(ItemEntity::getItem)
                    .map(item -> ItemStack.CODEC.encodeStart(ops, item).getOrThrow())
                    .forEach(itemData::add);

                e.getEntity().getPersistentData().put("packedup:backpacks", itemData);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone e){
        ListTag itemData = e.getOriginal().getPersistentData().getListOrEmpty("packedup:backpacks");
        RegistryOps<Tag> ops = e.getEntity().registryAccess().createSerializationContext(NbtOps.INSTANCE);
        itemData.stream()
            .map(tag -> ItemStack.CODEC.decode(ops, tag))
            .filter(DataResult::isSuccess)
            .map(result -> result.getOrThrow().getFirst())
            .forEach(stack -> e.getEntity().getInventory().placeItemBackInInventory(stack));
    }

    /**
     * Upgrades the old pre-1.20.5 nbt data to the new data component
     */
    private static void updateOldBackpack(ItemStack stack){
        // Check the item nbt for a backpack id from before 1.20.6
        if(stack.has(DataComponents.CUSTOM_DATA)){
            CustomData data = stack.get(DataComponents.CUSTOM_DATA);
            if(data != null && data.tag.getInt("packedup:invIndex").isPresent()){
                //noinspection OptionalGetWithoutIsPresent
                stack.set(BackpackItem.INVENTORY_ID, data.copyTag().getInt("packedup:invIndex").get());
                if(data.tag.size() <= 1)
                    stack.remove(DataComponents.CUSTOM_DATA);
                else
                    stack.set(DataComponents.CUSTOM_DATA, data.update(t -> t.remove("packedup:invIndex")));
            }
        }
    }
}
