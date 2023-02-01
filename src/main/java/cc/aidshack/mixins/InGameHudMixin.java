package cc.aidshack.mixins;

import cc.aidshack.event.events.EventRenderGUI;
import cc.aidshack.event.events.EventRenderHUD;
import cc.aidshack.gui.hud.Hud;
import cc.aidshack.module.ModuleManager;
import cc.aidshack.module.impl.render.NoRender;
import cc.aidshack.utils.math.TimeHelper;
import net.minecraft.client.gui.hud.BossBarHud;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static cc.aidshack.AidsHack.MC;
import static cc.aidshack.AidsHack.mc;


@Mixin(InGameHud.class)
public class InGameHudMixin {

    @Shadow @Final private BossBarHud bossBarHud;
    @Shadow private int scaledWidth;
    @Shadow private int scaledHeight;
    private float beginTime = TimeHelper.getTime();

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void onRenderHead (MatrixStack matrices, float tickDelta, CallbackInfo info) {
        float beginTime = TimeHelper.getTime();
        this.beginTime = beginTime;
    }

    @Inject(
            at = {@At(value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/systems/RenderSystem;enableBlend()V",
                    ordinal = 4)},
            method = {"render(Lnet/minecraft/client/util/math/MatrixStack;F)V"})
    public void onRender1(MatrixStack matrixStack, float partialTicks,
                           CallbackInfo ci) {
        if (MC.options.debugEnabled)
            return;

        boolean blend = GL11.glGetBoolean(GL11.GL_BLEND);

        EventRenderHUD event = new EventRenderHUD(matrixStack, partialTicks);
        event.call();

        if (blend)
            GL11.glEnable(GL11.GL_BLEND);
        else
            GL11.glDisable(GL11.GL_BLEND);
    }

    @Inject(method = "render", at = @At("RETURN"), cancellable = true)
    public void onRender (MatrixStack matrices, float tickDelta, CallbackInfo info) {
        Hud.renderArrayList(matrices);
        float endTime = TimeHelper.getTime();
        TimeHelper.setDeltaTime(this.beginTime - endTime);
        this.beginTime = endTime;
        EventRenderGUI event = new EventRenderGUI(matrices, tickDelta);
        event.call();
    }

    @Inject(method = "renderOverlay", at = @At("HEAD"), cancellable = true)
    public void renderOverlay(Identifier texture, float opacity, CallbackInfo ci) {
        if (texture.equals(new Identifier("textures/misc/pumpkinblur.png")) && ModuleManager.INSTANCE.getModule(NoRender.class).isEnabled() && ModuleManager.INSTANCE.getModule(NoRender.class).pumpkin.isEnabled()) ci.cancel();
    }

    @Inject(method = "renderPortalOverlay", at = @At("HEAD"), cancellable = true)
    public void renderPortalOverlay(float nauseaStrength, CallbackInfo ci) {
        if (ModuleManager.INSTANCE.getModule(NoRender.class).isEnabled() && ModuleManager.INSTANCE.getModule(NoRender.class).portal.isEnabled()) {
            mc.player.lastNauseaStrength = -1;
            mc.player.nextNauseaStrength = -1;
            ci.cancel();
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void onTick(CallbackInfo ci) {
        EventRenderGUI.Tick event = new EventRenderGUI.Tick();
        event.call();
    }
}