package cc.aidshack.mixins;

import cc.aidshack.event.events.EventCollide;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static cc.aidshack.AidsHack.mc;

@Mixin(Entity.class)
public class EntityMixin {

    @Inject(method = "pushAwayFrom", at = @At("HEAD"), cancellable = true)
    public void onPushAwayFrom(Entity entity, CallbackInfo ci) {
        if (entity == mc.player) {
            EventCollide.Entity event = new EventCollide.Entity();
            event.call();
            if (event.isCancelled()) ci.cancel();
        }
    }
}