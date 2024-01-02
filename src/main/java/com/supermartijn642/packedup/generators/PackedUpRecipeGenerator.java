package com.supermartijn642.packedup.generators;

import com.supermartijn642.core.data.condition.ResourceCondition;
import com.supermartijn642.core.data.condition.TagPopulatedResourceCondition;
import com.supermartijn642.core.generator.RecipeGenerator;
import com.supermartijn642.core.generator.ResourceCache;
import com.supermartijn642.core.registry.Registries;
import com.supermartijn642.packedup.BackpackRecipeCondition;
import com.supermartijn642.packedup.BackpackType;
import com.supermartijn642.packedup.BackpackUpgradeRecipe;
import com.supermartijn642.packedup.PackedUp;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

/**
 * Created 14/11/2022 by SuperMartijn642
 */
public class PackedUpRecipeGenerator extends RecipeGenerator {

    public PackedUpRecipeGenerator(ResourceCache cache){
        super("packedup", cache);
    }

    @Override
    public void generate(){
        // Basic
        ResourceCondition basicEnabled = new BackpackRecipeCondition(BackpackType.BASIC);
        this.shaped("basic_from_chest", PackedUp.basicbackpack)
            .pattern("ABA")
            .pattern("CDC")
            .pattern("CCC")
            .input('A', Items.STICK)
            .input('B', Items.STRING)
            .input('C', Items.LEATHER)
            .input('D', Items.CHEST)
            .unlockedBy(Items.CHEST)
            .condition(basicEnabled);

        // Iron
        ResourceCondition ironEnabled = new BackpackRecipeCondition(BackpackType.IRON);
        this.shaped("iron_from_chest", PackedUp.ironbackpack)
            .pattern("ABA")
            .pattern("CDC")
            .pattern("CCC")
            .input('A', Items.LEATHER)
            .input('B', Items.STRING)
            .input('C', ConventionalItemTags.IRON_INGOTS)
            .input('D', Items.CHEST)
            .unlockedBy(Items.CHEST)
            .condition(basicEnabled.negate())
            .condition(ironEnabled);
        this.shaped("iron_from_basic", PackedUp.ironbackpack)
            .customSerializer(BackpackUpgradeRecipe.SERIALIZER)
            .pattern("ABA")
            .pattern("ACA")
            .pattern("AAA")
            .input('A', ConventionalItemTags.IRON_INGOTS)
            .input('B', Items.LEATHER)
            .input('C', PackedUp.basicbackpack)
            .unlockedBy(PackedUp.basicbackpack)
            .condition(basicEnabled)
            .condition(ironEnabled);

        // Copper
        ResourceCondition copperEnabled = new BackpackRecipeCondition(BackpackType.COPPER);
        this.shaped("copper_from_chest", PackedUp.copperbackpack)
            .pattern("ABA")
            .pattern("CDC")
            .pattern("CCC")
            .input('A', Items.LEATHER)
            .input('B', Items.STRING)
            .input('C', ConventionalItemTags.COPPER_INGOTS)
            .input('D', Items.CHEST)
            .unlockedBy(Items.CHEST)
            .condition(basicEnabled.negate())
            .condition(copperEnabled);
        this.shaped("copper_from_basic", PackedUp.copperbackpack)
            .customSerializer(BackpackUpgradeRecipe.SERIALIZER)
            .pattern("ABA")
            .pattern("ACA")
            .pattern("AAA")
            .input('A', ConventionalItemTags.COPPER_INGOTS)
            .input('B', Items.LEATHER)
            .input('C', PackedUp.basicbackpack)
            .unlockedBy(PackedUp.basicbackpack)
            .condition(basicEnabled)
            .condition(copperEnabled);

        // Silver
        TagKey<Item> silverIngots = TagKey.create(Registries.ITEMS.getVanillaRegistry().key(), new ResourceLocation("c", "silver_ingots"));
        ResourceCondition silverEnabled = new BackpackRecipeCondition(BackpackType.SILVER).and(new TagPopulatedResourceCondition(Registries.ITEMS, new ResourceLocation("forge", "ingots/silver")));
        this.shaped("silver_from_chest", PackedUp.silverbackpack)
            .pattern("ABA")
            .pattern("CDC")
            .pattern("CCC")
            .input('A', Items.LEATHER)
            .input('B', Items.STRING)
            .input('C', silverIngots)
            .input('D', Items.CHEST)
            .unlockedBy(Items.CHEST)
            .condition(basicEnabled.negate())
            .condition(ironEnabled.negate())
            .condition(copperEnabled.negate())
            .condition(silverEnabled);
        this.shaped("silver_from_basic", PackedUp.silverbackpack)
            .customSerializer(BackpackUpgradeRecipe.SERIALIZER)
            .pattern("ABA")
            .pattern("ACA")
            .pattern("AAA")
            .input('A', silverIngots)
            .input('B', Items.LEATHER)
            .input('C', PackedUp.basicbackpack)
            .unlockedBy(PackedUp.basicbackpack)
            .condition(basicEnabled)
            .condition(ironEnabled.negate())
            .condition(copperEnabled.negate())
            .condition(silverEnabled);
        this.shaped("silver_from_iron", PackedUp.silverbackpack)
            .customSerializer(BackpackUpgradeRecipe.SERIALIZER)
            .pattern("ABA")
            .pattern("ACA")
            .pattern("AAA")
            .input('A', silverIngots)
            .input('B', ConventionalItemTags.IRON_INGOTS)
            .input('C', PackedUp.ironbackpack)
            .unlockedBy(PackedUp.ironbackpack)
            .condition(ironEnabled)
            .condition(silverEnabled);
        this.shaped("silver_from_copper", PackedUp.silverbackpack)
            .customSerializer(BackpackUpgradeRecipe.SERIALIZER)
            .pattern("ABA")
            .pattern("ACA")
            .pattern("AAA")
            .input('A', silverIngots)
            .input('B', ConventionalItemTags.COPPER_INGOTS)
            .input('C', PackedUp.copperbackpack)
            .unlockedBy(PackedUp.copperbackpack)
            .condition(copperEnabled)
            .condition(silverEnabled);

        // Gold
        ResourceCondition goldEnabled = new BackpackRecipeCondition(BackpackType.GOLD);
        this.shaped("gold_from_chest", PackedUp.goldbackpack)
            .pattern("ABA")
            .pattern("CDC")
            .pattern("CCC")
            .input('A', Items.LEATHER)
            .input('B', Items.STRING)
            .input('C', ConventionalItemTags.GOLD_INGOTS)
            .input('D', Items.CHEST)
            .unlockedBy(Items.CHEST)
            .condition(basicEnabled.negate())
            .condition(ironEnabled.negate())
            .condition(copperEnabled.negate())
            .condition(goldEnabled);
        this.shaped("gold_from_basic", PackedUp.goldbackpack)
            .customSerializer(BackpackUpgradeRecipe.SERIALIZER)
            .pattern("ABA")
            .pattern("ACA")
            .pattern("AAA")
            .input('A', ConventionalItemTags.GOLD_INGOTS)
            .input('B', Items.LEATHER)
            .input('C', PackedUp.basicbackpack)
            .unlockedBy(PackedUp.basicbackpack)
            .condition(basicEnabled)
            .condition(ironEnabled.negate())
            .condition(copperEnabled.negate())
            .condition(goldEnabled);
        this.shaped("gold_from_iron", PackedUp.goldbackpack)
            .customSerializer(BackpackUpgradeRecipe.SERIALIZER)
            .pattern("ABA")
            .pattern("ACA")
            .pattern("AAA")
            .input('A', ConventionalItemTags.GOLD_INGOTS)
            .input('B', ConventionalItemTags.IRON_INGOTS)
            .input('C', PackedUp.ironbackpack)
            .unlockedBy(PackedUp.ironbackpack)
            .condition(ironEnabled)
            .condition(goldEnabled);
        this.shaped("gold_from_copper", PackedUp.goldbackpack)
            .customSerializer(BackpackUpgradeRecipe.SERIALIZER)
            .pattern("ABA")
            .pattern("ACA")
            .pattern("AAA")
            .input('A', ConventionalItemTags.GOLD_INGOTS)
            .input('B', ConventionalItemTags.COPPER_INGOTS)
            .input('C', PackedUp.copperbackpack)
            .unlockedBy(PackedUp.copperbackpack)
            .condition(copperEnabled)
            .condition(goldEnabled);

        // Diamond
        ResourceCondition diamondEnabled = new BackpackRecipeCondition(BackpackType.DIAMOND);
        this.shaped("diamond_from_chest", PackedUp.diamondbackpack)
            .pattern("ABA")
            .pattern("CDC")
            .pattern("CEC")
            .input('A', Items.LEATHER)
            .input('B', Items.STRING)
            .input('C', ConventionalItemTags.DIAMONDS)
            .input('D', Items.CHEST)
            .input('E', ConventionalItemTags.GLASS_BLOCKS)
            .unlockedBy(Items.CHEST)
            .condition(basicEnabled.negate())
            .condition(ironEnabled.negate())
            .condition(copperEnabled.negate())
            .condition(silverEnabled.negate())
            .condition(goldEnabled.negate())
            .condition(diamondEnabled);
        this.shaped("diamond_from_basic", PackedUp.diamondbackpack)
            .customSerializer(BackpackUpgradeRecipe.SERIALIZER)
            .pattern("ABA")
            .pattern("ACA")
            .pattern("DDD")
            .input('A', ConventionalItemTags.DIAMONDS)
            .input('B', Items.LEATHER)
            .input('C', PackedUp.basicbackpack)
            .input('D', ConventionalItemTags.GLASS_BLOCKS)
            .unlockedBy(PackedUp.basicbackpack)
            .condition(basicEnabled)
            .condition(ironEnabled.negate())
            .condition(copperEnabled.negate())
            .condition(silverEnabled.negate())
            .condition(goldEnabled.negate())
            .condition(diamondEnabled);
        this.shaped("diamond_from_iron", PackedUp.diamondbackpack)
            .customSerializer(BackpackUpgradeRecipe.SERIALIZER)
            .pattern("ABA")
            .pattern("ACA")
            .pattern("DDD")
            .input('A', ConventionalItemTags.DIAMONDS)
            .input('B', ConventionalItemTags.IRON_INGOTS)
            .input('C', PackedUp.ironbackpack)
            .input('D', ConventionalItemTags.GLASS_BLOCKS)
            .unlockedBy(PackedUp.ironbackpack)
            .condition(ironEnabled)
            .condition(silverEnabled.negate())
            .condition(goldEnabled.negate())
            .condition(diamondEnabled);
        this.shaped("diamond_from_copper", PackedUp.diamondbackpack)
            .customSerializer(BackpackUpgradeRecipe.SERIALIZER)
            .pattern("ABA")
            .pattern("ACA")
            .pattern("DDD")
            .input('A', ConventionalItemTags.DIAMONDS)
            .input('B', ConventionalItemTags.COPPER_INGOTS)
            .input('C', PackedUp.copperbackpack)
            .input('D', ConventionalItemTags.GLASS_BLOCKS)
            .unlockedBy(PackedUp.copperbackpack)
            .condition(copperEnabled)
            .condition(silverEnabled.negate())
            .condition(goldEnabled.negate())
            .condition(diamondEnabled);
        this.shaped("diamond_from_silver", PackedUp.diamondbackpack)
            .customSerializer(BackpackUpgradeRecipe.SERIALIZER)
            .pattern("ABA")
            .pattern("ACA")
            .pattern("DDD")
            .input('A', ConventionalItemTags.DIAMONDS)
            .input('B', silverIngots)
            .input('C', PackedUp.silverbackpack)
            .input('D', ConventionalItemTags.GLASS_BLOCKS)
            .unlockedBy(PackedUp.silverbackpack)
            .condition(silverEnabled)
            .condition(diamondEnabled);
        this.shaped("diamond_from_gold", PackedUp.diamondbackpack)
            .customSerializer(BackpackUpgradeRecipe.SERIALIZER)
            .pattern("ABA")
            .pattern("ACA")
            .pattern("DDD")
            .input('A', ConventionalItemTags.DIAMONDS)
            .input('B', ConventionalItemTags.GOLD_INGOTS)
            .input('C', PackedUp.goldbackpack)
            .input('D', ConventionalItemTags.GLASS_BLOCKS)
            .unlockedBy(PackedUp.goldbackpack)
            .condition(goldEnabled)
            .condition(diamondEnabled);

        // Obsidian
        ResourceCondition obsidianEnabled = new BackpackRecipeCondition(BackpackType.OBSIDIAN);
        this.shaped("obsidian_from_chest", PackedUp.obsidianbackpack)
            .pattern("ABA")
            .pattern("CDC")
            .pattern("CCC")
            .input('A', Items.LEATHER)
            .input('B', Items.STRING)
            .input('C', Items.OBSIDIAN)
            .input('D', Items.CHEST)
            .unlockedBy(Items.CHEST)
            .condition(basicEnabled.negate())
            .condition(ironEnabled.negate())
            .condition(copperEnabled.negate())
            .condition(silverEnabled.negate())
            .condition(goldEnabled.negate())
            .condition(diamondEnabled.negate())
            .condition(obsidianEnabled);
        this.shaped("obsidian_from_basic", PackedUp.obsidianbackpack)
            .customSerializer(BackpackUpgradeRecipe.SERIALIZER)
            .pattern("ABA")
            .pattern("ACA")
            .pattern("AAA")
            .input('A', Items.OBSIDIAN)
            .input('B', Items.LEATHER)
            .input('C', PackedUp.basicbackpack)
            .unlockedBy(PackedUp.basicbackpack)
            .condition(basicEnabled)
            .condition(ironEnabled.negate())
            .condition(copperEnabled.negate())
            .condition(silverEnabled.negate())
            .condition(goldEnabled.negate())
            .condition(diamondEnabled.negate())
            .condition(obsidianEnabled);
        this.shaped("obsidian_from_iron", PackedUp.obsidianbackpack)
            .customSerializer(BackpackUpgradeRecipe.SERIALIZER)
            .pattern("ABA")
            .pattern("ACA")
            .pattern("AAA")
            .input('A', Items.OBSIDIAN)
            .input('B', ConventionalItemTags.IRON_INGOTS)
            .input('C', PackedUp.ironbackpack)
            .unlockedBy(PackedUp.ironbackpack)
            .condition(ironEnabled)
            .condition(silverEnabled.negate())
            .condition(goldEnabled.negate())
            .condition(diamondEnabled.negate())
            .condition(obsidianEnabled);
        this.shaped("obsidian_from_copper", PackedUp.obsidianbackpack)
            .customSerializer(BackpackUpgradeRecipe.SERIALIZER)
            .pattern("ABA")
            .pattern("ACA")
            .pattern("AAA")
            .input('A', Items.OBSIDIAN)
            .input('B', ConventionalItemTags.COPPER_INGOTS)
            .input('C', PackedUp.copperbackpack)
            .unlockedBy(PackedUp.copperbackpack)
            .condition(copperEnabled)
            .condition(silverEnabled.negate())
            .condition(goldEnabled.negate())
            .condition(diamondEnabled.negate())
            .condition(obsidianEnabled);
        this.shaped("obsidian_from_silver", PackedUp.obsidianbackpack)
            .customSerializer(BackpackUpgradeRecipe.SERIALIZER)
            .pattern("ABA")
            .pattern("ACA")
            .pattern("AAA")
            .input('A', Items.OBSIDIAN)
            .input('B', silverIngots)
            .input('C', PackedUp.silverbackpack)
            .unlockedBy(PackedUp.silverbackpack)
            .condition(silverEnabled)
            .condition(diamondEnabled.negate())
            .condition(obsidianEnabled);
        this.shaped("obsidian_from_gold", PackedUp.obsidianbackpack)
            .customSerializer(BackpackUpgradeRecipe.SERIALIZER)
            .pattern("ABA")
            .pattern("ACA")
            .pattern("AAA")
            .input('A', Items.OBSIDIAN)
            .input('B', ConventionalItemTags.GOLD_INGOTS)
            .input('C', PackedUp.goldbackpack)
            .unlockedBy(PackedUp.goldbackpack)
            .condition(goldEnabled)
            .condition(diamondEnabled.negate())
            .condition(obsidianEnabled);
        this.shaped("obsidian_from_diamond", PackedUp.obsidianbackpack)
            .customSerializer(BackpackUpgradeRecipe.SERIALIZER)
            .pattern("ABA")
            .pattern("ACA")
            .pattern("AAA")
            .input('A', Items.OBSIDIAN)
            .input('B', ConventionalItemTags.DIAMONDS)
            .input('C', PackedUp.diamondbackpack)
            .unlockedBy(PackedUp.diamondbackpack)
            .condition(diamondEnabled)
            .condition(obsidianEnabled);
    }
}
