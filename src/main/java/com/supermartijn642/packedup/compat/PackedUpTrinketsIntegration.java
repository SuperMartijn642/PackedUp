package com.supermartijn642.packedup.compat;

import com.supermartijn642.packedup.BackpackItem;
import com.supermartijn642.packedup.PackedUpCommon;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Created 08/03/2023 by SuperMartijn642
 */
public class PackedUpTrinketsIntegration {

    public static boolean openBackpack(Player player){
        return TrinketsApi.getTrinketComponent(player).map(handler -> {
            for(Tuple<SlotReference,ItemStack> slot : handler.getAllEquipped()){
                if(slot.getB().getItem() instanceof BackpackItem){
                    PackedUpCommon.openBackpackInventory(slot.getB(), player, -1);
                    return true;
                }
            }
            return false;
        }).orElse(false);
    }
}
