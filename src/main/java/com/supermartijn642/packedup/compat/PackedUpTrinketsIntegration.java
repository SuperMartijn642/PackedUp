package com.supermartijn642.packedup.compat;

import com.supermartijn642.packedup.BackpackItem;
import com.supermartijn642.packedup.PackedUpCommon;
import eu.pb4.trinkets.api.TrinketsApi;
import net.minecraft.world.entity.player.Player;

/**
 * Created 08/03/2023 by SuperMartijn642
 */
public class PackedUpTrinketsIntegration {

    public static boolean openBackpack(Player player){
        return TrinketsApi.getAttachment(player)
            .findFirst(stack -> stack.getItem() instanceof BackpackItem)
            .map(slot -> {
                PackedUpCommon.openBackpackInventory(slot.get(), player, -1);
                return true;
            }).orElse(false);
    }
}
