package cc.aidshack.module.impl.hud;

import cc.aidshack.AidsHack;
import cc.aidshack.event.EventTarget;
import cc.aidshack.event.events.EventRenderHUD;
import cc.aidshack.module.Module;
import cc.aidshack.module.ModuleManager;
import cc.aidshack.module.impl.other.CustomFont;
import cc.aidshack.module.settings.BooleanSetting;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.lwjgl.opengl.GL11;

import java.awt.*;

import static cc.aidshack.AidsHack.MC;
import static cc.aidshack.gui.frames.ModuleButton.font;

public class Hud extends Module   {



    private final Identifier[] tacos = {new Identifier("aidshack", "dancingtaco1.png"), new Identifier("aidshack", "dancingtaco2.png"), new Identifier("aidshack", "dancingtaco3.png"), new Identifier("aidshack", "dancingtaco4.png")};
    private static final Identifier logoImage = new Identifier("aidshack", "aidshack.png");
    public static String customText= "change this by doing /aidshack:customhudtext <Your Custom Meessage>";
    private int ticks;
    public BooleanSetting version = new BooleanSetting("Version Text", false);
    public BooleanSetting taco = new BooleanSetting("Taco", false);
    public BooleanSetting logo = new BooleanSetting("Logo", false);
    public BooleanSetting arrayList = new BooleanSetting("Array List", false);

    public Hud() {
        super("Hud", "Hud",false, Category.HUD);
        addSettings(version, taco, logo, arrayList);
    }

    @Override
    public void onEnable() {
        super.onEnable();


    }

    @Override
    public void onDisable() {
        super.onDisable();
    }



    @Override
    public void onTick() {
        if (taco.isEnabled()){
            if (ticks >= 31)
                ticks = 0;
            else
                ticks++;
        }
    }

    @EventTarget
    public void onRenderHud(EventRenderHUD event) {
        MatrixStack matrices = event.getMatrices();
        if (version.isEnabled()){
            matrices.push();
            matrices.translate(10, 60, 0);
            if (ModuleManager.INSTANCE.getModule(CustomFont.class).isEnabled()){
                font.drawWithShadow(matrices, AidsHack.FULL_MOD_NAME, 0, 0, Color.red.darker().getRGB());
            }else {
                MC.textRenderer.drawWithShadow(matrices, AidsHack.FULL_MOD_NAME, 0, 0, Color.red.darker().getRGB());
            }
            matrices.pop();
        }
        if (taco.isEnabled()){
            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            matrices.push();
            matrices.translate(28, 10, 0);
            matrices.scale(0.7f, 0.7f, 1);
            RenderSystem.setShaderColor(1, 1, 1, 1);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, tacos[ticks / 8]);
            DrawableHelper.drawTexture(matrices, 0, 3, 0, 0, 128, 64, 128, 64);
            matrices.pop();
            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glDisable(GL11.GL_BLEND);
        }

        if (logo.isEnabled()){
            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            matrices.push();
            matrices.translate(28, 10, 0);
            matrices.scale(0.5f, 0.5f, 1);
            RenderSystem.setShaderColor(1, 1, 1, 1);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, logoImage);
            DrawableHelper.drawTexture(matrices, 0, 3, 0, 0, 81, 81, 81, 81);
            matrices.pop();
            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glDisable(GL11.GL_BLEND);
        }
    }
    public static String sdasd = "sercon";

    public static int setCustomHudText(String newReplacementMessage) {
        customText = newReplacementMessage;
        return 0;
    }

}
