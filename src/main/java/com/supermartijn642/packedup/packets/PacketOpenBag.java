package com.supermartijn642.packedup.packets;

import baubles.api.BaublesApi;
import com.supermartijn642.packedup.BackpackItem;
import com.supermartijn642.packedup.CommonProxy;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.items.IItemHandler;

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
        if(player == null)
            return null;
        IItemHandler handler = BaublesApi.getBaublesHandler(player);
        for(int i = 0; i < handler.getSlots(); i++){
            ItemStack stack = handler.getStackInSlot(i);
            if(stack.getItem() instanceof BackpackItem){
                CommonProxy.openBackpackInventory(stack, player, -1);
                break;
            }
        }
        return null;
    }

}
