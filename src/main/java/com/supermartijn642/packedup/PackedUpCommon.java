package com.supermartijn642.packedup;

import com.google.common.collect.Streams;
import com.supermartijn642.core.CommonUtils;
import com.supermartijn642.core.TextComponents;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created 2/7/2020 by SuperMartijn642
 */
@Mod.EventBusSubscriber
public class PackedUpCommon {

    public static void openBackpackInventory(ItemStack stack, EntityPlayer player, int bagSlot){
        BackpackType type = ((BackpackItem)stack.getItem()).type;
        ITextComponent name = TextComponents.itemStack(stack).get();
        NBTTagCompound compound = stack.hasTagCompound() ? stack.getTagCompound() : new NBTTagCompound();
        if(!compound.hasKey("packedup:invIndex") || BackpackStorageManager.getInventory(compound.getInteger("packedup:invIndex")) == null){
            compound.setInteger("packedup:invIndex", BackpackStorageManager.createInventoryIndex(type));
            stack.setTagCompound(compound);
        }else
            BackpackStorageManager.getInventory(compound.getInteger("packedup:invIndex")).adjustSize(type);
        int inventoryIndex = compound.getInteger("packedup:invIndex");
        BackpackInventory inventory = BackpackStorageManager.getInventory(inventoryIndex);
        CommonUtils.openContainer(new BackpackContainer(player, bagSlot, name, inventoryIndex, type, inventory.bagsInThisBag, inventory.bagsThisBagIsIn, inventory.layer));
    }

    @SubscribeEvent
    public static void onPlayerDeath(LivingDropsEvent e){
        if(e.getEntity() instanceof EntityPlayer && PackedUpConfig.keepBackpacksOnDeath.get() && !e.isCanceled()){
            List<EntityItem> stacksToBeSaved = e.getDrops().stream()
                .filter(itemEntity -> itemEntity.isEntityAlive() && !itemEntity.getItem().isEmpty() && itemEntity.getItem().getItem() instanceof BackpackItem)
                .collect(Collectors.toList());

            if(!stacksToBeSaved.isEmpty()){
                stacksToBeSaved.forEach(e.getDrops()::remove);

                NBTTagList itemData = new NBTTagList();
                stacksToBeSaved.stream().map(EntityItem::getItem)
                    .forEach(stack -> itemData.appendTag(stack.serializeNBT()));

                e.getEntity().getEntityData().setTag("packedup:backpacks", itemData);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone e){
        if(e.getOriginal().getEntityData().hasKey("packedup:backpacks", Constants.NBT.TAG_LIST)){
            NBTTagList itemData = e.getOriginal().getEntityData().getTagList("packedup:backpacks", Constants.NBT.TAG_COMPOUND);
            Streams.stream(itemData.iterator())
                .filter(NBTTagCompound.class::isInstance)
                .map(NBTTagCompound.class::cast)
                .map(ItemStack::new)
                .forEach(stack -> e.getEntityPlayer().inventory.placeItemBackInInventory(e.getEntityPlayer().world, stack));
        }
    }
}
