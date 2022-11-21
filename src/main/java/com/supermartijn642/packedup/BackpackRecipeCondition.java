package com.supermartijn642.packedup;

import com.google.gson.JsonObject;
import com.supermartijn642.core.data.condition.ResourceCondition;
import com.supermartijn642.core.data.condition.ResourceConditionContext;
import com.supermartijn642.core.data.condition.ResourceConditionSerializer;

import java.util.Locale;

/**
 * Created 6/26/2021 by SuperMartijn642
 */
public class BackpackRecipeCondition implements ResourceCondition {

    public static final ResourceConditionSerializer<BackpackRecipeCondition> SERIALIZER = new Serializer();

    private final BackpackType type;

    public BackpackRecipeCondition(BackpackType type){
        this.type = type;
    }

    @Override
    public String toString(){
        return "backpackEnabled(" + this.type.name() + ")";
    }

    @Override
    public boolean test(ResourceConditionContext context){
        return this.type.isEnabled();
    }

    @Override
    public ResourceConditionSerializer<?> getSerializer(){
        return SERIALIZER;
    }

    public static class Serializer implements ResourceConditionSerializer<BackpackRecipeCondition> {

        @Override
        public void serialize(JsonObject json, BackpackRecipeCondition condition){
            json.addProperty("backpack", condition.type.name().toLowerCase(Locale.ROOT));
        }

        @Override
        public BackpackRecipeCondition deserialize(JsonObject json){
            BackpackType type = BackpackType.valueOf(json.get("backpack").getAsString().toUpperCase(Locale.ROOT));
            return new BackpackRecipeCondition(type);
        }
    }
}
