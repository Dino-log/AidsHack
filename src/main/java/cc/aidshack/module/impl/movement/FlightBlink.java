package cc.aidshack.module.impl.movement;

import cc.aidshack.event.EventManager;
import cc.aidshack.event.EventTarget;
import cc.aidshack.event.events.EventSendPacket;
import cc.aidshack.module.Module;
import cc.aidshack.utils.PlayerUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.KeepAliveC2SPacket;

import java.util.ArrayList;

public class FlightBlink extends Module {

    @SuppressWarnings("rawtypes")
    private ArrayList<Packet> packets = new ArrayList<>();
    public static PlayerEntity playerEntity;
    private boolean stopCatching;

    public FlightBlink() {
        super("FlightBlink", "Used for blink setting in flight", false,Category.MOVEMENT);
    }

    @Override
    public void onEnable() {
        stopCatching = false;

        super.onEnable();
    }



    @EventTarget
    private void onSendPacket(EventSendPacket event) {
        if (mc.player == null || (packets.isEmpty() && stopCatching)) {
            packets.clear();
            this.setEnabled(false);
            return;
        }
        if (!stopCatching && !(event.getPacket() instanceof KeepAliveC2SPacket)) {
            if (PlayerUtils.isMoving()) {
                packets.add(event.getPacket());
            }
            event.setCancelled(true);
        }
    }

    @Override
    public void onDisable() {
        stopCatching = true;
//		if (!buffer.isEnabled())
//			packets.forEach(packet -> {
//				mc.getNetworkHandler().sendPacket(packet);
//			});
        packets.clear();
        super.onDisable();
    }

    @Override
    public void toggle() {
        enabled = !enabled;
        if(enabled) {
            onEnable();
            EventManager.INSTANCE.register(this);
        } else {
            onDisable();
            EventManager.INSTANCE.unregister(this);
        }
    }
}

