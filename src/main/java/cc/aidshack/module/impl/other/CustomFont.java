package cc.aidshack.module.impl.other;

import cc.aidshack.module.Module;
import cc.aidshack.utils.font.FontManager;

public class CustomFont extends Module {

	public CustomFont() {
		super("CustomFont", "gamer font", false, Category.RENDER);
	}

	@Override
	public void onEnable() {
		FontManager.setMcFont(false);
		super.onEnable();
	}
	
	@Override
	public void onDisable() {
		FontManager.setMcFont(true);
		super.onDisable();
	}

}
