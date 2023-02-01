package cc.aidshack.mixins;


import cc.aidshack.module.ModuleManager;
import cc.aidshack.module.impl.render.Cape;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(net.minecraft.client.network.AbstractClientPlayerEntity.class)
public class AbstractClientPlayerEntityMixin {
    @Inject(method = "getCapeTexture", at = @At("HEAD"), cancellable = true)
    private void onGetCapeTexture(CallbackInfoReturnable<Identifier> info){
        Identifier id = ModuleManager.INSTANCE.getModule(Cape.class).getTexture(((PlayerEntity) (Object) this));
        if (id != null) info.setReturnValue(id);
    }
}
