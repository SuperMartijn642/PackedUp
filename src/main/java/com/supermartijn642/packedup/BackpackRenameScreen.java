package com.supermartijn642.packedup;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.supermartijn642.core.TextComponents;
import com.supermartijn642.core.gui.BaseScreen;
import com.supermartijn642.core.gui.ScreenUtils;
import com.supermartijn642.core.gui.widget.TextFieldWidget;
import com.supermartijn642.packedup.packets.PacketRename;
import net.minecraft.util.text.TranslationTextComponent;

/**
 * Created 4/29/2020 by SuperMartijn642
 */
public class BackpackRenameScreen extends BaseScreen {

    private static final int MAX_NAME_CHARACTER_COUNT = 23;

    private TextFieldWidget nameField;
    private final String defaultName, startName;

    public BackpackRenameScreen(String defaultName, String name){
        super(new TranslationTextComponent("gui.packedup.title"));
        this.defaultName = defaultName;
        this.startName = name;
    }

    @Override
    protected float sizeX(){
        return 170;
    }

    @Override
    protected float sizeY(){
        return 50;
    }

    @Override
    protected void addWidgets(){
        this.nameField = this.addWidget(new TextFieldWidget(((int)this.sizeX() - 150) / 2, 20, 150, 20, this.startName, MAX_NAME_CHARACTER_COUNT,
            name -> PackedUp.CHANNEL.sendToServer(new PacketRename(name))
        ));
        this.nameField.setSuggestion(this.defaultName);
        this.nameField.setFocused(true);
    }

    @Override
    protected void render(MatrixStack matrixStack, int mouseX, int mouseY){
        ScreenUtils.drawScreenBackground(matrixStack, 0, 0, this.sizeX(), this.sizeY());
        ScreenUtils.drawString(matrixStack, TextComponents.translation("gui.packedup.name").get(), this.nameField.x + 2, 8, 4210752);
    }

}
