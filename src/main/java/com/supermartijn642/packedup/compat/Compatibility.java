package com.supermartijn642.packedup.compat;

import com.supermartijn642.core.CommonUtils;
import com.supermartijn642.packedup.compat.baubles.BaublesActive;
import com.supermartijn642.packedup.compat.baubles.BaublesInactive;

/**
 * Created 12/21/2020 by SuperMartijn642
 */
public class Compatibility {

    public static BaublesInactive BAUBLES;

    public static void init(){
        BAUBLES = CommonUtils.isModLoaded("baubles") ? new BaublesActive() : new BaublesInactive();
    }
}
