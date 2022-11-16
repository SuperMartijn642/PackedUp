package com.supermartijn642.packedup.generators;

import com.supermartijn642.core.generator.ModelGenerator;
import com.supermartijn642.core.generator.ResourceCache;
import com.supermartijn642.packedup.BackpackType;
import net.minecraft.resources.ResourceLocation;

/**
 * Created 14/11/2022 by SuperMartijn642
 */
public class PackedUpModelGenerator extends ModelGenerator {

    public PackedUpModelGenerator(ResourceCache cache){
        super("packedup", cache);
    }

    @Override
    public void generate(){
        for(BackpackType type : BackpackType.values())
            this.itemGenerated("item/" + type.getRegistryName(), new ResourceLocation("packedup", "items/" + type.getRegistryName()));
    }
}
