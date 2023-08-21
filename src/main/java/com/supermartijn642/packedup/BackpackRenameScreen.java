package com.supermartijn642.packedup;

import com.mojang.blaze3d.vertex.PoseStack;
import com.supermartijn642.core.ClientUtils;
import com.supermartijn642.core.TextComponents;
import com.supermartijn642.core.gui.ScreenUtils;
import com.supermartijn642.core.gui.widget.BaseWidget;
import com.supermartijn642.core.gui.widget.premade.TextFieldWidget;
import com.supermartijn642.packedup.packets.PacketRename;
import net.minecraft.network.chat.Component;

/**
 * Created 4/29/2020 by SuperMartijn642
 */
public class BackpackRenameScreen extends BaseWidget {

    private static final int MAX_NAME_CHARACTER_COUNT = 23;

    private TextFieldWidget nameField;
    private final String defaultName, startName;

    public BackpackRenameScreen(String defaultName, String name){
        super(0, 0, 170, 50);
        this.defaultName = defaultName;
        this.startName = name;
    }

    @Override
    public Component getNarrationMessage(){
        return TextComponents.translation("packedup.rename_screen.title").get();
    }

    @Override
    protected void addWidgets(){
        this.nameField = this.addWidget(new TextFieldWidget((this.width() - 150) / 2, 20, 150, 20, this.startName, MAX_NAME_CHARACTER_COUNT,
            name -> PackedUp.CHANNEL.sendToServer(new PacketRename(name))
        ));
        this.nameField.setSuggestion(this.defaultName);
        this.nameField.setFocused(true);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY){
        ScreenUtils.drawScreenBackground(poseStack, 0, 0, this.width(), this.height());
        ScreenUtils.drawString(poseStack, TextComponents.translation("packedup.rename_screen.name_field").get(), this.nameField.left() + 2, 8, 4210752);

        super.render(poseStack, mouseX, mouseY);
    }

    @Override
    public boolean keyPressed(int keyCode, boolean hasBeenHandled){
        if(!hasBeenHandled && keyCode == 257 /* Enter */){
            ClientUtils.getPlayer().closeContainer();
            hasBeenHandled = true;
        }
        hasBeenHandled |= super.keyPressed(keyCode, hasBeenHandled);
        return hasBeenHandled;
    }
}
