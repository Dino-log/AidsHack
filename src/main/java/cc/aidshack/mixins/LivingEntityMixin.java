package cc.aidshack.mixins;

import cc.aidshack.module.ModuleManager;
import cc.aidshack.module.impl.other.OffhandCrash;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static cc.aidshack.AidsHack.mc;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "onEquipStack", at = @At("HEAD"), cancellable = true)
    private void onEquipStack(ItemStack stack, CallbackInfo info) {
        if ((Object) this == mc.player && ModuleManager.INSTANCE.getModule(OffhandCrash.class).antiCrash.isEnabled() && ModuleManager.INSTANCE.getModule(OffhandCrash.class).isEnabled()) {
            info.cancel();
        }
    }
}
