package com.supermartijn642.packedup.compat;

import com.supermartijn642.packedup.compat.curios.CuriosOff;
import com.supermartijn642.packedup.compat.curios.CuriosOn;
import net.minecraftforge.fml.ModList;

/**
 * Created 12/21/2020 by SuperMartijn642
 */
public class Compatibility {

    public static CuriosOff CURIOS;

    public static void init(){
        CURIOS = ModList.get().isLoaded("curios") ? new CuriosOn() : new CuriosOff();
    }
}
