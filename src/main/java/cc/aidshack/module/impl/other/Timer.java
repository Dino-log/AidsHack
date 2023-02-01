package cc.aidshack.module.impl.other;

import cc.aidshack.module.Module;
import cc.aidshack.module.settings.DecimalSetting;
import cc.aidshack.utils.ColorUtils;
import cc.aidshack.utils.ReflectionHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderTickCounter;

public class Timer extends Module {

	public DecimalSetting speed = new DecimalSetting("Speed", 0.1, 20, 10, 0.1);
	
    public Timer() {
        super("Timer", "Speeds up the game time", false, Category.OTHER);
        addSetting(speed);
    }

    @Override
    public void onTick() {
    	this.setDisplayName("Timer " + ColorUtils.gray + speed.getValue());
        ReflectionHelper.setPrivateValue(RenderTickCounter.class, ReflectionHelper.getPrivateValue(MinecraftClient.class, mc, "renderTickCounter", "field_1728"), 1000.0F / (float) speed.getValue() / 20, "tickTime", "field_1968");
    }

    @Override
    public void onDisable() {
        ReflectionHelper.setPrivateValue(RenderTickCounter.class, ReflectionHelper.getPrivateValue(MinecraftClient.class, mc, "renderTickCounter", "field_1728"), 1000.0F / 20.0F, "tickTime", "field_1968");
    }
}
