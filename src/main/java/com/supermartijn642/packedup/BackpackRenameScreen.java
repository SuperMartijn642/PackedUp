package com.supermartijn642.packedup;

import com.supermartijn642.packedup.packets.PacketRename;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;


/**
 * Created 4/29/2020 by SuperMartijn642
 */
public class BackpackRenameScreen extends GuiScreen {

    private static final int MAX_NAME_CHARACTER_COUNT = 23;

    private static final ResourceLocation BACKGROUND = new ResourceLocation(PackedUp.MODID, "textures/screen.png");
    private static final int BACKGROUND_WIDTH = 170, BACKGROUND_HEIGHT = 50;

    private GuiTextField nameField;
    private final String defaultName;
    private String lastTickName;

    private int left, top;

    public BackpackRenameScreen(String defaultName, String name){
        this.defaultName = defaultName;
        this.lastTickName = name;
    }

    @Override
    public void initGui(){
        this.left = (this.width - BACKGROUND_WIDTH) / 2;
        this.top = (this.height - BACKGROUND_HEIGHT) / 2;

        boolean focused = this.nameField != null && this.nameField.isFocused();
        int width = 150;
        int height = 20;
        this.nameField = new GuiTextField(0, this.fontRenderer, (this.width - width) / 2, this.top + 20, width, height);
        this.nameField.setText(this.lastTickName);
        this.nameField.setCanLoseFocus(true);
        this.nameField.setFocused(focused);
        this.nameField.setMaxStringLength(MAX_NAME_CHARACTER_COUNT);
    }

    @Override
    public void updateScreen(){
        this.nameField.updateCursorCounter();
        if(!this.lastTickName.trim().equals(this.nameField.getText().trim())){
            this.lastTickName = this.nameField.getText();
            PackedUp.channel.sendToServer(new PacketRename(this.lastTickName.trim().isEmpty() || this.lastTickName.trim().equals(this.defaultName) ? null : this.lastTickName.trim()));
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks){
        this.drawDefaultBackground();

        GlStateManager.pushMatrix();
        GlStateManager.translate(this.left, this.top, 0);
        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.enableAlpha();

        Minecraft.getMinecraft().getTextureManager().bindTexture(BACKGROUND);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();
        builder.begin(7, DefaultVertexFormats.POSITION_TEX);
        builder.pos(0, 0, 0).tex(0, 0).endVertex();
        builder.pos(0, BACKGROUND_HEIGHT, 0).tex(0, 1).endVertex();
        builder.pos(BACKGROUND_WIDTH, BACKGROUND_HEIGHT, 0).tex(1, 1).endVertex();
        builder.pos(BACKGROUND_WIDTH, 0, 0).tex(1, 0).endVertex();
        tessellator.draw();

        GlStateManager.popMatrix();

        this.fontRenderer.drawString(I18n.format("gui.packedup.name"), this.nameField.x + 2, this.top + 8, 4210752);
        this.nameField.drawTextBox();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean doesGuiPauseGame(){
        return false;
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException{
        this.nameField.mouseClicked(mouseX, mouseY, mouseButton);
        if(mouseButton == 1){ // text field
            if(mouseX >= this.nameField.x && mouseX < this.nameField.x + this.nameField.width
                && mouseY >= this.nameField.y && mouseY < this.nameField.y + this.nameField.height)
                this.nameField.setText("");
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char c, int keyCode) throws IOException{
        super.keyTyped(c, keyCode);
        this.nameField.textboxKeyTyped(c, keyCode);
        if(!this.nameField.isFocused() && (keyCode == 1 || this.mc.gameSettings.keyBindInventory.isActiveAndMatches(keyCode)))
            this.mc.player.closeScreen();
    }

}
