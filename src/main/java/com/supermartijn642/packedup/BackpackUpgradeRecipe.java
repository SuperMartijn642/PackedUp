package com.supermartijn642.packedup;

import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * Created 2/8/2020 by SuperMartijn642
 */
public class BackpackUpgradeRecipe extends ShapedRecipe {

    public static final RecipeSerializer<BackpackUpgradeRecipe> SERIALIZER = new Serializer();

    public BackpackUpgradeRecipe(ResourceLocation id, String group, CraftingBookCategory category, int recipeWidth, int recipeHeight, NonNullList<Ingredient> recipeItems, ItemStack recipeOutput){
        super(id, group, category, recipeWidth, recipeHeight, recipeItems, recipeOutput);
    }

    @Override
    public ItemStack assemble(CraftingContainer inventory, RegistryAccess registryAccess){
        for(int index = 0; index < inventory.getContainerSize(); index++){
            ItemStack stack = inventory.getItem(index);
            if(!stack.isEmpty() && stack.getItem() instanceof BackpackItem){
                ItemStack result = this.getResultItem(registryAccess).copy();
                result.setTag(stack.getTag());
                if(stack.hasCustomHoverName())
                    result.setHoverName(stack.getHoverName());
                for(Map.Entry<Enchantment,Integer> enchant : EnchantmentHelper.getEnchantments(stack).entrySet())
                    result.enchant(enchant.getKey(), enchant.getValue());
                return result;
            }
        }
        return this.getResultItem(registryAccess).copy();
    }

    private static class Serializer implements RecipeSerializer<BackpackUpgradeRecipe> {

        @Override
        public BackpackUpgradeRecipe fromJson(ResourceLocation recipeId, JsonObject json){
            ShapedRecipe recipe = RecipeSerializer.SHAPED_RECIPE.fromJson(recipeId, json);
            return new BackpackUpgradeRecipe(recipeId, recipe.getGroup(), recipe.category(), recipe.getRecipeWidth(), recipe.getRecipeHeight(), recipe.getIngredients(), recipe.getResultItem(null));
        }

        @Nullable
        @Override
        public BackpackUpgradeRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer){
            ShapedRecipe recipe = RecipeSerializer.SHAPED_RECIPE.fromNetwork(recipeId, buffer);
            return new BackpackUpgradeRecipe(recipeId, recipe.getGroup(), recipe.category(), recipe.getRecipeWidth(), recipe.getRecipeHeight(), recipe.getIngredients(), recipe.getResultItem(null));
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, BackpackUpgradeRecipe recipe){
            RecipeSerializer.SHAPED_RECIPE.toNetwork(buffer, recipe);
        }
    }
}
