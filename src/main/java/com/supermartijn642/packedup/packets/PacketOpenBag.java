package com.supermartijn642.packedup.packets;

import com.supermartijn642.core.network.BasePacket;
import com.supermartijn642.core.network.PacketContext;
import com.supermartijn642.packedup.BackpackItem;
import com.supermartijn642.packedup.PackedUpCommon;
import com.supermartijn642.packedup.compat.Compatibility;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Created 8/13/2020 by SuperMartijn642
 */
public class PacketOpenBag implements BasePacket {

    public PacketOpenBag(){
    }

    @Override
    public void write(FriendlyByteBuf buffer){
    }

    @Override
    public void read(FriendlyByteBuf buffer){
    }

    @Override
    public void handle(PacketContext context){
        Player player = context.getSendingPlayer();
        if(player != null){
            Inventory inventory = player.getInventory();
            if(!Compatibility.CURIOS.openBackpack(player)){
                for(int i = 0; i < inventory.getContainerSize(); i++){
                    ItemStack stack = inventory.getItem(i);
                    if(stack.getItem() instanceof BackpackItem)
                        PackedUpCommon.openBackpackInventory(stack, player, i);
                }
            }
        }
    }
}
