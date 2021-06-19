package com.supermartijn642.packedup.packets;

import com.supermartijn642.core.TextComponents;
import com.supermartijn642.core.network.BasePacket;
import com.supermartijn642.core.network.PacketContext;
import com.supermartijn642.packedup.BackpackItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraft.util.text.StringTextComponent;

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
    public void write(PacketBuffer buffer){
        buffer.writeBoolean(this.name != null);
        if(this.name != null)
            buffer.writeUtf(this.name);
    }

    @Override
    public void read(PacketBuffer buffer){
        this.name = buffer.readBoolean() ? buffer.readUtf(32767) : "";
    }

    @Override
    public void handle(PacketContext context){
        PlayerEntity player = context.getSendingPlayer();
        if(player != null){
            ItemStack stack = player.getItemInHand(Hand.MAIN_HAND);

            if(stack.isEmpty() || !(stack.getItem() instanceof BackpackItem))
                stack = player.getItemInHand(Hand.OFF_HAND);
            if(stack.isEmpty() || !(stack.getItem() instanceof BackpackItem))
                return;

            if(this.name == null || this.name.isEmpty() || this.name.equals(TextComponents.format(stack.getItem().getName(stack))))
                stack.resetHoverName();
            else
                stack.setHoverName(new StringTextComponent(this.name));
        }
    }
}
