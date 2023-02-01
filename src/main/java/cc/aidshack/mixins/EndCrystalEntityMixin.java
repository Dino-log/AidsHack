package cc.aidshack.mixins;

import cc.aidshack.module.ModuleManager;
import cc.aidshack.module.impl.combat.CrystalPingBypass;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.EndCrystalEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EndCrystalEntity.class)
public abstract class EndCrystalEntityMixin extends Entity {


    private EndCrystalEntityMixin() {
        super(null, null);
        throw new AssertionError("Thanks to HCsCR");
    }

    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    public void hcscrModHurt(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir){
        if (!ModuleManager.INSTANCE.getModule(CrystalPingBypass.class).isEnabled() && !ModuleManager.INSTANCE.getModule(CrystalPingBypass.class).explodesClientSide((EndCrystalEntity) (Object) this, source, amount)){
            return;
        }
        remove(RemovalReason.KILLED);
        cir.setReturnValue(true);
    }
}
