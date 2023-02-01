package cc.aidshack.mixins;

import cc.aidshack.event.events.EventEntityRender;
import cc.aidshack.event.events.EventRenderNametags;
import cc.aidshack.module.ModuleManager;
import cc.aidshack.module.impl.render.NameTagPing;
import cc.aidshack.utils.PlayerUtils;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.math.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin<T extends Entity> {


    @Redirect(method = "renderLabelIfPresent", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/text/Text;FFIZLnet/minecraft/util/math/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;ZII)I"))
    private int shouldDisplayPingInName(TextRenderer instance, Text text, float x, float y, int color, boolean shadow, Matrix4f matrix, VertexConsumerProvider vertexConsumers, boolean seeThrough, int backgroundColor, int light, Entity entity) {
        if (entity.isPlayer() && ModuleManager.INSTANCE.getModule(NameTagPing.class).isEnabled()) {
            text = new LiteralText(text.getString() + " (" + PlayerUtils.getPing(entity) + "ms)");
            x -= 15;
        }
        instance.draw(text, x, (float) y, color, false, matrix, vertexConsumers, seeThrough, backgroundColor, light);
        return color;
    }

    @Inject(method = "renderLabelIfPresent", at = @At("HEAD"), cancellable = true)
    public void renderLabel(Entity entity, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (entity instanceof LivingEntity) {
            EventRenderNametags eventRenderNametags = new EventRenderNametags((LivingEntity) entity, matrices, vertexConsumers);
            eventRenderNametags.call();
            if (eventRenderNametags.isCancelled())
                ci.cancel();
        }
    }

    @Inject(method = "renderLabelIfPresent", at = @At("HEAD"), cancellable = true)
    public void renderLabelIfPresent(T entity, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        EventEntityRender.Single.Label event = new EventEntityRender.Single.Label(entity, matrices, vertexConsumers);
        event.call();

        if (event.isCancelled()) {
            ci.cancel();
        }
    }

}
