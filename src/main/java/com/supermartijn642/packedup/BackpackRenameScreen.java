package com.supermartijn642.packedup;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
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
    protected void func_231160_c_(){
        this.left = (this.field_230708_k_ - BACKGROUND_WIDTH) / 2;
        this.top = (this.field_230709_l_ - BACKGROUND_HEIGHT) / 2;

        boolean focused = this.nameField != null && this.nameField.func_230999_j_();
        int width = 150;
        int height = 20;
        this.field_230705_e_.add(this.nameField = new TextFieldWidget(this.field_230712_o_, (this.field_230708_k_ - width) / 2, this.top + 20, width, height, new StringTextComponent("")));
        this.nameField.setText(this.lastTickName);
        this.nameField.setCanLoseFocus(true);
        this.nameField.setFocused2(focused);
        this.nameField.setMaxStringLength(MAX_NAME_CHARACTER_COUNT);
    }

    @Override
    public void func_231023_e_(){
        this.nameField.tick();
        if(!this.lastTickName.trim().equals(this.nameField.getText().trim())){
            this.lastTickName = this.nameField.getText();
            PackedUp.CHANNEL.sendToServer(new PacketRename(this.lastTickName.trim().isEmpty() || this.lastTickName.trim().equals(this.defaultName) ? null : this.lastTickName.trim()));
        }
    }

    @Override
    public void func_230430_a_(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks){
        this.func_230446_a_(matrixStack);

        RenderSystem.pushMatrix();
        RenderSystem.translated(this.left, this.top, 0);
        RenderSystem.color4f(1, 1, 1, 1);
        RenderSystem.enableAlphaTest();

        Minecraft.getInstance().textureManager.bindTexture(BACKGROUND);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();
        builder.begin(7, DefaultVertexFormats.POSITION_TEX);
        builder.pos(0, 0, 0).tex(0, 0).endVertex();
        builder.pos(0, BACKGROUND_HEIGHT, 0).tex(0, 1).endVertex();
        builder.pos(BACKGROUND_WIDTH, BACKGROUND_HEIGHT, 0).tex(1, 1).endVertex();
        builder.pos(BACKGROUND_WIDTH, 0, 0).tex(1, 0).endVertex();
        tessellator.draw();

        RenderSystem.popMatrix();

        this.field_230712_o_.func_238422_b_(matrixStack,new TranslationTextComponent("gui.packedup.name"), this.nameField.field_230690_l_ + 2, this.top + 8, 4210752);
        this.nameField.func_230430_a_(matrixStack, mouseX, mouseY, partialTicks);
        super.func_230430_a_(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean func_231177_au__(){
        return false;
    }

    @Override
    public boolean func_231044_a_(double mouseX, double mouseY, int mouseButton){
        if(mouseButton == 1){ // text field
            if(mouseX >= this.nameField.field_230690_l_ && mouseX < this.nameField.field_230690_l_ + this.nameField.func_230998_h_()
                && mouseY >= this.nameField.field_230691_m_ && mouseY < this.nameField.field_230691_m_ + this.nameField.getHeight())
                this.nameField.setText("");
        }
        super.func_231044_a_(mouseX, mouseY, mouseButton);
        return false;
    }

    @Override
    public boolean func_231046_a_(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_){
        if(super.func_231046_a_(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_))
            return true;
        InputMappings.Input mouseKey = InputMappings.getInputByCode(p_keyPressed_1_, p_keyPressed_2_);
        if(!this.nameField.func_230999_j_() && (p_keyPressed_1_ == 256 || Minecraft.getInstance().gameSettings.keyBindInventory.isActiveAndMatches(mouseKey))){
            Minecraft.getInstance().player.closeScreen();
            return true;
        }
        return false;
    }

}
