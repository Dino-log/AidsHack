package cc.aidshack.mixins;

import cc.aidshack.AidsHack;
import cc.aidshack.AidsHackClient;
import cc.aidshack.core.BlockIterator;
import cc.aidshack.core.CrystalDataTracker;
import cc.aidshack.event.events.EventItemUse;
import cc.aidshack.event.events.EventTick;
import cc.aidshack.module.ModuleManager;
import cc.aidshack.module.impl.combat.MarlowCrystalOptimizer;
import cc.aidshack.module.impl.other.ChangeWindowName;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;
import java.util.Map;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Shadow @Final
    private RenderTickCounter renderTickCounter;

    @Shadow
    public ClientWorld world;

    @Inject(at = @At("HEAD"), method = "tick")
    private void onPreTick(CallbackInfo info) {
        BlockIterator.INSTANCE.onTick();
        CrystalDataTracker.INSTANCE.onTick();

        EventTick event = new EventTick();
        event.call();
    }

    @Inject(at = @At("TAIL"), method = "scheduleStop")
    public void onShutdown(CallbackInfo ci) {
        AidsHack.CLIENT.shutdown();
    }

    @Inject(at = @At("TAIL"), method = "updateWindowTitle()V")
    public void updateWindowTitle(CallbackInfo info) {
        if (ModuleManager.INSTANCE.getModule(ChangeWindowName.class).isEnabled())MinecraftClient.getInstance().getWindow().setTitle(ChangeWindowName.windowName);
    }


    @Inject(method = {"tick"}, at = {@At("HEAD")})
    private void onPreTick1(CallbackInfo info) {
        if (!ModuleManager.INSTANCE.getModule(MarlowCrystalOptimizer.class).isEnabled())
        {return;}

            for (Iterator<Map.Entry<Entity, Integer>> iterator = AidsHackClient.toKill.entrySet().iterator(); iterator.hasNext(); ) {
                Map.Entry<Entity, Integer> entry = iterator.next();
                Entity entity = entry.getKey();
                int delay = ((Integer)entry.getValue()).intValue() - 1;
                if (delay == 0) {
                    iterator.remove();
                    if (!entity.isAlive()) {
                        entity.kill();
                        entity.setRemoved(Entity.RemovalReason.KILLED);
                        entity.onRemoved();
                    }
                    continue;
                }
                entry.setValue(Integer.valueOf(delay));
            }
        }



    @Inject(at = @At("HEAD"), method = "doItemUse", cancellable = true)
    private void onDoItemUse(CallbackInfo ci) {
        EventItemUse event = new EventItemUse();
        event.call();
        if (event.isCancelled())
            ci.cancel();
    }
}
