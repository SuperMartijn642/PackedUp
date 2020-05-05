package com.supermartijn642.packedup;

import java.util.Locale;

/**
 * Created 2/7/2020 by SuperMartijn642
 */
public enum BackpackType {

    BASIC(3), IRON(4), COPPER(4), SILVER(5), GOLD(5), DIAMOND(7), OBSIDIAN(8);

    private int defaultRows;

    BackpackType(int defaultRows){
        this.defaultRows = defaultRows;
    }

    public int getDefaultRows(){
        return this.defaultRows;
    }

    public int getRows(){
        return PUConfig.rows.getOrDefault(this, 0);
    }

    public boolean isEnabled(){
        return true;
    }

    public String getRegistryName(){
        return this.name().toLowerCase(Locale.ENGLISH) + "backpack";
    }
}
