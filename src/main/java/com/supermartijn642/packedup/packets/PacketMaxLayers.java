package com.supermartijn642.packedup.packets;

import com.supermartijn642.packedup.BackpackStorageManager;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Created 6/28/2020 by SuperMartijn642
 */
public class PacketMaxLayers implements IMessage, IMessageHandler<PacketMaxLayers,IMessage> {

    private int maxLayers;

    public PacketMaxLayers(int maxLayers){
        this.maxLayers = maxLayers;
    }

    public PacketMaxLayers(){
    }

    @Override
    public void fromBytes(ByteBuf buf){
        this.maxLayers = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf){
        buf.writeInt(this.maxLayers);
    }

    @Override
    public IMessage onMessage(PacketMaxLayers message, MessageContext ctx){
        BackpackStorageManager.maxLayers = message.maxLayers;
        return null;
    }
}
