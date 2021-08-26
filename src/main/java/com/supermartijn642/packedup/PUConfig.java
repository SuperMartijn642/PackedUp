package com.supermartijn642.packedup;

import com.supermartijn642.configlib.ModConfigBuilder;

import java.util.function.Supplier;

/**
 * Created 3/9/2020 by SuperMartijn642
 */
public class PUConfig {

    public static Supplier<Boolean> allowBagInBag;
    public static Supplier<Integer> maxBagInBagLayer;
    public static Supplier<Boolean> keepBackpacksOnDeath;
    public static Supplier<Boolean> canBackpacksBurn;

    public static Supplier<Boolean> basicEnable;
    public static Supplier<Integer> basicRows;
    public static Supplier<Integer> basicColumns;
    public static Supplier<Boolean> ironEnable;
    public static Supplier<Integer> ironRows;
    public static Supplier<Integer> ironColumns;
    public static Supplier<Boolean> copperEnable;
    public static Supplier<Integer> copperRows;
    public static Supplier<Integer> copperColumns;
    public static Supplier<Boolean> silverEnable;
    public static Supplier<Integer> silverRows;
    public static Supplier<Integer> silverColumns;
    public static Supplier<Boolean> goldEnable;
    public static Supplier<Integer> goldRows;
    public static Supplier<Integer> goldColumns;
    public static Supplier<Boolean> diamondEnable;
    public static Supplier<Integer> diamondRows;
    public static Supplier<Integer> diamondColumns;
    public static Supplier<Boolean> obsidianEnable;
    public static Supplier<Integer> obsidianRows;
    public static Supplier<Integer> obsidianColumns;

    static{
        ModConfigBuilder builder = new ModConfigBuilder("packedup");

        builder.push("General");
        allowBagInBag = builder.comment("Can backpacks be put inside other backpacks?").define("allowBagInBag", true);
        maxBagInBagLayer = builder.comment("How many layers deep can you place backpacks inside backpacks? -1 for infinite, 0 is the same as setting 'allowBagInBag' to false.").define("maxBagInBagLayer", -1, -1, 5);
        keepBackpacksOnDeath = builder.comment("Should the backpack remain in the player's inventory if they die?").define("keepBackpacksOnDeath", false);
        canBackpacksBurn = builder.comment("Should backpacks be destroyed by lava and fire?").define("canBackpacksBurn", true);
        builder.pop();

        builder.push("Backpacks");
        builder.push("Basic");
        basicEnable = builder.gameRestart().comment("Enable the basic backpack?").define("basicEnable", true);
        basicRows = builder.comment("How many rows does the basic backpack have?").define("basicRows", BackpackType.BASIC.getDefaultRows(), 1, 9);
        basicColumns = builder.comment("How many columns does the basic backpack have?").define("basicColumns", BackpackType.BASIC.getDefaultColumns(), 1, 13);
        builder.pop();
        builder.push("Iron");
        ironEnable = builder.gameRestart().comment("Enable the iron backpack?").define("ironEnable", true);
        ironRows = builder.comment("How many rows does the iron backpack have?").define("ironRows", BackpackType.IRON.getDefaultRows(), 1, 9);
        ironColumns = builder.comment("How many columns does the iron backpack have?").define("ironColumns", BackpackType.IRON.getDefaultColumns(), 1, 13);
        builder.pop();
        builder.push("Copper");
        copperEnable = builder.gameRestart().comment("Enable the copper backpack?").define("copperEnable", true);
        copperRows = builder.comment("How many rows does the copper backpack have?").define("copperRows", BackpackType.COPPER.getDefaultRows(), 1, 9);
        copperColumns = builder.comment("How many columns does the copper backpack have?").define("copperColumns", BackpackType.COPPER.getDefaultColumns(), 1, 13);
        builder.pop();
        builder.push("Silver");
        silverEnable = builder.gameRestart().comment("Enable the silver backpack?").define("silverEnable", true);
        silverRows = builder.comment("How many rows does the silver backpack have?").define("silverRows", BackpackType.SILVER.getDefaultRows(), 1, 9);
        silverColumns = builder.comment("How many columns does the silver backpack have?").define("silverColumns", BackpackType.SILVER.getDefaultColumns(), 1, 13);
        builder.pop();
        builder.push("Gold");
        goldEnable = builder.gameRestart().comment("Enable the gold backpack?").define("goldEnable", true);
        goldRows = builder.comment("How many rows does the gold backpack have?").define("goldRows", BackpackType.GOLD.getDefaultRows(), 1, 9);
        goldColumns = builder.comment("How many columns does the gold backpack have?").define("goldColumns", BackpackType.GOLD.getDefaultColumns(), 1, 13);
        builder.pop();
        builder.push("Diamond");
        diamondEnable = builder.gameRestart().comment("Enable the diamond backpack?").define("diamondEnable", true);
        diamondRows = builder.comment("How many rows does the diamond backpack have?").define("diamondRows", BackpackType.DIAMOND.getDefaultRows(), 1, 9);
        diamondColumns = builder.comment("How many columns does the diamond backpack have?").define("diamondColumns", BackpackType.DIAMOND.getDefaultColumns(), 1, 13);
        builder.pop();
        builder.push("Obsidian");
        obsidianEnable = builder.gameRestart().comment("Enable the obsidian backpack?").define("obsidianEnable", true);
        obsidianRows = builder.comment("How many rows does the obsidian backpack have?").define("obsidianRows", BackpackType.OBSIDIAN.getDefaultRows(), 1, 9);
        obsidianColumns = builder.comment("How many columns does the obsidian backpack have?").define("obsidianColumns", BackpackType.OBSIDIAN.getDefaultColumns(), 1, 13);
        builder.pop();
        builder.pop();

        builder.build();
    }

}
