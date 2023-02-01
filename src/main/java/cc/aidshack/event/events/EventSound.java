package cc.aidshack.event.events;

import cc.aidshack.event.Event;
import net.minecraft.client.sound.SoundInstance;

public class EventSound extends Event {

    public SoundInstance sound;

    public EventSound(SoundInstance sound) {
    	this.sound = sound;
    }
}
