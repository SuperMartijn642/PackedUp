package com.supermartijn642.packedup.generators;

import com.supermartijn642.core.generator.AtlasSourceGenerator;
import com.supermartijn642.core.generator.ResourceCache;
import com.supermartijn642.packedup.screen.BackpackContainerScreen;
import com.supermartijn642.packedup.screen.customization.IconSelectionScreen;
import com.supermartijn642.packedup.screen.customization.IconSlotWidget;

/**
 * Created 29/06/2025 by SuperMartijn642
 */
public class PackedUpAtlasSourceGenerator extends AtlasSourceGenerator {

    public PackedUpAtlasSourceGenerator(ResourceCache cache){
        super("packedup", cache);
    }

    @Override
    public void generate(){
        this.guiAtlas()
            .texture(IconSlotWidget.ICON_SLOT_TEXTURE)
            .texture(BackpackContainerScreen.CORNERS)
            .texture(IconSelectionScreen.SLOT_HIGHLIGHT_TEXTURE)
            .texture(IconSelectionScreen.BACKGROUND_TEXTURE);
    }
}
