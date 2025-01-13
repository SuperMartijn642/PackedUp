package com.supermartijn642.packedup.generators;

import com.supermartijn642.core.generator.ItemInfoGenerator;
import com.supermartijn642.core.generator.ResourceCache;
import com.supermartijn642.packedup.BackpackIconRenderer;
import com.supermartijn642.packedup.BackpackType;

/**
 * Created 23/12/2024 by SuperMartijn642
 */
public class PackedUpItemInfoGenerator extends ItemInfoGenerator {

    public PackedUpItemInfoGenerator(ResourceCache cache){
        super("packedup", cache);
    }

    @Override
    public void generate(){
        for(BackpackType type : BackpackType.values())
            this.info(type.getRegistryName()).model(
                this.compositeModel()
                    .addModel(this.model("item/" + type.getRegistryName()))
                    .addModel(new BackpackIconRenderer())
            );
    }
}
