package cc.aidshack.module.impl.other;

import cc.aidshack.event.EventTarget;
import cc.aidshack.event.events.EventReceivePacket;
import cc.aidshack.module.Module;
import cc.aidshack.module.settings.BooleanSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;

import static cc.aidshack.AidsHack.MC;

public class AutoEZ extends Module
{
    public static String killMessage= "ez kill";


    private final BooleanSetting killMessageOnOff = new BooleanSetting("Kill Message", false);

    public AutoEZ()
    {
        super("AutoEZ", "Automatically put a specific line of text in chat after someone pops or dies", false, Category.OTHER);
    }

    @Override
    public void onEnable()
    {
        super.onDisable();
    }

    @Override
    public void onDisable()
    {
        super.onEnable();
    }

    @EventTarget
    public void onReceivePacket(EventReceivePacket event)
    {
        if (!(event.getPacket() instanceof EntityStatusS2CPacket))
            return;

        EntityStatusS2CPacket packet = (EntityStatusS2CPacket) event.getPacket();

        if (MC.world == null)
            return;

        Entity entity = packet.getEntity(MC.world);
        if (!(entity instanceof PlayerEntity))
            return;
        if (entity == MC.player)
            return;
        PlayerEntity player = (PlayerEntity) entity;
        if (packet.getStatus() == 3 && killMessageOnOff.isEnabled())
        {
            MC.player.sendChatMessage(killMessage);
        }
    }
    public static int setKillMessage(String newReplacementMessage) {
        killMessage = newReplacementMessage;
        return 0;
    }
}
