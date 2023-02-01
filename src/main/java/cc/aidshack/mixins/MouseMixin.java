package cc.aidshack.mixins;

import cc.aidshack.event.events.EventMouseButton;
import net.minecraft.client.Mouse;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static cc.aidshack.AidsHack.mc;

@Mixin(Mouse.class)
public class MouseMixin {

    @Inject(method = "onMouseButton", at = @At("HEAD"), cancellable = true)
    public void onMouseButton(long window, int button, int action, int mods, CallbackInfo ci) {
        if (window == mc.getWindow().getHandle()) {

            boolean bl = action == GLFW.GLFW_PRESS;
            if (bl) {
                EventMouseButton event = new EventMouseButton(button, mc.player == null ? EventMouseButton.ClickType.IN_MENU : EventMouseButton.ClickType.IN_GAME);
                event.call();
                if (event.isCancelled())
                    ci.cancel();
            }
        }
    }
}
