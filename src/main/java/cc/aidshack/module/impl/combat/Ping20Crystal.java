package cc.aidshack.module.impl.combat;

import cc.aidshack.core.CrystalDataTracker;
import cc.aidshack.event.EventTarget;
import cc.aidshack.event.events.EventItemUse;
import cc.aidshack.module.Module;
import cc.aidshack.module.settings.BooleanSetting;
import cc.aidshack.module.settings.DecimalSetting;
import cc.aidshack.utils.BlockUtils;
import cc.aidshack.utils.CrystalUtils;
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
import net.minecraft.util.math.BlockPos;
import org.lwjgl.glfw.GLFW;

import static cc.aidshack.AidsHack.MC;

public class Ping20Crystal extends Module {

    public DecimalSetting placeInterval = new DecimalSetting("Place Interval", 4, 4, 4, 0.1);
    public DecimalSetting breakInterval = new DecimalSetting("Break Interval", 4, 4, 4, 0.1);
    public BooleanSetting activateOnRightClick = new BooleanSetting("On Right Click", true);
    public BooleanSetting stopOnKill = new BooleanSetting("Stop On Kill", true);



    private int crystalPlaceClock = 0;
    private int crystalBreakClock = 0;

    public Ping20Crystal() {
        super("Ping20Crystal", "cw", false,Category.COMBAT);
        addSetting(stopOnKill);
        addSetting(activateOnRightClick);
    }



    @Override
    public void onEnable() {
        super.onEnable();

        crystalPlaceClock = 0;
        crystalBreakClock = 0;
    }

    @Override
    public void onDisable() {
        super.onDisable();

    }

    private boolean isDeadBodyNearby()
    {
        return MC.world.getPlayers().parallelStream()
                .filter(e -> MC.player != e)
                .filter(e -> e.squaredDistanceTo(MC.player) < 36)
                .anyMatch(LivingEntity::isDead);
    }

    @Override
    public void onTick() {
        boolean dontPlaceCrystal = crystalPlaceClock != 0;
        boolean dontBreakCrystal = crystalBreakClock != 0;
        if (dontPlaceCrystal)
            crystalPlaceClock--;
        if (dontBreakCrystal)
            crystalBreakClock--;
        if (activateOnRightClick.isEnabled() && GLFW.glfwGetMouseButton(MC.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_2) != GLFW.GLFW_PRESS)
            return;
        ItemStack mainHandStack = MC.player.getMainHandStack();
        if (!mainHandStack.isOf(Items.END_CRYSTAL))
            return;
        if (stopOnKill.isEnabled() && isDeadBodyNearby())
            return;

        if (MC.crosshairTarget instanceof EntityHitResult hit)
        {
            if (!dontBreakCrystal && hit.getEntity() instanceof EndCrystalEntity crystal)
            {
                crystalBreakClock = breakInterval.getValueInt();
                MC.interactionManager.attackEntity(MC.player, crystal);
                MC.player.swingHand(Hand.MAIN_HAND);
                CrystalDataTracker.INSTANCE.recordAttack(crystal);
            }
        }
        if (MC.crosshairTarget instanceof BlockHitResult hit)
        {
            BlockPos block = hit.getBlockPos();
            if (!dontPlaceCrystal && CrystalUtils.canPlaceCrystalServer(block))
            {
                crystalPlaceClock = placeInterval.getValueInt();
                ActionResult result = MC.interactionManager.interactBlock(MC.player, MC.world, Hand.MAIN_HAND, hit);
                if (result.isAccepted() && result.shouldSwingHand())
                    MC.player.swingHand(Hand.MAIN_HAND);
            }
        }
    }

    @EventTarget
    public void onItemUse(EventItemUse event)
    {
        ItemStack mainHandStack = MC.player.getMainHandStack();
        if (MC.crosshairTarget.getType() == HitResult.Type.BLOCK)
        {
            BlockHitResult hit = (BlockHitResult) MC.crosshairTarget;
            if (mainHandStack.isOf(Items.END_CRYSTAL) && BlockUtils.isBlock(Blocks.OBSIDIAN, hit.getBlockPos()))
                event.setCancelled(true);
        }
    }
}
