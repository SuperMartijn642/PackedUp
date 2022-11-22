package com.supermartijn642.packedup.generators;

import com.supermartijn642.core.generator.ResourceCache;
import com.supermartijn642.core.generator.TagGenerator;
import com.supermartijn642.packedup.PackedUp;

/**
 * Created 14/11/2022 by SuperMartijn642
 */
public class PackedUpTagGenerator extends TagGenerator {

    public PackedUpTagGenerator(ResourceCache cache){
        super("packedup", cache);
    }

    @Override
    public void generate(){
        // Create empty silver and copper tags
        this.itemTag("forge", "ingots/copper");
        this.itemTag("forge", "ingots/silver");

        // Curios compatibility
        this.itemTag("curios", "back")
            .add(PackedUp.basicbackpack)
            .add(PackedUp.ironbackpack)
            .add(PackedUp.copperbackpack)
            .add(PackedUp.silverbackpack)
            .add(PackedUp.goldbackpack)
            .add(PackedUp.diamondbackpack)
            .add(PackedUp.obsidianbackpack);
    }
}
