package cc.aidshack.module.impl.movement;

import cc.aidshack.module.Module;
import cc.aidshack.module.settings.BooleanSetting;
import cc.aidshack.utils.KeyUtils;
import net.minecraft.client.gui.screen.ChatScreen;

public class InvMove extends Module {

	public BooleanSetting space = new BooleanSetting("Jump", false);
	public BooleanSetting shift = new BooleanSetting("Sneak", false);

	public InvMove() {
        super("InvMove", "Allows you to move inside of you inventory", false, Category.MOVEMENT);
        addSettings(space, shift);
    }

    @Override
    public void onTick() {
    	if (mc.currentScreen != null && !(mc.currentScreen instanceof ChatScreen)) {
    		mc.options.forwardKey.setPressed(KeyUtils.isPressed(mc.options.forwardKey));
            mc.options.backKey.setPressed(KeyUtils.isPressed(mc.options.backKey));
            mc.options.leftKey.setPressed(KeyUtils.isPressed(mc.options.leftKey));
            mc.options.rightKey.setPressed(KeyUtils.isPressed(mc.options.rightKey));
            mc.options.attackKey.setPressed(KeyUtils.isPressed(mc.options.attackKey));
            mc.options.useKey.setPressed(KeyUtils.isPressed(mc.options.useKey));
            if (space.isEnabled()) mc.options.jumpKey.setPressed(KeyUtils.isPressed(mc.options.jumpKey));
            if (shift.isEnabled()) mc.options.sneakKey.setPressed(KeyUtils.isPressed(mc.options.sneakKey));
    	}
    	super.onTick();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}
