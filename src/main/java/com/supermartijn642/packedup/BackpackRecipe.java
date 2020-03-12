package com.supermartijn642.packedup;

import com.google.gson.JsonObject;
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

/**
 * Created 2/8/2020 by SuperMartijn642
 */
public class BackpackRecipe extends ShapedRecipe {

    public BackpackRecipe(ResourceLocation idIn, String groupIn, int recipeWidthIn, int recipeHeightIn, NonNullList<Ingredient> recipeItemsIn, ItemStack recipeOutputIn){
        super(idIn, groupIn, recipeWidthIn, recipeHeightIn, recipeItemsIn, recipeOutputIn);
    }

    @Override
    public ItemStack getCraftingResult(CraftingInventory inv){
        ItemStack backpack = inv.getStackInSlot(4);
        if(!backpack.isEmpty() && backpack.getItem() instanceof BackpackItem){
            ItemStack result = this.getRecipeOutput().copy();
            result.setTag(backpack.getTag());
            return result;
        }
        return this.getRecipeOutput().copy();
    }

    @Override
    public IRecipeSerializer<?> getSerializer(){
        return super.getSerializer();
    }

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<BackpackRecipe> {
        @Override
        public BackpackRecipe read(ResourceLocation recipeId, JsonObject json){
            ShapedRecipe recipe = IRecipeSerializer.CRAFTING_SHAPED.read(recipeId, json);
            return new BackpackRecipe(recipeId, recipe.getGroup(), recipe.getRecipeWidth(), recipe.getRecipeHeight(), recipe.getIngredients(), recipe.getRecipeOutput());
        }

        @Nullable
        @Override
        public BackpackRecipe read(ResourceLocation recipeId, PacketBuffer buffer){
            ShapedRecipe recipe = IRecipeSerializer.CRAFTING_SHAPED.read(recipeId, buffer);
            return new BackpackRecipe(recipeId, recipe.getGroup(), recipe.getRecipeWidth(), recipe.getRecipeHeight(), recipe.getIngredients(), recipe.getRecipeOutput());
        }

        @Override
        public void write(PacketBuffer buffer, BackpackRecipe recipe){
            IRecipeSerializer.CRAFTING_SHAPED.write(buffer, recipe);
        }
    }
}
