package cc.aidshack.module.impl.render;

import cc.aidshack.module.Module;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.OreBlock;
import net.minecraft.block.RedstoneOreBlock;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;

public class Xray extends Module {

    public static ArrayList<Block> blocks = new ArrayList<>();

    public Xray() {
        super("Xray", "See shit through blocks", false, Category.RENDER);
        Registry.BLOCK.forEach(block -> {
            if (blockApplicable(block)) blocks.add(block);
        });
    }

    boolean blockApplicable(Block block) {
        boolean c1 = block == Blocks.LAVA || block == Blocks.CHEST || block == Blocks.FURNACE || block == Blocks.END_GATEWAY || block == Blocks.COMMAND_BLOCK || block == Blocks.ANCIENT_DEBRIS;
        boolean c2 = block instanceof OreBlock || block instanceof RedstoneOreBlock;
        return c1 || c2;
    }

    @Override
    public void onEnable() {
        mc.worldRenderer.reload();
    }

    @Override
    public void onDisable() {
        mc.worldRenderer.reload();
    }
}
