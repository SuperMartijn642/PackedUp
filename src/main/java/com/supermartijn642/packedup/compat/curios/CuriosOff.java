package com.supermartijn642.packedup.compat.curios;

import net.minecraft.world.entity.player.Player;

/**
 * Created 12/21/2020 by SuperMartijn642
 */
public class CuriosOff {

    public boolean isLoaded(){
        return false;
    }

    public boolean openBackpack(Player player){
        return false;
    }

}
