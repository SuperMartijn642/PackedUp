package com.supermartijn642.packedup;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;

/**
 * Created 2/8/2020 by SuperMartijn642
 */
public class BackpackUpgradeRecipe extends ShapedRecipe {

    private static final MapCodec<BackpackUpgradeRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance.group(
            Recipe.CommonInfo.MAP_CODEC.forGetter(recipe -> recipe.commonInfo),
            CraftingRecipe.CraftingBookInfo.MAP_CODEC.forGetter(recipe -> recipe.bookInfo),
            ShapedRecipePattern.MAP_CODEC.forGetter(recipe -> recipe.pattern),
            ItemStackTemplate.CODEC.fieldOf("result").forGetter(recipe -> recipe.result)
        ).apply(instance, BackpackUpgradeRecipe::new));
    public static final StreamCodec<RegistryFriendlyByteBuf,BackpackUpgradeRecipe> STREAM_CODEC = StreamCodec.composite(
        Recipe.CommonInfo.STREAM_CODEC,
        recipe -> recipe.commonInfo,
        CraftingRecipe.CraftingBookInfo.STREAM_CODEC,
        recipe -> recipe.bookInfo,
        ShapedRecipePattern.STREAM_CODEC,
        recipe -> recipe.pattern,
        ItemStackTemplate.STREAM_CODEC,
        recipe -> recipe.result,
        BackpackUpgradeRecipe::new
    );
    public static final RecipeSerializer<BackpackUpgradeRecipe> SERIALIZER = new RecipeSerializer<>(CODEC, STREAM_CODEC);

    private final ShapedRecipePattern pattern;
    private final ItemStackTemplate result;

    public BackpackUpgradeRecipe(CommonInfo commonInfo, CraftingBookInfo craftingBookInfo, ShapedRecipePattern pattern, ItemStackTemplate result){
        super(commonInfo, craftingBookInfo, pattern, result);
        this.pattern = pattern;
        this.result = result;
    }

    @Override
    public ItemStack assemble(CraftingInput input){
        for(int index = 0; index < input.ingredientCount(); index++){
            ItemStack stack = input.getItem(index);
            if(!stack.isEmpty() && stack.getItem() instanceof BackpackItem && stack.has(BackpackItem.INVENTORY_ID)){
                ItemStack result = this.result.create();
                result.set(BackpackItem.INVENTORY_ID, stack.get(BackpackItem.INVENTORY_ID));
                if(stack.has(DataComponents.CUSTOM_NAME))
                    result.set(DataComponents.CUSTOM_NAME, stack.get(DataComponents.CUSTOM_NAME));
                if(stack.has(DataComponents.ENCHANTMENTS))
                    result.set(DataComponents.ENCHANTMENTS, stack.get(DataComponents.ENCHANTMENTS));
                return result;
            }
        }
        return this.result.create();
    }

    @Override
    public RecipeSerializer<ShapedRecipe> getSerializer(){
        //noinspection unchecked,rawtypes
        return (RecipeSerializer)SERIALIZER;
    }
}
