package cc.aidshack.module.impl.combat;

import cc.aidshack.module.Module;
import cc.aidshack.module.settings.BooleanSetting;
import cc.aidshack.module.settings.DecimalSetting;
import cc.aidshack.utils.BlockUtils;
import cc.aidshack.utils.InventoryUtils;
import net.minecraft.block.Blocks;
import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.item.Items;
import net.minecraft.util.hit.BlockHitResult;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.awt.event.InputEvent;

import static cc.aidshack.AidsHack.CLIENT;
import static cc.aidshack.AidsHack.MC;

public class AnchorMacro extends Module   {

	public boolean isClicking = false;

	public DecimalSetting delay = new DecimalSetting("Delay", 1, 7, 1, 0.1);
	public BooleanSetting disableOnCrouch = new BooleanSetting("Not On Crouch", false);

	private int GlowStonePlaceClock = 0;
	private boolean AnchorActivateQueued = false;
	private boolean GlowStonePlaceQueued;
	private int AnchorActivateClock = 0;
	private boolean isActivatingUnactivatedAnchor = false;

	public AnchorMacro() {
		super("AnchorMacro", "Nigger",false, Category.COMBAT);
		addSettings(delay, disableOnCrouch);
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
		if (disableOnCrouch.isEnabled() && MC.player.isSneaking()) {
			return;
		}
		if (MC.player.isHolding(Items.RESPAWN_ANCHOR) && GLFW.glfwGetMouseButton(MC.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_2) == GLFW.GLFW_PRESS && !isClicking) {
			isClicking = true;
			GlowStonePlaceQueued = true;
			GlowStonePlaceClock = delay.getValueInt();
		}
		if (GlowStonePlaceQueued && !isActivatingUnactivatedAnchor) {
			if (GlowStonePlaceClock == delay.getValueInt() / 2) {
				InventoryUtils.selectItemFromHotbar(Items.GLOWSTONE);
			}
			if (GlowStonePlaceClock == 0) {
				CLIENT.getRobot().mousePress(InputEvent.BUTTON3_DOWN_MASK);
				CLIENT.getRobot().mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
				AnchorActivateQueued = true;
				AnchorActivateClock = delay.getValueInt();
				GlowStonePlaceQueued = false;
			}
			GlowStonePlaceClock--;
		}
		if (AnchorActivateQueued && !isActivatingUnactivatedAnchor) {
			try {
				if (AnchorActivateClock == delay.getValueInt() / 2) {
					InventoryUtils.selectItemFromHotbar(Items.TOTEM_OF_UNDYING);
				}
				if (AnchorActivateClock == 0) {
					Robot robot = new Robot();
					robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
					robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
					AnchorActivateQueued = false;
				}
			} catch (AWTException e) {
				e.printStackTrace();
			}
			AnchorActivateClock--;
		}
		//
		//ACTIVATE UNACTIVATED ANCHOR MACRO
		//
		if (MC.crosshairTarget instanceof BlockHitResult hit) {
			if (BlockUtils.isBlock(Blocks.RESPAWN_ANCHOR, hit.getBlockPos())) {
				if (BlockUtils.getBlockState(hit.getBlockPos()).get(RespawnAnchorBlock.CHARGES) == 0) {
					if (MC.player.isHolding(Items.TOTEM_OF_UNDYING) && GLFW.glfwGetMouseButton(MC.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_2) == GLFW.GLFW_PRESS && !isClicking) {
						isClicking = true;
						GlowStonePlaceQueued = true;
						GlowStonePlaceClock = delay.getValueInt();
						isActivatingUnactivatedAnchor = true;
					}
				}
			}
		}
		if (isActivatingUnactivatedAnchor) {
			if (GlowStonePlaceQueued) {
				if (GlowStonePlaceClock == delay.getValueInt() / 2) {
					InventoryUtils.selectItemFromHotbar(Items.GLOWSTONE);
				}
				if (GlowStonePlaceClock == 0) {
					try {
						Robot robot = new Robot();
						robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
						robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
						AnchorActivateQueued = true;
						AnchorActivateClock = delay.getValueInt();
						GlowStonePlaceQueued = false;
					} catch (AWTException e) {
						e.printStackTrace();
					}
				}
				GlowStonePlaceClock--;
			}
			if (AnchorActivateQueued) {
				try {
					if (AnchorActivateClock == delay.getValueInt() / 2) {
						InventoryUtils.selectItemFromHotbar(Items.TOTEM_OF_UNDYING);
					}
					if (AnchorActivateClock == 0) {
						Robot robot = new Robot();
						robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
						robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
						AnchorActivateQueued = false;
						isActivatingUnactivatedAnchor = false;
					}
				} catch (AWTException e) {
					e.printStackTrace();
				}
				AnchorActivateClock--;
			}
		}
		if (GLFW.glfwGetMouseButton(MC.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_2) == GLFW.GLFW_RELEASE && isClicking) {
			isClicking = false;
		}
	}


	public static String klaw = "ht";
}
