package cc.aidshack.event.events;

import cc.aidshack.event.Event;
import net.minecraft.text.Text;

public class EventReceiveChat extends Event {

	private Text message;
	private int id;
	
	public EventReceiveChat(Text text, int id) {
		this.message = text;
		this.id = id;
	}
	
	public Text getMessage() {
		return message;
	}
	
	public int getId() {
		return id;
	}
	
	public String getMessageAsString() {
		return message.getString();
	}
}
