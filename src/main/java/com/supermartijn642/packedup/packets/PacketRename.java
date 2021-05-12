package com.supermartijn642.packedup.packets;

import com.supermartijn642.packedup.BackpackItem;
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
            buffer.writeUtf(this.name);
    }

    public static PacketRename decode(PacketBuffer buffer){
        return new PacketRename(buffer.readBoolean() ? buffer.readUtf(32767) : null);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier){
        contextSupplier.get().setPacketHandled(true);

        PlayerEntity player = contextSupplier.get().getSender();
        if(player != null){
            ItemStack stack = player.getItemInHand(Hand.MAIN_HAND);

            if(stack.isEmpty() || !(stack.getItem() instanceof BackpackItem))
                stack = player.getItemInHand(Hand.OFF_HAND);
            if(stack.isEmpty() || !(stack.getItem() instanceof BackpackItem))
                return;

            stack.setHoverName(new StringTextComponent(this.name));
        }
    }

}
