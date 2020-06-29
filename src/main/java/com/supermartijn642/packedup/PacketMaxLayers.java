package com.supermartijn642.packedup;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Created 6/28/2020 by SuperMartijn642
 */
public class PacketMaxLayers {

    private int maxLayers;

    public PacketMaxLayers(int maxLayers){
        this.maxLayers = maxLayers;
    }

    public void encode(PacketBuffer buffer){
        buffer.writeInt(this.maxLayers);
    }

    public static PacketMaxLayers decode(PacketBuffer buffer){
        return new PacketMaxLayers(buffer.readInt());
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier){
        contextSupplier.get().setPacketHandled(true);

        BackpackStorageManager.maxLayers = this.maxLayers;
    }
}
