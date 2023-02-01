package cc.aidshack.module.impl.render;

import cc.aidshack.module.Module;
import cc.aidshack.module.settings.DecimalSetting;

import static cc.aidshack.AidsHack.MC;

public class CustomBrightness extends Module
{

	public DecimalSetting brightness = new DecimalSetting("Bright Amount", 0, 100, 0, 1);
	private double prevGamma;

	public CustomBrightness()
	{
		super("CustomBright", "override gamma value", false, Category.RENDER);
		addSetting(brightness);
	}

	@Override
	public void onEnable()
	{
		super.onEnable();
		prevGamma = MC.options.gamma;
	}

	@Override
	public void onDisable()
	{
		super.onDisable();
		MC.options.gamma = prevGamma;
	}

	@Override
	public void onTick() {
		MC.options.gamma = brightness.getValue();
		super.onTick();
	}
}
