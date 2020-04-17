package com.supermartijn642.packedup;

import com.google.gson.JsonObject;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.oredict.ShapedOreRecipe;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * Created 2/8/2020 by SuperMartijn642
 */
public class BackpackRecipeFactory implements IRecipeFactory {

    @Override
    public IRecipe parse(JsonContext context, JsonObject json){
        ShapedOreRecipe recipe = ShapedOreRecipe.factory(context, json);
        CraftingHelper.ShapedPrimer primer = new CraftingHelper.ShapedPrimer();
        primer.width = recipe.getRecipeWidth();
        primer.height = recipe.getRecipeHeight();
        primer.mirrored = JsonUtils.getBoolean(json, "mirrored", true);
        primer.input = recipe.getIngredients();
        return new BackpackRecipe(new ResourceLocation(PackedUp.MODID, "backpackrecipe"), recipe.getRecipeOutput(), primer);
    }

    public static class BackpackRecipe extends ShapedOreRecipe {

        public BackpackRecipe(ResourceLocation group, @Nonnull ItemStack result, CraftingHelper.ShapedPrimer primer){
            super(group, result, primer);
        }

        @Override
        public ItemStack getCraftingResult(InventoryCrafting inv){
            ItemStack backpack = inv.getStackInSlot(4);
            if(!backpack.isEmpty() && backpack.getItem() instanceof BackpackItem){
                ItemStack result = this.getRecipeOutput().copy();
                result.setTagCompound(backpack.getTagCompound());
                if(backpack.hasDisplayName())
                    result.setStackDisplayName(backpack.getDisplayName());
                for(Map.Entry<Enchantment,Integer> enchant : EnchantmentHelper.getEnchantments(backpack).entrySet())
                    result.addEnchantment(enchant.getKey(),enchant.getValue());
                return result;
            }
            return this.getRecipeOutput().copy();
        }
    }
}
