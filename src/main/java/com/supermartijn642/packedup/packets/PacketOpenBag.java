package com.supermartijn642.packedup.packets;

import com.supermartijn642.packedup.BackpackItem;
import com.supermartijn642.packedup.CommonProxy;
import com.supermartijn642.packedup.compat.Compatibility;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Created 8/13/2020 by SuperMartijn642
 */
public class PacketOpenBag {

    public PacketOpenBag(){
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier){
        contextSupplier.get().setPacketHandled(true);

        PlayerEntity player = contextSupplier.get().getSender();
        if(player != null){
            PlayerInventory inventory = player.inventory;
            if(!Compatibility.CURIOS.openBackpack(player)){
                for(int i = 0; i < inventory.getSizeInventory(); i++){
                    ItemStack stack = inventory.getStackInSlot(i);
                    if(stack.getItem() instanceof BackpackItem)
                        CommonProxy.openBackpackInventory(stack, player, i);
                }
            }
        }
    }

}
