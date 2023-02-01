package cc.aidshack.module.impl.combat;

import cc.aidshack.module.Module;
import cc.aidshack.module.settings.BooleanSetting;
import cc.aidshack.module.settings.DecimalSetting;
import cc.aidshack.utils.InventoryUtils;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.glfw.GLFW;

import static cc.aidshack.AidsHack.MC;
import static cc.aidshack.utils.BlockUtils.getBlockState;
import static cc.aidshack.utils.BlockUtils.isAnchorCharged;

public class AutoAnchor extends Module
{
    public DecimalSetting explodeSlot = new DecimalSetting("Explode Slot", 0 , 8, 0 , 1) ;
    public BooleanSetting chargeOnly = new BooleanSetting("Charge Only", false);
    public DecimalSetting Cooldown = new DecimalSetting("Cooldown", 0, 10, 4, 1);
    private boolean hasAnchored;
    private int clock;
    
    public AutoAnchor() {
        super("AnchorMacroRewrite", "Automatically explodes Anchors you place", false, Category.COMBAT);
        addSettings(explodeSlot, chargeOnly, Cooldown);
        this.hasAnchored = false;
        this.clock = 0;
    }
    
    @Override
    public void onEnable() {
        super.onEnable();
        this.clock = 0;
        this.hasAnchored = false;
    }
    
    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Override
    public void onTick(){
        if (GLFW.glfwGetMouseButton(MC.getWindow().getHandle(), 1) != 1) {
            return;
        }
        if (MC.player.isUsingItem()) {
            return;
        }
        if (this.hasAnchored) {
            if (this.clock != 0) {
                --this.clock;
                return;
            }
            this.clock = this.Cooldown.getValueInt();
            this.hasAnchored = false;
        }
        final HitResult cr = MC.crosshairTarget;
        if (cr instanceof BlockHitResult) {
            final BlockHitResult hit = (BlockHitResult)cr;
            final BlockPos pos = hit.getBlockPos();
            if (isAnchorUncharged(pos)) {
                if (MC.player.isHolding(Items.GLOWSTONE)) {
                    final ActionResult actionResult = MC.interactionManager.interactBlock(MC.player, mc.world, Hand.MAIN_HAND, hit);
                    if (actionResult.isAccepted() && actionResult.CONSUME.shouldSwingHand()) {
                        MC.player.swingHand(Hand.MAIN_HAND);
                    }
                    return;
                }
                InventoryUtils.selectItemFromHotbar(Items.GLOWSTONE);
                final ActionResult actionResult = MC.interactionManager.interactBlock(MC.player, mc.world, Hand.MAIN_HAND, hit);
                if (actionResult.isAccepted() && actionResult.CONSUME.shouldSwingHand()) {
                    MC.player.swingHand(Hand.MAIN_HAND);
                }
            }
            else if (isAnchorCharged(pos) && !this.chargeOnly.isEnabled()) {
                final PlayerInventory inv = AutoAnchor.mc.player.getInventory();
                inv.selectedSlot = this.explodeSlot.getValueInt();
                final ActionResult actionResult2 = MC.interactionManager.interactBlock(MC.player,mc.world,  Hand.MAIN_HAND, hit);
                if (actionResult2.isAccepted() && actionResult2.CONSUME.shouldSwingHand()) {
                    MC.player.swingHand(Hand.MAIN_HAND);
                }
                this.hasAnchored = true;
            }
        }
    }
    public static boolean isAnchorUncharged(BlockPos anchor) {
        if (!isBlock(Blocks.RESPAWN_ANCHOR, anchor)) {
            return false;
        } else {
            try {
                return (Integer) getBlockState(anchor).get(RespawnAnchorBlock.CHARGES) == 0;
            } catch (IllegalArgumentException var2) {
                return false;
            }
        }
    }
    public static boolean isBlock(Block block, BlockPos pos) {
        return getBlockState(pos).getBlock() == block;
    }
}
