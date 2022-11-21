package com.supermartijn642.packedup.generators;

import com.supermartijn642.core.generator.LanguageGenerator;
import com.supermartijn642.core.generator.ResourceCache;
import com.supermartijn642.packedup.PackedUp;

/**
 * Created 14/11/2022 by SuperMartijn642
 */
public class PackedUpLanguageGenerator extends LanguageGenerator {

    public PackedUpLanguageGenerator(ResourceCache cache){
        super("packedup", cache, "en_us");
    }

    @Override
    public void generate(){
        // Item group
        this.itemGroup(PackedUp.ITEM_GROUP, "Packed Up");
        
        // Backpacks
        this.item(PackedUp.basicbackpack, "Backpack");
        this.item(PackedUp.ironbackpack, "Iron Backpack");
        this.item(PackedUp.copperbackpack, "Copper Backpack");
        this.item(PackedUp.silverbackpack, "Silver Backpack");
        this.item(PackedUp.goldbackpack, "Golden Backpack");
        this.item(PackedUp.diamondbackpack, "Diamond Backpack");
        this.item(PackedUp.obsidianbackpack, "Obsidian Backpack");
        this.translation("packedup.backpacks.info.one", "Can store %d item stacks");
        this.translation("packedup.backpacks.info.two", "Can be opened with '%s'");
        this.translation("packedup.backpacks.info.inventory_index", "Inventory id: %d");
        
        // Rename screen
        this.translation("packedup.rename_screen.title", "Backpack Name");
        this.translation("packedup.rename_screen.name_field", "Backpack Name:");
        
        // Keybinding
        this.translation("packedup.keys.category", "Packed Up");
        this.translation("packedup.keys.openbag", "Open Backpack");

        // Advancements
        this.translation("packedup.advancement.basic_backpack.title", "Packing Up!");
        this.translation("packedup.advancement.basic_backpack.description", "Craft a backpack");
        this.translation("packedup.advancement.iron_or_copper_backpack.title", "Bigger is better");
        this.translation("packedup.advancement.iron_or_copper_backpack.description", "Craft an iron or copper backpack");
        this.translation("packedup.advancement.silver_or_gold_backpack.title", "Gucci backpack");
        this.translation("packedup.advancement.silver_or_gold_backpack.description", "Craft a silver or golden backpack");
        this.translation("packedup.advancement.diamond_backpack.title", "Gotta have that bling");
        this.translation("packedup.advancement.diamond_backpack.description", "Craft a diamond backpack");
        this.translation("packedup.advancement.obsidian_backpack.title", "The largest of them all");
        this.translation("packedup.advancement.obsidian_backpack.description", "Craft an obsidian backpack");
    }
}
