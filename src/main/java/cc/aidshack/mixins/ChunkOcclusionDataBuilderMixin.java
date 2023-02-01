package cc.aidshack.mixins;

import cc.aidshack.event.events.EventMarkChunkClosed;
import net.minecraft.client.render.chunk.ChunkOcclusionDataBuilder;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkOcclusionDataBuilder.class)
public class ChunkOcclusionDataBuilderMixin {

	@Inject(method = "markClosed", at = @At("HEAD"), cancellable = true)
	public void markClosed(BlockPos blockPos, CallbackInfo ci) {
		try {
			EventMarkChunkClosed eventMarkChunkClosed = new EventMarkChunkClosed();
			eventMarkChunkClosed.call();
			if (eventMarkChunkClosed.isCancelled())
				ci.cancel();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
