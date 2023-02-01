package cc.aidshack.mixins;

import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RenderTickCounter.class)
public interface RenderTickCounterAccessor {

	@Mutable 
	@Accessor("tickTime")
	public float getTickTime();
	
	@Mutable 
	@Accessor("tickTime")
	public void setTickTime(float v);
	
	@Mutable 
	@Accessor("lastFrameDuration")
	public float getLastFrameDuration();
	
	@Mutable 
	@Accessor("prevTimeMillis")
	public long getPrevTimeMillis();
}
