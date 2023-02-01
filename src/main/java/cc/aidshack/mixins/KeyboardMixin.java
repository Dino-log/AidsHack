package cc.aidshack.mixins;

import cc.aidshack.command.CommandManager;
import cc.aidshack.event.events.EventKeyPress;
import cc.aidshack.module.Module;
import cc.aidshack.module.ModuleManager;
import cc.aidshack.utils.KeyUtils;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public abstract class KeyboardMixin {
    @Shadow @Final private MinecraftClient client;

    @Inject(method = "onKey", at = @At("HEAD"), cancellable = true)
    public void onKey(long window, int key, int scancode, int action, int modifiers, CallbackInfo info) {
        if (key != GLFW.GLFW_KEY_UNKNOWN) {
            KeyUtils.setKeyState(key, action != GLFW.GLFW_RELEASE);
            for (Module mod : ModuleManager.INSTANCE.modules) {
                if (mod.getKey() == key && action == GLFW.GLFW_PRESS && MinecraftClient.getInstance().currentScreen == null)
                    mod.toggle();
            }
            EventKeyPress event = new EventKeyPress(key, scancode, action);
            event.call();
            if (event.isCancelled()) info.cancel();
            if (client.currentScreen == null && key == KeyUtils.getKey(CommandManager.get().getPrefix()) && action == GLFW.GLFW_PRESS) client.setScreen(new ChatScreen(""));
        }
    }
}
