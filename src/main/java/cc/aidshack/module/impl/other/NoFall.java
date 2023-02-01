package cc.aidshack.module.impl.other;

import cc.aidshack.event.EventTarget;
import cc.aidshack.event.events.EventSendPacket;
import cc.aidshack.mixins.PlayerMoveC2SPacketAccessor;
import cc.aidshack.module.Module;
import cc.aidshack.module.settings.ModeSetting;
import cc.aidshack.utils.ColorUtils;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.RaycastContext;

public class NoFall extends Module {

	public ModeSetting mode = new ModeSetting("Mode", "Packet", "Packet");
	
    public NoFall() {
        super("NoFall", "Prevents you from taking fall damage", false, Category.OTHER);
        addSetting(mode);
    }

    @Override
    public void onTick() {
    	this.setDisplayName("NoFall " + ColorUtils.gray + mode.getMode());
        super.onTick();
    }
    
    @EventTarget
    private void onSendPacket(EventSendPacket event) {
    	if (mc.player == null) return;
        if (mc.player.getAbilities().creativeMode
            || !(event.getPacket() instanceof PlayerMoveC2SPacket) || mc.player.isOnGround() || mc.player.fallDistance < 2.5) return;


        if ((mc.player.isFallFlying()) && mc.player.getVelocity().y < 1) {
            BlockHitResult result = mc.world.raycast(new RaycastContext(
                mc.player.getPos(),
                mc.player.getPos().subtract(0, 0.5, 0),
                RaycastContext.ShapeType.OUTLINE,
                RaycastContext.FluidHandling.NONE,
                mc.player)
            );

            if (result != null && result.getType() == HitResult.Type.BLOCK) {
                ((PlayerMoveC2SPacketAccessor) event.getPacket()).setOnGround(true);
            }
        }
        else {
            ((PlayerMoveC2SPacketAccessor) event.getPacket()).setOnGround(true);
        }
    }
}
