package com.supermartijn642.packedup;

import com.google.gson.JsonObject;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * Created 2/8/2020 by SuperMartijn642
 */
public class BackpackUpgradeRecipe extends ShapedRecipe {

    public static final IRecipeSerializer<BackpackUpgradeRecipe> SERIALIZER = new Serializer();

    public BackpackUpgradeRecipe(ResourceLocation id, String group, int recipeWidth, int recipeHeight, NonNullList<Ingredient> recipeItems, ItemStack recipeOutput){
        super(id, group, recipeWidth, recipeHeight, recipeItems, recipeOutput);
    }

    @Override
    public ItemStack assemble(CraftingInventory inventory){
        for(int index = 0; index < inventory.getContainerSize(); index++){
            ItemStack stack = inventory.getItem(index);
            if(!stack.isEmpty() && stack.getItem() instanceof BackpackItem){
                ItemStack result = this.getResultItem().copy();
                result.setTag(stack.getTag());
                if(stack.hasCustomHoverName())
                    result.setHoverName(stack.getHoverName());
                for(Map.Entry<Enchantment,Integer> enchant : EnchantmentHelper.getEnchantments(stack).entrySet())
                    result.enchant(enchant.getKey(), enchant.getValue());
                return result;
            }
        }
        return this.getResultItem().copy();
    }

    private static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<BackpackUpgradeRecipe> {

        @Override
        public BackpackUpgradeRecipe fromJson(ResourceLocation recipeId, JsonObject json){
            ShapedRecipe recipe = IRecipeSerializer.SHAPED_RECIPE.fromJson(recipeId, json);
            return new BackpackUpgradeRecipe(recipeId, recipe.getGroup(), recipe.getRecipeWidth(), recipe.getRecipeHeight(), recipe.getIngredients(), recipe.getResultItem());
        }

        @Nullable
        @Override
        public BackpackUpgradeRecipe fromNetwork(ResourceLocation recipeId, PacketBuffer buffer){
            ShapedRecipe recipe = IRecipeSerializer.SHAPED_RECIPE.fromNetwork(recipeId, buffer);
            return new BackpackUpgradeRecipe(recipeId, recipe.getGroup(), recipe.getRecipeWidth(), recipe.getRecipeHeight(), recipe.getIngredients(), recipe.getResultItem());
        }

        @Override
        public void toNetwork(PacketBuffer buffer, BackpackUpgradeRecipe recipe){
            IRecipeSerializer.SHAPED_RECIPE.toNetwork(buffer, recipe);
        }
    }
}
