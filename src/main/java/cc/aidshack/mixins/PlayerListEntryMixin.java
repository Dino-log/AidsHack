package cc.aidshack.mixins;

import net.minecraft.client.network.PlayerListEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerListEntry.class)
public class PlayerListEntryMixin {


	@Inject(method = "setLatency", at = @At("HEAD"), cancellable = true)
	protected void onSetLatency(int latency, CallbackInfo ci) {

	}
	
	@Inject(method = "getLatency", at = @At("RETURN"), cancellable = true)
	public void onGetLatency(CallbackInfoReturnable<Integer> cir) {

	}
}
