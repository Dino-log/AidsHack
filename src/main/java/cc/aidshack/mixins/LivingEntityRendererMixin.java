package cc.aidshack.mixins;

import cc.aidshack.module.ModuleManager;
import cc.aidshack.module.impl.render.Freecam;
import cc.aidshack.module.impl.render.UpsideDownPlayers;
import cc.aidshack.utils.RotationUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

import static cc.aidshack.AidsHack.mc;


@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin
{
    @Redirect(method = "setupTransforms", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;shouldFlipUpsideDown(Lnet/minecraft/entity/LivingEntity;)Z"))
    public boolean forceFlipUpsideDownTransForms(LivingEntity entity) {
        if (ModuleManager.INSTANCE.getModule(UpsideDownPlayers.class).isEnabled()) {
            return true;
        }
        if (entity instanceof PlayerEntity || entity.hasCustomName()) {
            String string = Formatting.strip(entity.getName().getString());
            if ("Dinnerbone".equals(string) || "Grumm".equals(string)) {
                return !(entity instanceof PlayerEntity) || ((PlayerEntity) entity).isPartVisible(PlayerModelPart.CAPE);
            }
        }
        return false;
    }

    @Redirect(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;shouldFlipUpsideDown(Lnet/minecraft/entity/LivingEntity;)Z"))
    public boolean forceFlipUpsideDown(LivingEntity entity) {
        if (ModuleManager.INSTANCE.getModule(UpsideDownPlayers.class).isEnabled()) {
            return true;
        }
        if (entity instanceof PlayerEntity || entity.hasCustomName()) {
            String string = Formatting.strip(entity.getName().getString());
            if ("Dinnerbone".equals(string) || "Grumm".equals(string)) {
                return !(entity instanceof PlayerEntity) || ((PlayerEntity) entity).isPartVisible(PlayerModelPart.CAPE);
            }
        }
        return false;
    }


    @ModifyVariable(method = "render", ordinal = 5, at = @At(value = "STORE", ordinal = 3))
    public float changePitch(float oldValue, LivingEntity entity) {
        if (entity.equals(mc.player) && RotationUtils.isCustomPitch) return RotationUtils.serverPitch;
        return oldValue;
    }

    @ModifyVariable(method = "render", ordinal = 2, at = @At(value = "STORE", ordinal = 0))
    public float changeYaw(float oldValue, LivingEntity entity) {
        if (entity.equals(mc.player) && RotationUtils.isCustomYaw) return RotationUtils.serverYaw;
        return oldValue;
    }

    @ModifyVariable(method = "render", ordinal = 3, at = @At(value = "STORE", ordinal = 0))
    public float changeHeadYaw(float oldValue, LivingEntity entity) {
        if (entity.equals(mc.player) && RotationUtils.isCustomYaw) return RotationUtils.serverYaw;
        return oldValue;
    }

    @Redirect(method = "hasLabel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;getCameraEntity()Lnet/minecraft/entity/Entity;"))
    private Entity hasLabelGetCameraEntityProxy(MinecraftClient mc) {
        if (ModuleManager.INSTANCE.getModule(Freecam.class).isEnabled()) return null;
        return mc.getCameraEntity();
    }
}
