package com.supermartijn642.packedup.integration;

import baubles.api.BaubleType;
import baubles.api.cap.BaublesCapabilities;
import net.minecraft.item.Item;
import net.minecraftforge.common.capabilities.Capability;

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
}
