package cc.aidshack.module.impl.other;

import cc.aidshack.event.EventTarget;
import cc.aidshack.event.events.EventReceivePacket;
import cc.aidshack.module.Module;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;

import static cc.aidshack.AidsHack.MC;

public class AutoGG extends Module
{
	public static String replacementMessage= "GG!";

	public AutoGG()
	{
		super("AutoGG", "Automatically sends an excuse in game chat when you die", false, Category.OTHER);
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
	public void onReceivePacket(EventReceivePacket event)
	{
		if (!(event.getPacket() instanceof EntityStatusS2CPacket))
			return;

		EntityStatusS2CPacket packet = (EntityStatusS2CPacket) event.getPacket();

		if (MC.world == null)
			return;

		if (packet.getEntity(mc.player.world) != MC.player)
			return;

		if (packet.getStatus() != 3)
			return;

		MC.player.sendChatMessage(replacementMessage);
	}

	public static int setReplacementMessage(String newReplacementMessage) {
		replacementMessage = newReplacementMessage;
		return 0;
	}
}
