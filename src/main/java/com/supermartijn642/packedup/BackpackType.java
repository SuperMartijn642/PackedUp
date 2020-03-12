package com.supermartijn642.packedup;

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
        switch(this){
            case BASIC:
                return PUConfig.INSTANCE.basicRows.get();
            case IRON:
                return PUConfig.INSTANCE.ironRows.get();
            case COPPER:
                return PUConfig.INSTANCE.copperRows.get();
            case SILVER:
                return PUConfig.INSTANCE.silverRows.get();
            case GOLD:
                return PUConfig.INSTANCE.goldRows.get();
            case DIAMOND:
                return PUConfig.INSTANCE.diamondRows.get();
            case OBSIDIAN:
                return PUConfig.INSTANCE.obsidianRows.get();
        }
        return 0;
    }

    public boolean isEnabled(){
//        switch(this){
//            case BASIC:
//                return PUConfig.INSTANCE.basicEnable.get();
//            case IRON:
//                return PUConfig.INSTANCE.ironEnable.get();
//            case COPPER:
//                return PUConfig.INSTANCE.copperEnable.get();
//            case SILVER:
//                return PUConfig.INSTANCE.silverEnable.get();
//            case GOLD:
//                return PUConfig.INSTANCE.goldEnable.get();
//            case DIAMOND:
//                return PUConfig.INSTANCE.diamondEnable.get();
//            case OBSIDIAN:
//                return PUConfig.INSTANCE.obsidianEnable.get();
//        }
//        return false;
        return true;
    }

    public String getRegistryName(){
        return this.toString().toLowerCase() + "backpack";
    }
}
