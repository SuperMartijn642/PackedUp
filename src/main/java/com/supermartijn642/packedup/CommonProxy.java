package com.supermartijn642.packedup;

import com.supermartijn642.packedup.packets.PacketBackpackContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.Mod;

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

}
