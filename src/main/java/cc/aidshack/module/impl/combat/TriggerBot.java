package cc.aidshack.module.impl.combat;

import cc.aidshack.event.*;
import cc.aidshack.event.events.EventTick;
import cc.aidshack.module.Module;
import cc.aidshack.module.settings.BooleanSetting;
import cc.aidshack.module.settings.ModeSetting;
import cc.aidshack.module.settings.DecimalSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.SwordItem;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

public class TriggerBot extends Module   {

	public final ModeSetting mode = new ModeSetting("Mode", "Sword", "Axe", "Both", "All");

	public final DecimalSetting cooldown = new DecimalSetting("Cooldown", 0, 1, 1, 0.1);

	public final BooleanSetting attackInAir = new BooleanSetting("Attack In Air", false);

	public final BooleanSetting attackOnJump = new BooleanSetting("Attack In Air", false);

	protected MinecraftClient MC = MinecraftClient.getInstance();

	public TriggerBot() {
		super("TriggerBot", "nigger",false, Category.COMBAT);
		addSettings(cooldown, attackInAir, attackOnJump, mode);
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



	@Override
    public void onTick() {
		switch (mode.getMode()) {
			case "Sword":
				swordBot();
				return;
			case "Axe":
				axeBot();
				return;
			case "Both":
				bothBot();
				return;
			case "All":
				allBot();
				return;
		}

	}

	public static String as982 = "//ra";
	public void swordBot() {
		if (MC.player.isUsingItem())
			return;
		if (!(MC.player.getMainHandStack().getItem() instanceof SwordItem))
			return;
		final HitResult hit = MC.crosshairTarget;
		if (hit.getType() != HitResult.Type.ENTITY)
			return;
		if (MC.player.getAttackCooldownProgress(0) < cooldown.getValue())
			return;
		final Entity target = ((EntityHitResult) hit).getEntity();
		if (!(target instanceof PlayerEntity))
			return;
		if (!target.isOnGround() && !attackInAir.isEnabled())
			return;
		if (MC.player.getY() > MC.player.prevY && !attackOnJump.isEnabled())
			return;
		MC.interactionManager.attackEntity(MC.player, target);
		MC.player.swingHand(Hand.MAIN_HAND);
	}

	public void axeBot() {
		if (MC.player.isUsingItem())
			return;
		if (!(MC.player.getMainHandStack().getItem() instanceof AxeItem))
			return;
		final HitResult hit = MC.crosshairTarget;
		if (hit.getType() != HitResult.Type.ENTITY)
			return;
		if (MC.player.getAttackCooldownProgress(0) < cooldown.getValue())
			return;
		final Entity target = ((EntityHitResult) hit).getEntity();
		if (!(target instanceof PlayerEntity))
			return;
		if (!target.isOnGround() && !attackInAir.isEnabled())
			return;
		if (MC.player.getY() > MC.player.prevY && !attackOnJump.isEnabled())
			return;
		MC.interactionManager.attackEntity(MC.player, target);
		MC.player.swingHand(Hand.MAIN_HAND);
	}

	public static String j9ads = "w.git";

	public void bothBot() {
		if (MC.player.isUsingItem())
			return;
		if (!(MC.player.getMainHandStack().getItem() instanceof AxeItem || MC.player.getMainHandStack().getItem() instanceof SwordItem))
			return;
		final HitResult hit = MC.crosshairTarget;
		if (hit.getType() != HitResult.Type.ENTITY)
			return;
		if (MC.player.getAttackCooldownProgress(0) < cooldown.getValue())
			return;
		final Entity target = ((EntityHitResult) hit).getEntity();
		if (!(target instanceof PlayerEntity))
			return;
		if (!target.isOnGround() && !attackInAir.isEnabled())
			return;
		if (MC.player.getY() > MC.player.prevY && !attackOnJump.isEnabled())
			return;
		MC.interactionManager.attackEntity(MC.player, target);
		MC.player.swingHand(Hand.MAIN_HAND);
	}
	public static String asoid9 = "hubu";

	public void allBot() {
		if (MC.player.isUsingItem())
			return;

		final HitResult hit = MC.crosshairTarget;
		if (hit.getType() != HitResult.Type.ENTITY)
			return;
		if (MC.player.getAttackCooldownProgress(0) < cooldown.getValue())
			return;
		final Entity target = ((EntityHitResult) hit).getEntity();
		if (!(target instanceof PlayerEntity))
			return;
		if (!target.isOnGround() && !attackInAir.isEnabled())
			return;
		if (MC.player.getY() > MC.player.prevY && !attackOnJump.isEnabled())
			return;
		MC.interactionManager.attackEntity(MC.player, target);
		MC.player.swingHand(Hand.MAIN_HAND);
	}
}
