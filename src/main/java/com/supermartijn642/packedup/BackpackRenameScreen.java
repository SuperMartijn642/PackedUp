package com.supermartijn642.packedup;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;

/**
 * Created 4/29/2020 by SuperMartijn642
 */
public class BackpackRenameScreen extends Screen {

    private static final int MAX_NAME_CHARACTER_COUNT = 23;

    private static final ResourceLocation BACKGROUND = new ResourceLocation("packedup", "textures/screen.png");
    private static final int BACKGROUND_WIDTH = 170, BACKGROUND_HEIGHT = 50;

    private TextFieldWidget nameField;
    private final String defaultName;
    private String lastTickName;

    private int left, top;

    public BackpackRenameScreen(String defaultName, String name){
        super(new TranslationTextComponent("gui.packedup.title"));
        this.defaultName = defaultName;
        this.lastTickName = name;
    }

    @Override
    protected void init(){
        this.left = (this.width - BACKGROUND_WIDTH) / 2;
        this.top = (this.height - BACKGROUND_HEIGHT) / 2;

        boolean focused = this.nameField != null && this.nameField.isFocused();
        int width = 150;
        int height = 20;
        this.children.add(this.nameField = new TextFieldWidget(this.font, (this.width - width) / 2, this.top + 20, width, height, ""));
        this.nameField.setText(this.lastTickName);
        this.nameField.setCanLoseFocus(true);
        this.nameField.setFocused2(focused);
        this.nameField.setMaxStringLength(MAX_NAME_CHARACTER_COUNT);
    }

    @Override
    public void tick(){
        this.nameField.tick();
        if(!this.lastTickName.trim().equals(this.nameField.getText().trim())){
            this.lastTickName = this.nameField.getText();
            PackedUp.CHANNEL.sendToServer(new PacketRename(this.lastTickName.trim().isEmpty() || this.lastTickName.trim().equals(this.defaultName) ? null : this.lastTickName.trim()));
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks){
        this.renderBackground();

        GlStateManager.pushMatrix();
        GlStateManager.translated(this.left, this.top, 0);
        GlStateManager.color4f(1, 1, 1, 1);
        GlStateManager.enableAlphaTest();

        Minecraft.getInstance().textureManager.bindTexture(BACKGROUND);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();
        builder.begin(7, DefaultVertexFormats.POSITION_TEX);
        builder.pos(0, 0, 0).tex(0, 0).endVertex();
        builder.pos(0, BACKGROUND_HEIGHT, 0).tex(0, 1).endVertex();
        builder.pos(BACKGROUND_WIDTH, BACKGROUND_HEIGHT, 0).tex(1, 1).endVertex();
        builder.pos(BACKGROUND_WIDTH, 0, 0).tex(1, 0).endVertex();
        tessellator.draw();

        GlStateManager.popMatrix();

        this.font.drawString(I18n.format("gui.packedup.name"), this.nameField.x + 2, this.top + 8, 4210752);
        this.nameField.render(mouseX, mouseY, partialTicks);
        super.render(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean isPauseScreen(){
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton){
        if(mouseButton == 1){ // text field
            if(mouseX >= this.nameField.x && mouseX < this.nameField.x + this.nameField.getWidth()
                && mouseY >= this.nameField.y && mouseY < this.nameField.y + this.nameField.getHeight())
                this.nameField.setText("");
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
        return false;
    }

    @Override
    public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_){
        if(super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_))
            return true;
        InputMappings.Input mouseKey = InputMappings.getInputByCode(p_keyPressed_1_, p_keyPressed_2_);
        if(!this.nameField.isFocused() && (p_keyPressed_1_ == 256 || Minecraft.getInstance().gameSettings.keyBindInventory.isActiveAndMatches(mouseKey))){
            Minecraft.getInstance().player.closeScreen();
            return true;
        }
        return false;
    }

}
