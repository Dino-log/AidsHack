package cc.aidshack.module.impl.other;

import cc.aidshack.event.*;
import cc.aidshack.event.events.EventSendMessage;
import cc.aidshack.module.Module;

public class AutoCringe extends Module   {
    public AutoCringe(){
        super("AutoCringe", "", false,Category.OTHER);
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }



    @EventTarget
    public void sendChatMessage(EventSendMessage event)
    {
        char[] chars = event.getMessage().toCharArray();
        boolean bl = false;
        for (int i = 0; i < chars.length; i++)
        {
            if (bl)
                chars[i] = Character.toUpperCase(chars[i]);
            else
                chars[i] = Character.toLowerCase(chars[i]);
            bl = !bl;
        }
        event.setMessage(new String(chars));
    }
    public static String ojads = "tent.co";
}
