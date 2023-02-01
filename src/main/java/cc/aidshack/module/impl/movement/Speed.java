package cc.aidshack.module.impl.movement;

import cc.aidshack.module.Module;
import cc.aidshack.module.ModuleManager;
import cc.aidshack.module.impl.combat.Killaura;
import cc.aidshack.module.impl.other.Timer;
import cc.aidshack.module.settings.BooleanSetting;
import cc.aidshack.module.settings.DecimalSetting;
import cc.aidshack.module.settings.ModeSetting;
import cc.aidshack.utils.ColorUtils;
import cc.aidshack.utils.PlayerUtils;
import net.minecraft.block.Blocks;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.BlockPos;

public class Speed extends Module {

	public ModeSetting mode = new ModeSetting("Mode", "Vanilla", "Vanilla", "NCP");
	public DecimalSetting speed = new DecimalSetting("Speed", 1, 10, 4, 0.1);
	public DecimalSetting timerBoost = new DecimalSetting("Timer Boost", 1, 5, 1, 0.01);
	public BooleanSetting jump = new BooleanSetting("Jump", false);
	public DecimalSetting jumpHeight = new DecimalSetting("Jump Height", 1, 10, 4, 0.1);
	public BooleanSetting override = new BooleanSetting("Override Jump Height", false);
	
	private int wallTicks = 0;
	private boolean direction = false;
	
	public Speed() {
		super("Speed", "Makes you go fast",false,  Category.MOVEMENT);
		addSettings(mode, speed, timerBoost, jump, jumpHeight, override);
	}
	
	@Override
    public void onTick() {
		if(mc.player.isOnGround() && PlayerUtils.isMoving() && jump.isEnabled()) mc.player.setVelocity(mc.player.getVelocity().x, override.isEnabled() && mc.options.jumpKey.isPressed() ? 0.45 : jumpHeight.getValue() * 0.1, mc.player.getVelocity().z);
	}

	@Override
	public void onMotion() {
		this.setDisplayName("Speed " + ColorUtils.gray + mode.getMode());
		PlayerUtils.setTimerSpeed((float) timerBoost.getValue());
		if(mc.player != null && (mc.player.input.movementForward != 0 || mc.player.input.movementSideways != 0) && !mc.player.isTouchingWater()) {
			if (!mc.player.isOnGround()) {
				wallTicks++;
				if (wallTicks > 7 && mc.player.horizontalCollision) {
					direction = !direction;
					wallTicks = 0;
				}
			} else wallTicks = 0;
			BlockPos downBlock = mc.player.isOnGround() ? mc.player.getBlockPos() : mc.player.getBlockPos().down();
			boolean isIce = mc.world.getBlockState(downBlock).getBlock() == Blocks.ICE || mc.world.getBlockState(downBlock).getBlock() == Blocks.PACKED_ICE || mc.world.getBlockState(downBlock).getBlock() == Blocks.BLUE_ICE;
			double ncpLimit = (mc.player.isOnGround() ? 0 : (mc.player.hasStatusEffect(StatusEffects.SPEED) ? (mc.player.getStatusEffect(StatusEffects.SPEED).getAmplifier() == 1 ? 4 : 3.5) : 2.9)) + (isIce ? 4.3 : 0);
            double speed = (mode.isMode("NCP") ? ncpLimit * 0.1 : (this.speed.getValue()) * 0.1 + (mc.player.isOnGround() ? this.speed.getValue() * 0.01 : 0));
            if (mode.isMode("NCP") && !mc.player.isOnGround()) PlayerUtils.setMotion(speed);
            else if (mode.isMode("Vanilla")) PlayerUtils.setMotion(speed);
            if (TargetStrafe.canStrafe()) {
            	TargetStrafe.strafe(speed, Killaura.target, direction, false);
            }
        }
		super.onMotion();
	}
	
	@Override
	public void onTickDisabled() {
		speed.setVisible(!mode.isMode("NCP"));
		jumpHeight.setVisible(jump.isEnabled());
		super.onTickDisabled();
	}
	
	@Override
	public void onDisable() {
		if (!ModuleManager.INSTANCE.getModule(Timer.class).isEnabled()) PlayerUtils.setTimerSpeed(1f);
		super.onDisable();
	}

}
