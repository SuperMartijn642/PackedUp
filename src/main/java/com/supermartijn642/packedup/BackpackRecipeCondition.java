package com.supermartijn642.packedup;

import com.google.gson.JsonObject;
import net.minecraftforge.common.crafting.IConditionFactory;
import net.minecraftforge.common.crafting.JsonContext;

import java.util.Locale;
import java.util.function.BooleanSupplier;

/**
 * Created 6/26/2021 by SuperMartijn642
 */
public class BackpackRecipeCondition implements IConditionFactory {

    @Override
    public BooleanSupplier parse(JsonContext context, JsonObject json){
        BackpackType type = BackpackType.valueOf(json.get("backpack").getAsString().toUpperCase(Locale.ROOT));
        return type::isEnabled;
    }
}
