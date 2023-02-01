package cc.aidshack.module.impl.combat;

import cc.aidshack.core.CrystalDataTracker;
import cc.aidshack.event.EventTarget;
import cc.aidshack.event.events.EventItemUse;
import cc.aidshack.module.Module;
import cc.aidshack.module.settings.BooleanSetting;
import cc.aidshack.module.settings.DecimalSetting;
import cc.aidshack.utils.BlockUtils;
import cc.aidshack.utils.CrystalUtils;
import cc.aidshack.utils.RotationUtils;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import org.lwjgl.glfw.GLFW;

import static cc.aidshack.AidsHack.MC;

public class MarlowCrystal extends Module{

    public DecimalSetting breakInterval = new DecimalSetting("Break Interval", 0, 20, 0, 0.1);
    public BooleanSetting activateOnRightClick = new BooleanSetting("On Right Click", true);
    public BooleanSetting stopOnKill = new BooleanSetting("Stop On Kill", true);

    public MarlowCrystal() {
        super("MarlowCrystal", "crystal like Marlowww!", false, Module.Category.COMBAT);
        addSettings(breakInterval, activateOnRightClick, stopOnKill);
    }

    private int crystalBreakClock = 0;

    @Override
    public void onEnable() {
        crystalBreakClock = 0;
    }


    private boolean isDeadBodyNearby() {
        return MC.world.getPlayers().parallelStream()
                .filter(e -> MC.player != e)
                .filter(e -> e.squaredDistanceTo(MC.player) < 36)
                .anyMatch(LivingEntity::isDead);
    }

    @EventTarget
    public void onItemUse(EventItemUse event) {
        ItemStack mainHandStack = MC.player.getMainHandStack();
        if (MC.crosshairTarget.getType() == HitResult.Type.BLOCK) {
            BlockHitResult hit = (BlockHitResult) MC.crosshairTarget;
            if (mainHandStack.isOf(Items.END_CRYSTAL) && BlockUtils.isBlock(Blocks.OBSIDIAN, hit.getBlockPos()))
                event.setCancelled(true);
        }
    }

    @Override
    public void onTick() {
        boolean dontBreakCrystal = crystalBreakClock != 0;
        if (dontBreakCrystal)
            crystalBreakClock--;
        if (activateOnRightClick.isEnabled() && GLFW.glfwGetMouseButton(MC.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_2) != GLFW.GLFW_PRESS)
            return;
        ItemStack mainHandStack = MC.player.getMainHandStack();
        if (!mainHandStack.isOf(Items.END_CRYSTAL))
            return;
        if (stopOnKill.isEnabled() && isDeadBodyNearby())
            return;
        Vec3d camPos = MC.player.getEyePos();
        BlockHitResult blockHit = MC.world.raycast(new RaycastContext(camPos, camPos.add(RotationUtils.getClientLookVec().multiply(4.5)), RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, MC.player));
        if (MC.crosshairTarget instanceof EntityHitResult hit) {
            if (!dontBreakCrystal && hit.getEntity() instanceof EndCrystalEntity crystal) {
                crystalBreakClock = breakInterval.getValueInt();
                MC.interactionManager.attackEntity(MC.player, crystal);
                MC.player.swingHand(Hand.MAIN_HAND);
                CrystalDataTracker.INSTANCE.recordAttack(crystal);
            }
        }
        if (BlockUtils.isBlock(Blocks.OBSIDIAN, blockHit.getBlockPos())) {
            if (CrystalUtils.canPlaceCrystalServer(blockHit.getBlockPos())) {
                ActionResult result = MC.interactionManager.interactBlock(MC.player, mc.world, Hand.MAIN_HAND, blockHit);
                if (result.isAccepted() && result.shouldSwingHand())
                    MC.player.swingHand(Hand.MAIN_HAND);
            }
        }
    }
}