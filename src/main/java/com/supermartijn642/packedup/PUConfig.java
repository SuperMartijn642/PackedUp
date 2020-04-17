package com.supermartijn642.packedup;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Created 3/9/2020 by SuperMartijn642
 */
public class PUConfig {

    static{
        Pair<PUConfig,ForgeConfigSpec> pair = new ForgeConfigSpec.Builder().configure(PUConfig::new);
        CONFIG_SPEC = pair.getRight();
        INSTANCE = pair.getLeft();
    }

    public static final ForgeConfigSpec CONFIG_SPEC;
    public static final PUConfig INSTANCE;

    public ForgeConfigSpec.BooleanValue basicEnable;
    public ForgeConfigSpec.IntValue basicRows;
    public ForgeConfigSpec.BooleanValue ironEnable;
    public ForgeConfigSpec.IntValue ironRows;
    public ForgeConfigSpec.BooleanValue copperEnable;
    public ForgeConfigSpec.IntValue copperRows;
    public ForgeConfigSpec.BooleanValue silverEnable;
    public ForgeConfigSpec.IntValue silverRows;
    public ForgeConfigSpec.BooleanValue goldEnable;
    public ForgeConfigSpec.IntValue goldRows;
    public ForgeConfigSpec.BooleanValue diamondEnable;
    public ForgeConfigSpec.IntValue diamondRows;
    public ForgeConfigSpec.BooleanValue obsidianEnable;
    public ForgeConfigSpec.IntValue obsidianRows;

    private PUConfig(ForgeConfigSpec.Builder builder){
//        this.basicEnable = builder.worldRestart().comment("Enable the basic backpack?").define("enableBasic", true);
        this.basicRows = builder.comment("How many rows does the basic backpack have?").defineInRange("rowsBasic", BackpackType.BASIC.getDefaultRows(), 1, 13);
//        this.ironEnable = builder.worldRestart().comment("Enable the iron backpack?").define("enableIron", true);
        this.ironRows = builder.comment("How many rows does the iron backpack have?").defineInRange("rowsIron", BackpackType.IRON.getDefaultRows(), 1, 13);
//        this.copperEnable = builder.worldRestart().comment("Enable the copper backpack?").define("enableCopper", true);
        this.copperRows = builder.comment("How many rows does the copper backpack have?").defineInRange("rowsCopper", BackpackType.COPPER.getDefaultRows(), 1, 13);
//        this.silverEnable = builder.worldRestart().comment("Enable the silver backpack?").define("enableSilver", true);
        this.silverRows = builder.comment("How many rows does the silver backpack have?").defineInRange("rowsSilver", BackpackType.SILVER.getDefaultRows(), 1, 13);
//        this.goldEnable = builder.worldRestart().comment("Enable the gold backpack?").define("enableGold", true);
        this.goldRows = builder.comment("How many rows does the gold backpack have?").defineInRange("rowsGold", BackpackType.GOLD.getDefaultRows(), 1, 13);
//        this.diamondEnable = builder.worldRestart().comment("Enable the diamond backpack?").define("enableDiamond", true);
        this.diamondRows = builder.comment("How many rows does the diamond backpack have?").defineInRange("rowsDiamond", BackpackType.DIAMOND.getDefaultRows(), 1, 13);
//        this.obsidianEnable = builder.worldRestart().comment("Enable the obsidian backpack?").define("enableObsidian", true);
        this.obsidianRows = builder.comment("How many rows does the obsidian backpack have?").defineInRange("rowsObsidian", BackpackType.OBSIDIAN.getDefaultRows(), 1, 13);
    }

}
