package com.supermartijn642.packedup;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * Created 2/8/2020 by SuperMartijn642
 */
public class BackpackUpgradeRecipe extends ShapedRecipe {

    public static final RecipeSerializer<BackpackUpgradeRecipe> SERIALIZER = new Serializer();

    public BackpackUpgradeRecipe(String group, CraftingBookCategory category, ShapedRecipePattern pattern, ItemStack recipeOutput, boolean showNotifications){
        super(group, category, pattern, recipeOutput, showNotifications);
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

        private static final Codec<BackpackUpgradeRecipe> CODEC = new ShapedRecipe.Serializer().codec()
            .flatXmap(
                shapedRecipe -> DataResult.success(fromShapedRecipe(shapedRecipe)),
                DataResult::success
            );

        @Override
        public Codec<BackpackUpgradeRecipe> codec(){
            return CODEC;
        }

        @Override
        public @Nullable BackpackUpgradeRecipe fromNetwork(FriendlyByteBuf buffer){
            //noinspection DataFlowIssue
            return fromShapedRecipe(RecipeSerializer.SHAPED_RECIPE.fromNetwork(buffer));
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, BackpackUpgradeRecipe recipe){
            RecipeSerializer.SHAPED_RECIPE.toNetwork(buffer, recipe);
        }

        private static BackpackUpgradeRecipe fromShapedRecipe(ShapedRecipe recipe){
            return new BackpackUpgradeRecipe(recipe.getGroup(), recipe.category(), recipe.pattern, recipe.getResultItem(null), recipe.showNotification());
        }
    }
}
