package cc.aidshack.module.impl.combat;

import cc.aidshack.module.Module;
import cc.aidshack.utils.BlockUtils;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;

import static cc.aidshack.AidsHack.MC;

public class MarlowAnchor extends Module
{
    public MarlowAnchor() {
        super("MarlowAnchor", "Anchor like marlow :3", false, Category.COMBAT);
    }

    @Override
    public void onEnable()
    {
    }

    @Override
    public void onDisable()
    {
    }
    @Override
    public void onTick()
    {
        if (MC.crosshairTarget instanceof BlockHitResult hit)
        {
            BlockPos pos = hit.getBlockPos();
            if (BlockUtils.isAnchorCharged(pos))
            {
                if (!MC.player.isHolding(Items.GLOWSTONE))
                {
                    ActionResult actionResult = MC.interactionManager.interactBlock(MC.player,MC.world, Hand.MAIN_HAND, hit);
                    if (actionResult.isAccepted() && actionResult.shouldSwingHand())
                        MC.player.swingHand(Hand.MAIN_HAND);
                }
            }
        }
    }
}
