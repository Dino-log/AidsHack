package cc.aidshack.event.events;

import cc.aidshack.event.Event;

public class EventWalkOffLedge extends Event {

	public boolean isSneaking;

    public EventWalkOffLedge(boolean isSneaking) {
        this.isSneaking = isSneaking;
    }
}
