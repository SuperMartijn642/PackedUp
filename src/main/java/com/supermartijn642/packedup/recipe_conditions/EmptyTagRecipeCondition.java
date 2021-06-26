package com.supermartijn642.packedup.recipe_conditions;

import com.google.gson.JsonObject;
import net.minecraftforge.common.crafting.IConditionFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.oredict.OreDictionary;

import java.util.function.BooleanSupplier;

/**
 * Created 6/26/2021 by SuperMartijn642
 */
public class EmptyTagRecipeCondition implements IConditionFactory {

    @Override
    public BooleanSupplier parse(JsonContext context, JsonObject json){
        String tag = json.get("tag").getAsString();
        return () -> !OreDictionary.doesOreNameExist(tag);
    }
}
