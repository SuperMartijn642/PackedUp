package com.supermartijn642.packedup.compat;

import com.supermartijn642.core.CommonUtils;

/**
 * Created 12/21/2020 by SuperMartijn642
 */
public class Compatibility {

    public static void init(){
        if(CommonUtils.isModLoaded("trashslot"))
            TrashSlotCompatibility.register();
    }
}
