package cc.aidshack.module.impl.movement;

import cc.aidshack.event.EventTarget;
import cc.aidshack.event.events.EventMotionUpdate;
import cc.aidshack.module.Module;
import cc.aidshack.module.impl.combat.Killaura;
import cc.aidshack.module.settings.ModeSetting;
import cc.aidshack.utils.ColorUtils;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class NoSlow extends Module {

    public ModeSetting mode = new ModeSetting("Mode", "Vanilla", "Vanilla", "NCP");
    boolean blocking = false;

    public NoSlow() {
        super("NoSlow", "Prevents items from slowing you down", false,Category.MOVEMENT);
        addSettings(mode);
    }



    @EventTarget
    public void onMotionUpdate(EventMotionUpdate event) {
        if (mc.player.isOnGround()) {
            if (event.isPre()) {
                if (mc.player.isBlocking() && mode.isMode("NCP") && Killaura.target == null) {
                    mc.player.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, Direction.DOWN));
                    blocking = false;
                }
            } else {
                if (mc.player.isBlocking() && mode.isMode("NCP") && Killaura.target == null && !blocking) {
                    mc.player.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, new BlockHitResult(new Vec3d(0.0f, 0.0f, 0.0f), Direction.DOWN, new BlockPos(-1, -1, -1), false)));
                    mc.interactionManager.interactItem(mc.player, mc.world, Hand.OFF_HAND);
                    mc.interactionManager.interactItem(mc.player, mc.world, Hand.MAIN_HAND);
                    blocking = true;
                }
            }
        }
        this.setDisplayName("NoSlow " + ColorUtils.gray + mode.getMode());
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}
