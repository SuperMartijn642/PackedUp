package com.supermartijn642.packedup.packets;

import com.supermartijn642.core.network.BasePacket;
import com.supermartijn642.core.network.PacketContext;
import com.supermartijn642.packedup.BackpackItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumHand;

import java.io.IOException;

/**
 * Created 11/01/2025 by SuperMartijn642
 */
public class PacketSetIcon implements BasePacket {

    private EnumHand hand;
    private ItemStack icon;

    public PacketSetIcon(EnumHand hand, ItemStack icon){
        this.hand = hand;
        this.icon = icon;
    }

    public PacketSetIcon(){
    }

    @Override
    public void write(PacketBuffer buffer){
        buffer.writeBoolean(this.hand == EnumHand.MAIN_HAND);
        buffer.writeItemStack(this.icon);
    }

    @Override
    public void read(PacketBuffer buffer){
        this.hand = buffer.readBoolean() ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
        try{
            this.icon = buffer.readItemStack();
        }catch(IOException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void handle(PacketContext context){
        EntityPlayer player = context.getSendingPlayer();
        if(player != null){
            ItemStack stack = player.getHeldItem(this.hand);

            if(stack.isEmpty() || !(stack.getItem() instanceof BackpackItem))
                return;

            stack = stack.copy();
            BackpackItem.setIcon(stack, this.icon);
            player.setHeldItem(this.hand, stack);
        }
    }
}
