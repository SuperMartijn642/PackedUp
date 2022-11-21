package com.supermartijn642.packedup;

import java.util.Locale;
import java.util.function.Supplier;

/**
 * Created 2/7/2020 by SuperMartijn642
 */
public enum BackpackType {

    BASIC(() -> PackedUpConfig.basicEnable.get(), 3, 9, () -> PackedUpConfig.basicRows.get(), () -> PackedUpConfig.basicColumns.get()),
    IRON(() -> PackedUpConfig.ironEnable.get(), 4, 9, () -> PackedUpConfig.ironRows.get(), () -> PackedUpConfig.ironColumns.get()),
    COPPER(() -> PackedUpConfig.copperEnable.get(), 4, 9, () -> PackedUpConfig.copperRows.get(), () -> PackedUpConfig.copperColumns.get()),
    SILVER(() -> PackedUpConfig.silverEnable.get(), 5, 9, () -> PackedUpConfig.silverRows.get(), () -> PackedUpConfig.silverColumns.get()),
    GOLD(() -> PackedUpConfig.goldEnable.get(), 5, 9, () -> PackedUpConfig.goldRows.get(), () -> PackedUpConfig.goldColumns.get()),
    DIAMOND(() -> PackedUpConfig.diamondEnable.get(), 7, 9, () -> PackedUpConfig.diamondRows.get(), () -> PackedUpConfig.diamondColumns.get()),
    OBSIDIAN(() -> PackedUpConfig.obsidianEnable.get(), 8, 9, () -> PackedUpConfig.obsidianRows.get(), () -> PackedUpConfig.obsidianColumns.get());

    private final Supplier<Boolean> enabled;
    private final int defaultRows, defaultColumns;
    private final Supplier<Integer> rows, columns;

    BackpackType(Supplier<Boolean> enabled, int defaultRows, int defaultColumns, Supplier<Integer> rows, Supplier<Integer> columns){
        this.enabled = enabled;
        this.defaultRows = defaultRows;
        this.defaultColumns = defaultColumns;
        this.rows = rows;
        this.columns = columns;
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
        return this.enabled.get();
    }

    public int getRows(){
        return this.rows.get();
    }

    public int getColumns(){
        return this.columns.get();
    }

    public int getSlots(){
        return this.getRows() * this.getColumns();
    }
}
