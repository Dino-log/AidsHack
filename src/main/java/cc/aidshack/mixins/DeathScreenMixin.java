package cc.aidshack.mixins;

import cc.aidshack.module.ModuleManager;
import cc.aidshack.module.impl.combat.AutoGhost;
import cc.aidshack.utils.TotemUtils;
import net.minecraft.client.gui.screen.DeathScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DeathScreen.class)
public class DeathScreenMixin {
    @Inject(at = @At("HEAD"), method = "init()V")
    private void init(CallbackInfo info) {
        if (!ModuleManager.INSTANCE.getModule(AutoGhost.class).isEnabled()) {
            return;
        }


        if (ModuleManager.INSTANCE.getModule(AutoGhost.class).mode.isMode("MainHand")){
            TotemUtils.putTotemInMainHand();
        }else if (ModuleManager.INSTANCE.getModule(AutoGhost.class).mode.isMode("Offhand")){
            TotemUtils.putTotemInOffHand();
        }else if (ModuleManager.INSTANCE.getModule(AutoGhost.class).mode.isMode("Both")){
            TotemUtils.putTotemInBoth();
        }
    }
}
