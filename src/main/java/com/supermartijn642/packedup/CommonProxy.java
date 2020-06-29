package com.supermartijn642.packedup;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Created 2/7/2020 by SuperMartijn642
 */
@Mod.EventBusSubscriber
public class CommonProxy {

    @SubscribeEvent
    public static void onItemRegistry(final RegistryEvent.Register<Item> e){
        for(BackpackType type : BackpackType.values())
            e.getRegistry().register(new BackpackItem(type));
    }

    public static void openBackpackInventory(ItemStack stack, EntityPlayer player, int bagSlot){
        BackpackType type = ((BackpackItem)stack.getItem()).type;
        NBTTagCompound compound = stack.getTagCompound();
        if(compound == null)
            compound = new NBTTagCompound();
        if(!compound.hasKey("packedup:invIndex") || BackpackStorageManager.getInventory(compound.getInteger("packedup:invIndex")) == null){
            compound.setInteger("packedup:invIndex", BackpackStorageManager.createInventoryIndex(type));
            stack.setTagCompound(compound);
        }else{
            BackpackInventory inventory = BackpackStorageManager.getInventory(compound.getInteger("packedup:invIndex"));
            int rows = inventory.getSlots() / 9;
            if(rows != type.getRows())
                inventory.adjustSize(type.getRows() * 9);
        }
        int inventoryIndex = compound.getInteger("packedup:invIndex");
        player.openGui(PackedUp.instance, type.getRows() | ((bagSlot + 2) << 5) | (inventoryIndex << 11), player.world, (int)player.posX, (int)player.posY, (int)player.posZ);
        if(!player.world.isRemote)
            PackedUp.channel.sendTo(new PacketBackpackContainer(inventoryIndex), (EntityPlayerMP)player);
    }

}
