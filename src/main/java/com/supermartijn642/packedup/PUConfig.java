package com.supermartijn642.packedup;

import net.minecraftforge.common.config.Configuration;

import java.io.File;
import java.util.HashMap;

/**
 * Created 3/9/2020 by SuperMartijn642
 */
public class PUConfig {

    private static final String FILE_NAME = "packedup.cfg";

    public static Configuration instance;
    public static HashMap<BackpackType,Integer> rows = new HashMap<>();

    public static void init(File dir){
        instance = new Configuration(new File(dir, FILE_NAME));
        instance.load();

        instance.addCustomCategoryComment(Configuration.CATEGORY_GENERAL, "");
        for(BackpackType type : BackpackType.values()){
            rows.put(type, instance.getInt("rows" + type.name().substring(0, 1).toUpperCase() + type.name().substring(1).toLowerCase(), Configuration.CATEGORY_GENERAL, type.getDefaultRows(), 1, 8, "How many rows does the " + type.name().toLowerCase() + " backpack have?"));
        }

        if(instance.hasChanged())
            instance.save();
    }

}
