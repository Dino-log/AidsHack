package cc.aidshack.mixins;

import cc.aidshack.module.ModuleManager;
import cc.aidshack.module.impl.render.NoRender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameOverlayRenderer.class)
public class InGameOverlayRendererMixin {

	@Inject(method = "renderUnderwaterOverlay", at = @At("HEAD"), cancellable = true)
	private static void renderUnderwaterOverlay(MinecraftClient client, MatrixStack matrices, CallbackInfo ci) {
		if (ModuleManager.INSTANCE.getModule(NoRender.class).isEnabled() && ModuleManager.INSTANCE.getModule(NoRender.class).liquid.isEnabled()) ci.cancel();
	}
	
	@Inject(method = "renderFireOverlay", at = @At("HEAD"), cancellable = true)
	private static void renderFireOverlay(MinecraftClient client, MatrixStack matrices, CallbackInfo ci) {
		if (ModuleManager.INSTANCE.getModule(NoRender.class).isEnabled() && ModuleManager.INSTANCE.getModule(NoRender.class).fire.isEnabled()) ci.cancel(); 
	}
}
