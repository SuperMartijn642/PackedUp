package com.supermartijn642.packedup.packets;

import com.supermartijn642.packedup.BackpackItem;
import com.supermartijn642.packedup.CommonProxy;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.fml.network.NetworkEvent;
import top.theillusivec4.curios.api.CuriosApi;

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
            CuriosApi.getCuriosHelper().findEquippedCurio(item -> item.getItem() instanceof BackpackItem, player).ifPresent(triple ->
                CommonProxy.openBackpackInventory(triple.getRight(), player, -1)
            );
        }
    }

}
