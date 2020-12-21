package com.supermartijn642.packedup.packets;

import com.supermartijn642.packedup.BackpackItem;
import com.supermartijn642.packedup.CommonProxy;
import com.supermartijn642.packedup.PackedUp;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Created 4/29/2020 by SuperMartijn642
 */
public class PacketOpenBag implements IMessage, IMessageHandler<PacketOpenBag,IMessage> {

    public PacketOpenBag(){
    }

    @Override
    public void fromBytes(ByteBuf buffer){
    }

    @Override
    public void toBytes(ByteBuf buffer){
    }

    @Override
    public IMessage onMessage(PacketOpenBag message, MessageContext ctx){
        EntityPlayer player = ctx.getServerHandler().player;
        if(player != null){
            InventoryPlayer inventory = player.inventory;
            if(!PackedUp.baubles.openBackpack(player)){
                for(int i = 0; i < inventory.getSizeInventory(); i++){
                    ItemStack stack = inventory.getStackInSlot(i);
                    if(stack.getItem() instanceof BackpackItem)
                        CommonProxy.openBackpackInventory(stack, player, i);
                }
            }
        }
        return null;
    }

}
