package com.supermartijn642.packedup.extensions;

import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * Created 08/03/2023 by SuperMartijn642
 */
public interface PackedUpPlayer {

    void packedupSetBackpacks(List<ItemStack> backpacks);

    List<ItemStack> packedupGetBackpacks();
}
