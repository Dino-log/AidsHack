package cc.aidshack.event.events;

import cc.aidshack.event.Event;
import net.minecraft.network.Packet;

public class EventReceivePacket extends Event {

	private Packet<?> packet;
	
	public EventReceivePacket(Packet<?> packet) {
		this.packet = packet;
	}
	
	public Packet<?> getPacket() {
		return packet;
	}
}
