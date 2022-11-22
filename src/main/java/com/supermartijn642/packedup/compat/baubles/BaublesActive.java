package com.supermartijn642.packedup.compat.baubles;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.cap.BaublesCapabilities;
import com.supermartijn642.packedup.BackpackItem;
import com.supermartijn642.packedup.PackedUpCommon;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.IItemHandler;

/**
 * Created 7/14/2020 by SuperMartijn642
 */
public class BaublesActive extends BaublesInactive {

    @Override
    public boolean isBaubleCapability(Capability<?> capability){
        return capability == BaublesCapabilities.CAPABILITY_ITEM_BAUBLE;
    }

    @Override
    public <T> T getBaubleCapability(Capability<T> capability, Item item){
        return BaublesCapabilities.CAPABILITY_ITEM_BAUBLE.cast(itemStack -> BaubleType.BODY);
    }

    @Override
    public boolean openBackpack(EntityPlayer player){
        IItemHandler handler = BaublesApi.getBaublesHandler(player);
        for(int i = 0; i < handler.getSlots(); i++){
            ItemStack stack = handler.getStackInSlot(i);
            if(stack.getItem() instanceof BackpackItem){
                PackedUpCommon.openBackpackInventory(stack, player, -1);
                return true;
            }
        }
        return false;
    }
}
