package com.supermartijn642.packedup;

import java.util.Locale;

/**
 * Created 2/7/2020 by SuperMartijn642
 */
public enum BackpackType {

    BASIC(3, 9), IRON(4, 9), COPPER(4, 9), SILVER(5, 9), GOLD(5, 9), DIAMOND(7, 9), OBSIDIAN(8, 9);

    private final int defaultRows, defaultColumns;

    BackpackType(int defaultRows, int defaultColumns){
        this.defaultRows = defaultRows;
        this.defaultColumns = defaultColumns;
    }

    public String getRegistryName(){
        return this.name().toLowerCase(Locale.ROOT) + "backpack";
    }

    public int getDefaultRows(){
        return this.defaultRows;
    }

    public int getDefaultColumns(){
        return this.defaultColumns;
    }

    public boolean isEnabled(){
        switch(this){
            case BASIC:
                return PUConfig.basicEnable.get();
            case IRON:
                return PUConfig.ironEnable.get();
            case COPPER:
                return PUConfig.copperEnable.get();
            case SILVER:
                return PUConfig.silverEnable.get();
            case GOLD:
                return PUConfig.goldEnable.get();
            case DIAMOND:
                return PUConfig.diamondEnable.get();
            case OBSIDIAN:
                return PUConfig.obsidianEnable.get();
        }
        return false;
    }

    public int getRows(){
        switch(this){
            case BASIC:
                return PUConfig.basicRows.get();
            case IRON:
                return PUConfig.ironRows.get();
            case COPPER:
                return PUConfig.copperRows.get();
            case SILVER:
                return PUConfig.silverRows.get();
            case GOLD:
                return PUConfig.goldRows.get();
            case DIAMOND:
                return PUConfig.diamondRows.get();
            case OBSIDIAN:
                return PUConfig.obsidianRows.get();
        }
        return 0;
    }

    public int getColumns(){
        switch(this){
            case BASIC:
                return PUConfig.basicColumns.get();
            case IRON:
                return PUConfig.ironColumns.get();
            case COPPER:
                return PUConfig.copperColumns.get();
            case SILVER:
                return PUConfig.silverColumns.get();
            case GOLD:
                return PUConfig.goldColumns.get();
            case DIAMOND:
                return PUConfig.diamondColumns.get();
            case OBSIDIAN:
                return PUConfig.obsidianColumns.get();
        }
        return 0;
    }

    public int getSlots(){
        return this.getRows() * this.getColumns();
    }
}
