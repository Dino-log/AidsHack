package cc.aidshack.module.impl.movement;

import cc.aidshack.event.EventTarget;
import cc.aidshack.event.events.EventReceivePacket;
import cc.aidshack.event.events.EventSendPacket;
import cc.aidshack.mixins.PlayerMoveC2SPacketAccessor;
import cc.aidshack.module.Module;
import cc.aidshack.module.ModuleManager;
import cc.aidshack.module.impl.combat.Killaura;
import cc.aidshack.module.impl.other.AntiHunger;
import cc.aidshack.module.settings.BooleanSetting;
import cc.aidshack.module.settings.DecimalSetting;
import cc.aidshack.module.settings.ModeSetting;
import cc.aidshack.utils.ColorUtils;
import cc.aidshack.utils.PlayerUtils;
import cc.aidshack.utils.math.Timer;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class Flight extends Module {

	public ModeSetting mode = new ModeSetting("Mode", "Velocity", "Velocity", "Vanilla", "Verus");
	public DecimalSetting speed = new DecimalSetting("Speed", 0, 10, 1, 0.1);
	public BooleanSetting damage = new BooleanSetting("Damage", false);
	public BooleanSetting blink = new BooleanSetting("Blink", false);
	boolean hasDamaged = false;
	private int wallTicks = 0;
	private boolean direction = false;
	private static Timer blinkTimer = new Timer();

	public Flight() {
		super("Flight", "Allows you to fly",false,  Category.MOVEMENT);
		addSettings(mode, speed, damage, blink);
	}

	@Override
	public void onEnable() {
		super.onEnable();
	}

	@Override
    public void onTick() {
		this.setDisplayName("Flight " + ColorUtils.gray + mode.getMode());
		if (mc.player == null)
			return;
		if (!mc.player.isOnGround()) {
			wallTicks++;
			if (wallTicks > 7 && mc.player.horizontalCollision) {
				direction = !direction;
				wallTicks = 0;
			}
		} else wallTicks = 0;
		if (damage.isEnabled() && !hasDamaged) {
			boolean antiHunger = ModuleManager.INSTANCE.getModule(AntiHunger.class).isEnabled();
			if (antiHunger) ModuleManager.INSTANCE.getModule(AntiHunger.class).toggleSilent();
			mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), mc.player.getY() + 3.1, mc.player.getZ(), true));
			mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), mc.player.getY(), mc.player.getZ(), false));
			mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), mc.player.getY(), mc.player.getZ(), true));
			hasDamaged = true;
			if (antiHunger) ModuleManager.INSTANCE.getModule(AntiHunger.class).toggleSilent();
		}
		if (mode.isMode("Vanilla")) {
			mc.player.getAbilities().flying = true;
		} else {
			mc.player.getAbilities().flying = false;
			mc.player.airStrafingSpeed = (float) speed.getValue();

			mc.player.setVelocity(0, 0, 0);

			Vec3d velocity = mc.player.getVelocity();

			if (TargetStrafe.canStrafe()) {
				mc.player.getAbilities().flying = true;
				TargetStrafe.strafe(speed.getValue(), Killaura.target, direction, true);
			}
			if(mc.options.jumpKey.isPressed() && !mc.options.sneakKey.isPressed()) {
				mc.player.setVelocity(velocity.add(0, speed.getValue(), 0));
			}

			if(mc.options.sneakKey.isPressed() && !mc.options.jumpKey.isPressed()) {
				mc.player.setVelocity(velocity.subtract(0, speed.getValue(), 0));
			}
			if (mode.isMode("Verus")) {
				mc.player.setVelocity(mc.player.getVelocity().x, 0, mc.player.getVelocity().z);
				mc.player.setOnGround(true);
				if (mc.world.getBlockState(new BlockPos(mc.player.getX(), mc.player.getY() - 1.0, mc.player.getZ())).getBlock() == Blocks.AIR)
					mc.world.setBlockState(new BlockPos(mc.player.getX(), mc.player.getY() - 1.0, mc.player.getZ()), Blocks.BARRIER.getDefaultState(), Block.NOTIFY_LISTENERS);
				PlayerUtils.setMotion(speed.getValue());
			}
		}
	}

	@EventTarget
	public void eventReceivePacket(EventReceivePacket event) {
		if (event.getPacket() instanceof PlayerMoveC2SPacket && mode.isMode("Verus") && (damage.isEnabled() ? hasDamaged == true : true)) {
			((PlayerMoveC2SPacketAccessor) event.getPacket()).setOnGround(true);
		}
	}

	@EventTarget
	public void eventSendPacket(EventSendPacket event) {
		if (blink.isEnabled()) {
			if (blinkTimer.hasTimeElapsed(50, true) && (damage.isEnabled() ? hasDamaged : true)) {
				ModuleManager.INSTANCE.getModule(FlightBlink.class).toggle();
			}
		}
	}

	@Override
	public void onDisable() {
		mc.player.getAbilities().flying = false;
		hasDamaged = false;
		if (ModuleManager.INSTANCE.getModule(FlightBlink.class).isEnabled()) ModuleManager.INSTANCE.getModule(FlightBlink.class).toggle();
		super.onDisable();
	}
}
