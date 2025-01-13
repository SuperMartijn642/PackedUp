package com.supermartijn642.packedup.packets;

import com.supermartijn642.core.network.BasePacket;
import com.supermartijn642.core.network.PacketContext;
import com.supermartijn642.packedup.BackpackItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;

/**
 * Created 11/01/2025 by SuperMartijn642
 */
public class PacketSetIcon implements BasePacket {

    private Hand hand;
    private ItemStack icon;

    public PacketSetIcon(Hand hand, ItemStack icon){
        this.hand = hand;
        this.icon = icon;
    }

    public PacketSetIcon(){
    }

    @Override
    public void write(PacketBuffer buffer){
        buffer.writeBoolean(this.hand == Hand.MAIN_HAND);
        buffer.writeItem(this.icon);
    }

    @Override
    public void read(PacketBuffer buffer){
        this.hand = buffer.readBoolean() ? Hand.MAIN_HAND : Hand.OFF_HAND;
        this.icon = buffer.readItem();
    }

    @Override
    public void handle(PacketContext context){
        PlayerEntity player = context.getSendingPlayer();
        if(player != null){
            ItemStack stack = player.getItemInHand(this.hand);

            if(stack.isEmpty() || !(stack.getItem() instanceof BackpackItem))
                return;

            stack = stack.copy();
            BackpackItem.setIcon(stack, this.icon);
            player.setItemInHand(this.hand, stack);
        }
    }
}
