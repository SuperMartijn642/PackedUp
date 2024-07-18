package com.supermartijn642.packedup;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

/**
 * Created 2/8/2020 by SuperMartijn642
 */
public class BackpackUpgradeRecipe extends ShapedRecipe {

    public static final RecipeSerializer<BackpackUpgradeRecipe> SERIALIZER = new Serializer();

    private final String group;
    private final CraftingBookCategory category;
    private final ShapedRecipePattern pattern;
    private final ItemStack result;
    private final boolean showNotification;

    public BackpackUpgradeRecipe(String group, CraftingBookCategory category, ShapedRecipePattern pattern, ItemStack recipeOutput, boolean showNotification){
        super(group, category, pattern, recipeOutput, showNotification);
        this.group = group;
        this.category = category;
        this.pattern = pattern;
        this.result = recipeOutput;
        this.showNotification = showNotification;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider provider){
        for(int index = 0; index < input.ingredientCount(); index++){
            ItemStack stack = input.getItem(index);
            if(!stack.isEmpty() && stack.getItem() instanceof BackpackItem && stack.has(BackpackItem.INVENTORY_ID)){
                ItemStack result = this.getResultItem(provider).copy();
                result.set(BackpackItem.INVENTORY_ID, stack.get(BackpackItem.INVENTORY_ID));
                if(stack.has(DataComponents.CUSTOM_NAME))
                    result.set(DataComponents.CUSTOM_NAME, stack.get(DataComponents.CUSTOM_NAME));
                if(stack.has(DataComponents.ENCHANTMENTS))
                    result.set(DataComponents.ENCHANTMENTS, stack.get(DataComponents.ENCHANTMENTS));
                return result;
            }
        }
        return this.getResultItem(provider).copy();
    }

    private static class Serializer implements RecipeSerializer<BackpackUpgradeRecipe> {

        private static final MapCodec<BackpackUpgradeRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                Codec.STRING.optionalFieldOf("group", "").forGetter(recipe -> recipe.group),
                CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(recipe -> recipe.category),
                ShapedRecipePattern.MAP_CODEC.forGetter(recipe -> recipe.pattern),
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(recipe -> recipe.result),
                Codec.BOOL.optionalFieldOf("show_notification", true).forGetter(recipe -> recipe.showNotification)
            ).apply(instance, BackpackUpgradeRecipe::new));

        @Override
        public MapCodec<BackpackUpgradeRecipe> codec(){
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf,BackpackUpgradeRecipe> streamCodec(){
            return StreamCodec.of(Serializer::toNetwork, Serializer::fromNetwork);
        }

        public static BackpackUpgradeRecipe fromNetwork(RegistryFriendlyByteBuf buffer){
            return fromShapedRecipe(ShapedRecipe.Serializer.fromNetwork(buffer));
        }

        public static void toNetwork(RegistryFriendlyByteBuf buffer, BackpackUpgradeRecipe recipe){
            ShapedRecipe.Serializer.toNetwork(buffer, recipe);
        }

        private static BackpackUpgradeRecipe fromShapedRecipe(ShapedRecipe recipe){
            return new BackpackUpgradeRecipe(recipe.getGroup(), recipe.category(), recipe.pattern, recipe.getResultItem(null), recipe.showNotification());
        }
    }
}
