package com.supermartijn642.packedup.packets;

import com.supermartijn642.core.network.BasePacket;
import com.supermartijn642.core.network.PacketContext;
import com.supermartijn642.packedup.BackpackItem;
import com.supermartijn642.packedup.PackedUpCommon;
import com.supermartijn642.packedup.compat.Compatibility;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;

/**
 * Created 4/29/2020 by SuperMartijn642
 */
public class PacketOpenBag implements BasePacket {

    public PacketOpenBag(){
    }

    @Override
    public void write(PacketBuffer buffer){
    }

    @Override
    public void read(PacketBuffer buffer){
    }

    @Override
    public void handle(PacketContext context){
        EntityPlayer player = context.getSendingPlayer();
        if(player != null){
            InventoryPlayer inventory = player.inventory;
            if(!Compatibility.BAUBLES.openBackpack(player)){
                for(int i = 0; i < inventory.getSizeInventory(); i++){
                    ItemStack stack = inventory.getStackInSlot(i);
                    if(stack.getItem() instanceof BackpackItem)
                        PackedUpCommon.openBackpackInventory(stack, player, i);
                }
            }
        }
    }
}
