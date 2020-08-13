package com.supermartijn642.packedup.packets;

import com.supermartijn642.packedup.BackpackContainer;
import com.supermartijn642.packedup.BackpackInventory;
import com.supermartijn642.packedup.BackpackStorageManager;
import com.supermartijn642.packedup.ClientProxy;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.HashSet;
import java.util.Set;

/**
 * Created 6/29/2020 by SuperMartijn642
 */
public class PacketBackpackContainer implements IMessage, IMessageHandler<PacketBackpackContainer,IMessage> {

    private int inventoryIndex;
    private Set<Integer> bagsInThisBag;
    private Set<Integer> bagsThisBagIsIn;
    private int layer;

    public PacketBackpackContainer(int inventoryIndex){
        this.inventoryIndex = inventoryIndex;
    }

    public PacketBackpackContainer(){
    }

    @Override
    public void fromBytes(ByteBuf buf){
        int size = buf.readInt();
        this.bagsInThisBag = new HashSet<>(size);
        for(int i = 0; i < size; i++)
            this.bagsInThisBag.add(buf.readInt());
        size = buf.readInt();
        this.bagsThisBagIsIn = new HashSet<>(size);
        for(int i = 0; i < size; i++)
            this.bagsThisBagIsIn.add(buf.readInt());
        this.layer = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf){
        BackpackInventory inventory = BackpackStorageManager.getInventory(this.inventoryIndex);
        buf.writeInt(inventory.bagsInThisBag.size());
        inventory.bagsInThisBag.forEach(buf::writeInt);
        buf.writeInt(inventory.bagsThisBagIsIn.size());
        inventory.bagsThisBagIsIn.forEach(buf::writeInt);
        buf.writeInt(inventory.layer);
    }

    @Override
    public IMessage onMessage(PacketBackpackContainer message, MessageContext ctx){
        EntityPlayer player = ClientProxy.getClientPlayer();
        ClientProxy.addScheduledTask(() -> {
            if(player.openContainer instanceof BackpackContainer){
                BackpackContainer container = (BackpackContainer)player.openContainer;
                container.inventory.bagsInThisBag.addAll(message.bagsInThisBag);
                container.inventory.bagsThisBagIsIn.addAll(message.bagsThisBagIsIn);
                container.inventory.layer = message.layer;
            }
        });
        return null;
    }
}
