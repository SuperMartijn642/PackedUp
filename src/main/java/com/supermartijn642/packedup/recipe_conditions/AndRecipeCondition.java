package com.supermartijn642.packedup.recipe_conditions;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IConditionFactory;
import net.minecraftforge.common.crafting.JsonContext;

import java.util.LinkedList;
import java.util.List;
import java.util.function.BooleanSupplier;

/**
 * Created 6/26/2021 by SuperMartijn642
 */
public class AndRecipeCondition implements IConditionFactory {

    @Override
    public BooleanSupplier parse(JsonContext context, JsonObject json){
        JsonArray array = json.getAsJsonArray("values");
        List<BooleanSupplier> conditions = new LinkedList<>();
        for(JsonElement element : array){
            if(!element.isJsonObject())
                throw new JsonSyntaxException("Or condition values must be an array of JsonObjects");
            BooleanSupplier condition = CraftingHelper.getCondition(element.getAsJsonObject(), context);
            conditions.add(condition);
        }
        return () -> {
            for(BooleanSupplier condition : conditions)
                if(!condition.getAsBoolean())
                    return false;
            return true;
        };
    }
}
