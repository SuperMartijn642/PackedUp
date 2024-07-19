package com.supermartijn642.packedup.compat;

import com.supermartijn642.core.CommonUtils;
import com.supermartijn642.packedup.compat.curios.CuriosOff;
import com.supermartijn642.packedup.compat.curios.CuriosOn;

/**
 * Created 12/21/2020 by SuperMartijn642
 */
public class Compatibility {

    public static CuriosOff CURIOS;

    public static void init(){
        CURIOS = CommonUtils.isModLoaded("curios") ? new CuriosOn() : new CuriosOff();
    }
}
