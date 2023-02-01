package cc.aidshack.module.impl.other;

import cc.aidshack.event.EventTarget;
import cc.aidshack.event.events.EventSendMessage;
import cc.aidshack.module.Module;

public class AidsHackPostFix extends Module
{
    public AidsHackPostFix()
    {
        super("AidsHackPostFix", "Automatically put aids hack postfix after every chat message", false, Category.OTHER);
    }

    @Override
    public void onEnable()
    {
        super.onEnable();
    }

    @Override
    public void onDisable()
    {
        super.onDisable();
    }

    @EventTarget
    public void onSendMessage(EventSendMessage event)
    {
        if (event.getMessage().startsWith("/") || event.getMessage().startsWith("+") )
            return;
        event.setMessage(event.getMessage() + " | AidsHack on top");
    }
}
