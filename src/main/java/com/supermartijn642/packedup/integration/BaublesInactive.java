package com.supermartijn642.packedup.integration;

import net.minecraft.item.Item;
import net.minecraftforge.common.capabilities.Capability;

/**
 * Created 7/14/2020 by SuperMartijn642
 */
public class BaublesInactive {

    public boolean isBaubleCapability(Capability<?> capability){
        return false;
    }

    public <T> T getBaubleCapability(Capability<T> capability, Item item){
        return null;
    }
}