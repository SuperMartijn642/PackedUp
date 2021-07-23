package com.supermartijn642.packedup.compat.curios;

import net.minecraft.world.entity.player.Player;

/**
 * Created 12/21/2020 by SuperMartijn642
 */
public class CuriosOn extends CuriosOff {

    @Override
    public boolean isLoaded(){
        return true;
    }

    @Override
    public boolean openBackpack(Player player){ // TODO
//        Optional<ImmutableTriple<String,Integer,ItemStack>> optional = CuriosApi.getCuriosHelper().findEquippedCurio(item -> item.getItem() instanceof BackpackItem, player);
//        optional.ifPresent(triple ->
//            CommonProxy.openBackpackInventory(triple.getRight(), player, -1)
//        );
        return false;//optional.isPresent();
    }
}
