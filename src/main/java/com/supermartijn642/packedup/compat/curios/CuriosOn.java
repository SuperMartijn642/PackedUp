package com.supermartijn642.packedup.compat.curios;

import com.supermartijn642.packedup.BackpackItem;
import com.supermartijn642.packedup.PackedUpCommon;
import net.minecraft.world.entity.player.Player;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;

import java.util.Optional;

/**
 * Created 12/21/2020 by SuperMartijn642
 */
public class CuriosOn extends CuriosOff {

    @Override
    public boolean isLoaded(){
        return true;
    }

    @Override
    public boolean openBackpack(Player player){
        Optional<SlotResult> optional = CuriosApi.getCuriosInventory(player).flatMap(i -> i.findFirstCurio(item -> item.getItem() instanceof BackpackItem));
        optional.ifPresent(slot ->
            PackedUpCommon.openBackpackInventory(slot.stack(), player, -1)
        );
        return optional.isPresent();
    }
}
