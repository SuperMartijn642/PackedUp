package com.supermartijn642.packedup;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

import java.util.Locale;

/**
 * Created 6/26/2021 by SuperMartijn642
 */
public class BackpackRecipeCondition implements ICondition {

    public static final Serializer SERIALIZER = new Serializer();

    private final BackpackType type;

    public BackpackRecipeCondition(BackpackType type){
        this.type = type;
    }

    @Override
    public ResourceLocation getID(){
        return SERIALIZER.getID();
    }

    @Override
    public boolean test(){
        return this.type.isEnabled();
    }

    @Override
    public String toString(){
        return "backpackEnabled(" + this.type.name() + ")";
    }

    public static class Serializer implements IConditionSerializer<BackpackRecipeCondition> {

        @Override
        public void write(JsonObject json, BackpackRecipeCondition value){
            json.addProperty("backpack", value.type.name().toLowerCase(Locale.ROOT));
        }

        @Override
        public BackpackRecipeCondition read(JsonObject json){
            BackpackType type = BackpackType.valueOf(json.get("backpack").getAsString().toUpperCase(Locale.ROOT));
            return new BackpackRecipeCondition(type);
        }

        @Override
        public ResourceLocation getID(){
            return new ResourceLocation("packedup", "enabled");
        }
    }
}
