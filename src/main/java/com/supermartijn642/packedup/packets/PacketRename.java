package com.supermartijn642.packedup.packets;

import com.supermartijn642.core.TextComponents;
import com.supermartijn642.core.network.BasePacket;
import com.supermartijn642.core.network.PacketContext;
import com.supermartijn642.packedup.BackpackItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Created 4/29/2020 by SuperMartijn642
 */
public class PacketRename implements BasePacket {

    private String name;

    public PacketRename(){
    }

    public PacketRename(String name){
        this.name = name == null ? null : name.trim();
    }

    @Override
    public void write(FriendlyByteBuf buffer){
        buffer.writeBoolean(this.name != null);
        if(this.name != null)
            buffer.writeUtf(this.name);
    }

    @Override
    public void read(FriendlyByteBuf buffer){
        this.name = buffer.readBoolean() ? buffer.readUtf(32767) : "";
    }

    @Override
    public void handle(PacketContext context){
        Player player = context.getSendingPlayer();
        if(player != null){
            ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);

            if(stack.isEmpty() || !(stack.getItem() instanceof BackpackItem))
                stack = player.getItemInHand(InteractionHand.OFF_HAND);
            if(stack.isEmpty() || !(stack.getItem() instanceof BackpackItem))
                return;

            if(this.name == null || this.name.isEmpty() || this.name.equals(TextComponents.item(stack.getItem()).format()))
                stack.resetHoverName();
            else
                stack.setHoverName(TextComponents.string(this.name).get());
        }
    }
}
