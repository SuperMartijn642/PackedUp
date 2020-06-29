package com.supermartijn642.packedup;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.nio.charset.StandardCharsets;

/**
 * Created 4/29/2020 by SuperMartijn642
 */
public class PacketRename implements IMessage, IMessageHandler<PacketRename,IMessage> {

    private String name;

    public PacketRename(String name){
        this.name = name;
    }

    public PacketRename(){
    }

    @Override
    public void fromBytes(ByteBuf buffer){
        if(buffer.readBoolean()){
            byte[] bytes = new byte[buffer.readInt()];
            buffer.readBytes(bytes);
            this.name = new String(bytes, StandardCharsets.UTF_16);
        }
    }

    @Override
    public void toBytes(ByteBuf buffer){
        buffer.writeBoolean(this.name != null);
        if(this.name != null){
            byte[] bytes = this.name.getBytes(StandardCharsets.UTF_16);
            buffer.writeInt(bytes.length);
            buffer.writeBytes(bytes);
        }
    }

    @Override
    public IMessage onMessage(PacketRename message, MessageContext ctx){
        EntityPlayer player = ctx.getServerHandler().player;
        if(player != null){
            ItemStack stack = player.getHeldItem(EnumHand.MAIN_HAND);

            if(stack.isEmpty() || !(stack.getItem() instanceof BackpackItem))
                stack = player.getHeldItem(EnumHand.OFF_HAND);
            if(stack.isEmpty() || !(stack.getItem() instanceof BackpackItem))
                return null;

            if(message.name == null)
                stack.clearCustomName();
            else
                stack.setStackDisplayName(message.name);
        }
        return null;
    }

}
