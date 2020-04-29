package com.supermartijn642.packedup;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Created 4/29/2020 by SuperMartijn642
 */
public class PacketRename {

    private String name;

    public PacketRename(String name){
        this.name = name;
    }

    public void encode(PacketBuffer buffer){
        buffer.writeBoolean(this.name != null);
        if(this.name != null)
            buffer.writeString(this.name);
    }

    public static PacketRename decode(PacketBuffer buffer){
        return new PacketRename(buffer.readBoolean() ? buffer.readString() : null);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier){
        contextSupplier.get().setPacketHandled(true);

        PlayerEntity player = contextSupplier.get().getSender();
        if(player != null){
            ItemStack stack = player.getHeldItem(Hand.MAIN_HAND);

            if(stack.isEmpty() || !(stack.getItem() instanceof BackpackItem))
                stack = player.getHeldItem(Hand.OFF_HAND);
            if(stack.isEmpty() || !(stack.getItem() instanceof BackpackItem))
                return;

            stack.setDisplayName(new StringTextComponent(this.name));
        }
    }

}
