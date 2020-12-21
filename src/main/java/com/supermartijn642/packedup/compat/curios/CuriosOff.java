package com.supermartijn642.packedup.compat.curios;

import net.minecraft.entity.player.PlayerEntity;

/**
 * Created 12/21/2020 by SuperMartijn642
 */
public class CuriosOff {

    public boolean isLoaded(){
        return false;
    }

    public boolean openBackpack(PlayerEntity player){
        return false;
    }

}
