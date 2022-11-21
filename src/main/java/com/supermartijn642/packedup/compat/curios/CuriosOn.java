package com.supermartijn642.packedup.compat.curios;

import com.supermartijn642.packedup.BackpackItem;
import com.supermartijn642.packedup.PackedUpCommon;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import top.theillusivec4.curios.api.CuriosApi;

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
    public boolean openBackpack(PlayerEntity player){
        Optional<ImmutableTriple<String,Integer,ItemStack>> optional = CuriosApi.getCuriosHelper().findEquippedCurio(item -> item.getItem() instanceof BackpackItem, player);
        optional.ifPresent(triple ->
            PackedUpCommon.openBackpackInventory(triple.getRight(), player, -1)
        );
        return optional.isPresent();
    }
}
