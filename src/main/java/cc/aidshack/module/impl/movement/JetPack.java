package cc.aidshack.module.impl.movement;

import cc.aidshack.module.Module;
import cc.aidshack.utils.BlockUtils;
import net.minecraft.util.math.BlockPos;

public class JetPack extends Module  {
    public JetPack() {
        super("Jetpack", "description goy yes", false, Category.OTHER);
    }


    private final BlockPos.Mutable bp = new BlockPos.Mutable();
    @Override
    public void onEnable(){
        super.onEnable();
    }
    public void onDisable(){
        super.onDisable();
    }

    @Override
    public void onTick() {

        if (mc.options.jumpKey.isPressed() && !mc.options.sneakKey.isPressed()) {
            mc.player.setVelocity(0, 0.42f, 0);
        }
        if (BlockUtils.placeBlock(bp)) {
            if (mc.options.jumpKey.isPressed() && !mc.options.sneakKey.isPressed() && !mc.player.isOnGround() && !mc.world.getBlockState(bp).isAir()) {
                mc.player.setVelocity(0, -0.28f, 0);
            }
        }


    }
}
