package cc.aidshack.event.events;

import cc.aidshack.event.Event;

public class EventUpdate extends Event {

    private Event.State state;

    public EventUpdate(Event.State state){
        this.state = state;
    }

}
