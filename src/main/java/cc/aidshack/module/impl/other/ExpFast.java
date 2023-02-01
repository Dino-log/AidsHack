package cc.aidshack.module.impl.other;

import cc.aidshack.module.Module;
import cc.aidshack.module.settings.BooleanSetting;
import cc.aidshack.module.settings.DecimalSetting;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;

public class ExpFast extends Module   {

	public DecimalSetting delay = new DecimalSetting("Delay", 0, 1, 1, 0.1);
	public BooleanSetting onRightClick = new BooleanSetting("On Right Click", false);

	public BooleanSetting swap = new BooleanSetting("Switch Item", false);

	public ExpFast() {
		super("FastExp", "niggas", false,Category.OTHER);
		addSettings(swap, onRightClick);
	}


	@Override
	public void onEnable() {
		super.onEnable();
	}

	@Override
	public void onDisable() {
		super.onDisable();
	}

	@Override
    public void onTick() {
		if (!(onRightClick.isEnabled())) {
			if (mc.player.getMainHandStack().getItem() == Items.EXPERIENCE_BOTTLE) {
				mc.interactionManager.interactItem(mc.player, mc.world, Hand.MAIN_HAND);
			} else if (mc.player.getMainHandStack().getItem() != Items.EXPERIENCE_BOTTLE && swap.isEnabled()) {
				int xpSlot = mc.player.getInventory().getSlotWithStack(Items.EXPERIENCE_BOTTLE.getDefaultStack());
				if (xpSlot != -1) mc.player.getInventory().selectedSlot = xpSlot;
			}
		} else if (onRightClick.isEnabled()) {
			if (mc.player.getMainHandStack().getItem() == Items.EXPERIENCE_BOTTLE && mc.options.useKey.isPressed()) {
				mc.interactionManager.interactItem(mc.player, mc.world, Hand.MAIN_HAND);
			} else if (mc.player.getMainHandStack().getItem() != Items.EXPERIENCE_BOTTLE && swap.isEnabled()) {
				int xpSlot = mc.player.getInventory().getSlotWithStack(Items.EXPERIENCE_BOTTLE.getDefaultStack());
				if (xpSlot != -1) mc.player.getInventory().selectedSlot = xpSlot;
			}
		}

	}

}
