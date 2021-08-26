package com.supermartijn642.packedup;

import com.google.common.collect.Streams;
import com.supermartijn642.packedup.packets.PacketBackpackContainer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
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
public class CommonProxy {

    public static void openBackpackInventory(ItemStack stack, EntityPlayer player, int bagSlot){
        BackpackType type = ((BackpackItem)stack.getItem()).type;
        NBTTagCompound compound = stack.getTagCompound();
        if(compound == null)
            compound = new NBTTagCompound();
        if(!compound.hasKey("packedup:invIndex") || BackpackStorageManager.getInventory(compound.getInteger("packedup:invIndex")) == null){
            compound.setInteger("packedup:invIndex", BackpackStorageManager.createInventoryIndex(type));
            stack.setTagCompound(compound);
        }else
            BackpackStorageManager.getInventory(compound.getInteger("packedup:invIndex")).adjustSize(type);
        int inventoryIndex = compound.getInteger("packedup:invIndex");
        player.openGui(PackedUp.instance, type.ordinal() | ((bagSlot + 2) << 5) | (inventoryIndex << 11), player.world, (int)player.posX, (int)player.posY, (int)player.posZ);
        if(!player.world.isRemote)
            PackedUp.CHANNEL.sendToPlayer(player, new PacketBackpackContainer(inventoryIndex));
    }

    @SubscribeEvent
    public static void onPlayerDeath(LivingDropsEvent e){
        if(e.getEntity() instanceof EntityPlayer && PUConfig.keepBackpacksOnDeath.get() && !e.isCanceled()){
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

    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinWorldEvent e){
        if(e.getEntity() instanceof EntityItem && !PUConfig.canBackpacksBurn.get() && ((EntityItem)e.getEntity()).getItem().getItem() instanceof BackpackItem)
            e.getEntity().setEntityInvulnerable(true);
    }
}
