package com.supermartijn642.packedup.packets;

import com.supermartijn642.core.CommonUtils;
import com.supermartijn642.core.network.BasePacket;
import com.supermartijn642.core.network.PacketContext;
import com.supermartijn642.packedup.BackpackItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Created 11/01/2025 by SuperMartijn642
 */
public class PacketSetIcon implements BasePacket {

    private InteractionHand hand;
    private ItemStack icon;

    public PacketSetIcon(InteractionHand hand, ItemStack icon){
        this.hand = hand;
        this.icon = icon;
    }

    public PacketSetIcon(){
    }

    @Override
    public void write(FriendlyByteBuf buffer){
        buffer.writeBoolean(this.hand == InteractionHand.MAIN_HAND);
        ItemStack.OPTIONAL_STREAM_CODEC.encode(new RegistryFriendlyByteBuf(buffer, CommonUtils.getRegistryAccess()), this.icon);
    }

    @Override
    public void read(FriendlyByteBuf buffer){
        this.hand = buffer.readBoolean() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
        this.icon = ItemStack.OPTIONAL_STREAM_CODEC.decode(new RegistryFriendlyByteBuf(buffer, CommonUtils.getRegistryAccess()));
    }

    @Override
    public void handle(PacketContext context){
        Player player = context.getSendingPlayer();
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
