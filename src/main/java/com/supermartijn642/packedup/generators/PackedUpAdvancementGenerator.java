package com.supermartijn642.packedup.generators;

import com.supermartijn642.core.generator.AdvancementGenerator;
import com.supermartijn642.core.generator.ResourceCache;
import com.supermartijn642.packedup.PackedUp;

/**
 * Created 14/11/2022 by SuperMartijn642
 */
public class PackedUpAdvancementGenerator extends AdvancementGenerator {

    public PackedUpAdvancementGenerator(ResourceCache cache){
        super("packedup", cache);
    }

    @Override
    public void generate(){
        this.advancement("basic_backpack")
            .icon(PackedUp.basicbackpack)
            .background("minecraft", "blocks/wool_colored_black")
            .hasItemsCriterion("has_backpack", PackedUp.basicbackpack)
            .requirementGroup("has_backpack");
        this.advancement("iron_or_copper_backpack")
            .parent("basic_backpack")
            .icon(PackedUp.ironbackpack)
            .hasItemsCriterion("has_iron_backpack", PackedUp.ironbackpack)
            .hasItemsCriterion("has_copper_backpack", PackedUp.copperbackpack)
            .requirementGroup("has_iron_backpack", "has_copper_backpack");
        this.advancement("silver_or_gold_backpack")
            .parent("iron_or_copper_backpack")
            .icon(PackedUp.goldbackpack)
            .hasItemsCriterion("has_silver_backpack", PackedUp.silverbackpack)
            .hasItemsCriterion("has_gold_backpack", PackedUp.goldbackpack)
            .requirementGroup("has_silver_backpack", "has_gold_backpack");
        this.advancement("diamond_backpack")
            .parent("silver_or_gold_backpack")
            .icon(PackedUp.diamondbackpack)
            .hasItemsCriterion("has_backpack", PackedUp.diamondbackpack)
            .requirementGroup("has_backpack");
        this.advancement("obsidian_backpack")
            .parent("diamond_backpack")
            .icon(PackedUp.obsidianbackpack)
            .goalFrame()
            .hasItemsCriterion("has_backpack", PackedUp.obsidianbackpack)
            .requirementGroup("has_backpack");
    }
}
