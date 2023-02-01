package cc.aidshack.mixins;

import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin {
	


	@Inject(method = "addPlayer", at = @At("HEAD"))
	private void addPlayer(ServerPlayerEntity player, CallbackInfo ci) {

	}
	
	@Inject(method = "removePlayer", at = @At("HEAD"))
	private void removePlayer(ServerPlayerEntity player, Entity.RemovalReason reason, CallbackInfo ci) {

	}
}
