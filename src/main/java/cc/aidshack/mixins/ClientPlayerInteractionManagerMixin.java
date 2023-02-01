package cc.aidshack.mixins;

import cc.aidshack.event.events.EventDestroyBlock;
import cc.aidshack.mixinterface.IClientPlayerInteractionManager;
import cc.aidshack.module.ModuleManager;
import cc.aidshack.module.impl.combat.ExtendedReach;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ClientPlayerInteractionManager.class})
public abstract class ClientPlayerInteractionManagerMixin implements IClientPlayerInteractionManager {

    @Shadow private float currentBreakingProgress;
    @Shadow private int blockBreakingCooldown;

    @Shadow protected abstract void syncSelectedSlot();

    @Inject(method = "breakBlock", at = @At("RETURN"))
    public void breakBlock(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        new EventDestroyBlock(pos).call();
    }

    public void setBlockBreakProgress(float progress) {
        this.currentBreakingProgress = progress;
    }

    public void setBlockBreakingCooldown(int cooldown) {
        this.blockBreakingCooldown = cooldown;
    }

    public float getBlockBreakProgress() {
        return this.currentBreakingProgress;
    }

    @Override
    public void syncSelected() {
        syncSelectedSlot();
    }

    @Inject(at = {@At("HEAD")},
            method = {"getReachDistance()F"},
            cancellable = true)
    private void onGetReachDistance(CallbackInfoReturnable<Float> ci)
    {
        if(!ModuleManager.INSTANCE.getModule(ExtendedReach.class).isEnabled())
            return;

        ci.setReturnValue(4.5F);
    }

    @Inject(at = {@At("HEAD")},
            method = {"hasExtendedReach()Z"},
            cancellable = true)
    private void hasExtendedReach(CallbackInfoReturnable<Boolean> cir)
    {
        if(!ModuleManager.INSTANCE.getModule(ExtendedReach.class).isEnabled())
            return;

        cir.setReturnValue(true);
    }
}
