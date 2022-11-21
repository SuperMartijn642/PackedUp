package com.supermartijn642.packedup;

import com.supermartijn642.core.CommonUtils;
import com.supermartijn642.core.TextComponents;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created 2/7/2020 by SuperMartijn642
 */
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PackedUpCommon {

    public static void openBackpackInventory(ItemStack stack, PlayerEntity player, int bagSlot){
        BackpackType type = ((BackpackItem)stack.getItem()).type;
        ITextComponent name = TextComponents.itemStack(stack).get();
        CompoundNBT compound = stack.getOrCreateTag();
        if(!compound.contains("packedup:invIndex") || BackpackStorageManager.getInventory(compound.getInt("packedup:invIndex")) == null){
            compound.putInt("packedup:invIndex", BackpackStorageManager.createInventoryIndex(type));
            stack.setTag(compound);
        }else
            BackpackStorageManager.getInventory(compound.getInt("packedup:invIndex")).adjustSize(type);
        int inventoryIndex = compound.getInt("packedup:invIndex");
        BackpackInventory inventory = BackpackStorageManager.getInventory(inventoryIndex);
        CommonUtils.openContainer(new BackpackContainer(player, bagSlot, name, inventoryIndex, type, inventory.bagsInThisBag, inventory.bagsThisBagIsIn, inventory.layer));
    }

    @SubscribeEvent
    public static void onPlayerDeath(LivingDropsEvent e){
        if(e.getEntity() instanceof PlayerEntity && PackedUpConfig.keepBackpacksOnDeath.get() && !e.isCanceled()){
            List<ItemEntity> stacksToBeSaved = e.getDrops().stream()
                .filter(itemEntity -> itemEntity.isAlive() && !itemEntity.getItem().isEmpty() && itemEntity.getItem().getItem() instanceof BackpackItem)
                .collect(Collectors.toList());

            if(!stacksToBeSaved.isEmpty()){
                stacksToBeSaved.forEach(e.getDrops()::remove);

                ListNBT itemData = new ListNBT();
                stacksToBeSaved.stream().map(ItemEntity::getItem)
                    .forEach(stack -> itemData.add(stack.serializeNBT()));

                e.getEntity().getPersistentData().put("packedup:backpacks", itemData);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone e){
        if(e.getOriginal().getPersistentData().contains("packedup:backpacks", Constants.NBT.TAG_LIST)){
            ListNBT itemData = e.getOriginal().getPersistentData().getList("packedup:backpacks", Constants.NBT.TAG_COMPOUND);
            itemData.stream()
                .filter(CompoundNBT.class::isInstance)
                .map(CompoundNBT.class::cast)
                .map(ItemStack::of)
                .forEach(stack -> e.getPlayer().inventory.placeItemBackInInventory(e.getPlayer().level, stack));
        }
    }
}
