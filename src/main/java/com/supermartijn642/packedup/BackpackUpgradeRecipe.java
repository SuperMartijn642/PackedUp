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
public class BackpackUpgradeRecipe extends ShapedOreRecipe {

    public static final IRecipeFactory SERIALIZER = new Serializer();

    public BackpackUpgradeRecipe(ResourceLocation id, @Nonnull ItemStack recipeOutput, CraftingHelper.ShapedPrimer primer){
        super(id, recipeOutput, primer);
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv){
        for(int index = 0; index < inv.getSizeInventory(); index++){
            ItemStack stack = inv.getStackInSlot(index);
            if(!stack.isEmpty() && stack.getItem() instanceof BackpackItem){
                ItemStack result = this.getRecipeOutput().copy();
                result.setTagCompound(stack.getTagCompound());
                if(stack.hasDisplayName())
                    result.setStackDisplayName(stack.getDisplayName());
                for(Map.Entry<Enchantment,Integer> enchant : EnchantmentHelper.getEnchantments(stack).entrySet())
                    result.addEnchantment(enchant.getKey(), enchant.getValue());
                return result;
            }
        }
        return this.getRecipeOutput().copy();
    }

    public static class Serializer implements IRecipeFactory {

        @Override
        public IRecipe parse(JsonContext context, JsonObject json){
            ShapedOreRecipe recipe = ShapedOreRecipe.factory(context, json);
            CraftingHelper.ShapedPrimer primer = new CraftingHelper.ShapedPrimer();
            primer.width = recipe.getRecipeWidth();
            primer.height = recipe.getRecipeHeight();
            primer.mirrored = JsonUtils.getBoolean(json, "mirrored", true);
            primer.input = recipe.getIngredients();
            return new BackpackUpgradeRecipe(new ResourceLocation(recipe.getGroup()), recipe.getRecipeOutput(), primer);
        }
    }
}
