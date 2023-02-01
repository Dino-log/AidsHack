package cc.aidshack.module.impl.render;

import cc.aidshack.event.EventTarget;
import cc.aidshack.event.events.EventBlockEntityRender;
import cc.aidshack.event.events.EventRender3D;
import cc.aidshack.module.Module;
import cc.aidshack.module.settings.BooleanSetting;
import cc.aidshack.module.settings.DecimalSetting;
import cc.aidshack.module.settings.ModeSetting;
import cc.aidshack.utils.*;
import cc.aidshack.utils.math.QuadColor;
import com.google.gson.JsonSyntaxException;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.*;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.gl.ShaderEffect;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import org.apache.commons.io.IOUtils;

import java.awt.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class StorageESP extends Module {

    public ModeSetting mode = new ModeSetting("Mode", "Shader", "Shader", "Box", "Box-Fill");
    public DecimalSetting shaderWidth = new DecimalSetting("Shader Width", 0, 2, 0, 1);

    public BooleanSetting chest = new BooleanSetting("Chest", false);
    public String chestColor = Integer.toHexString(Color.YELLOW.getRGB());

    public BooleanSetting shulker = new BooleanSetting("Shulker", false);
    public String shulkerColor = Integer.toHexString(Color.PINK.getRGB());

    public BooleanSetting dispenser = new BooleanSetting("Dispenser", false);
    public String dispenserColor = Integer.toHexString(Color.GRAY.getRGB());

    public BooleanSetting echest = new BooleanSetting("EChest", false);
    public String echestColor = Integer.toHexString(Color.MAGENTA.getRGB());

    public BooleanSetting furnace = new BooleanSetting("Furnace", false);
    public String furnaceColor = Integer.toHexString(Color.GRAY.getRGB());

    private Map<BlockEntity, float[]> blockEntities = new HashMap<>();

    private Set<BlockPos> blacklist = new HashSet<>();

    private int lastWidth = -1;
    private int lastHeight = -1;
    private double lastShaderWidth;
    private boolean shaderUnloaded = true;

    public StorageESP() {
        super("StorageESP", "Renders a box on storage containers", false, Category.RENDER);
        addSettings(mode, shaderWidth, chest, echest, shulker, dispenser, furnace);
    }

    @Override
    public void onTick() {
        blockEntities.clear();

        for (BlockEntity be: WorldUtils.blockEntities()) {
            float[] color = getColorForBlock(be);

            if (color != null) {
                blockEntities.put(be, color);
            }
        }

        super.onTick();
    }

    public boolean shouldRenderBlock(BlockEntity block) {
        if (chest.isEnabled() && block instanceof ChestBlockEntity) return true;
        if (shulker.isEnabled() && block instanceof ShulkerBoxBlockEntity) return true;
        if (echest.isEnabled() && block instanceof EnderChestBlockEntity) return true;
        if (dispenser.isEnabled() && block instanceof DispenserBlockEntity) return true;
        if (furnace.isEnabled() && block instanceof FurnaceBlockEntity) return true;
        return false;
    }

    public Color getBlockColor(BlockEntity block, int alpha) {
        if (block instanceof ChestBlockEntity) return new Color(
                Color.YELLOW.getRed(),
                Color.YELLOW.getGreen(),
                Color.YELLOW.getBlue(), alpha);
        if (block instanceof ShulkerBoxBlockEntity) return new Color(
                Color.PINK.getRed(),
                Color.PINK.getGreen(),
                Color.PINK.getBlue(), alpha);
        if (block instanceof EnderChestBlockEntity) return new Color(
                Color.MAGENTA.getRed(),
                Color.MAGENTA.getGreen(),
                Color.MAGENTA.getBlue(), alpha);
        if (block instanceof DispenserBlockEntity) return new Color(
                Color.GRAY.getRed(),
                Color.GRAY.getGreen(),
                Color.GRAY.getBlue(), alpha).brighter();
        if (block instanceof FurnaceBlockEntity) return new Color(
                Color.GRAY.getRed(),
                Color.GRAY.getGreen(),
                Color.GRAY.getBlue(), alpha).brighter();
        return new Color(255, 255, 255, alpha);
    }

    @EventTarget
    public void onRender(EventRender3D event) {
        if (mode.isMode("Box-Fill") || mode.isMode("Box")) {
            for (Map.Entry<BlockEntity, float[]> e: blockEntities.entrySet()) {
                if (blacklist.contains(e.getKey().getPos())) {
                    continue;
                }

                Box box = new Box(e.getKey().getPos());

                Block block = e.getKey().getCachedState().getBlock();

                if (block == Blocks.CHEST || block == Blocks.TRAPPED_CHEST || block == Blocks.ENDER_CHEST) {
                    box = box.contract(0.06);
                    box = box.offset(0, -0.06, 0);

                    Direction dir = getChestDirection(e.getKey().getPos());
                    if (dir != null) {
                        box = box.expand(Math.abs(dir.getOffsetX()) / 2d, 0, Math.abs(dir.getOffsetZ()) / 2d);
                        box = box.offset(dir.getOffsetX() / 2d, 0, dir.getOffsetZ() / 2d);
                        blacklist.add(e.getKey().getPos().offset(dir));
                    }
                }

                if (mode.isMode("Box-Fill")) {
                    RenderUtils.drawBoxFill(box, QuadColor.single(e.getValue()[0], e.getValue()[1], e.getValue()[2], 1));
                }

                if (mode.isMode("Box-Fill") || mode.isMode("Box")) {
                    RenderUtils.drawBoxOutline(box, QuadColor.single(e.getValue()[0], e.getValue()[1], e.getValue()[2], 1f), 1);
                }
            }

            blacklist.clear();
        }
    }

    @EventTarget
    public void onBlockEntityRenderPre(EventBlockEntityRender.PreAll event) throws JsonSyntaxException, IOException {
        if (mode.isMode("Shader")) {
            if (mc.getWindow().getFramebufferWidth() != lastWidth || mc.getWindow().getFramebufferHeight() != lastHeight
                    || lastShaderWidth != shaderWidth.getValueInt() || shaderUnloaded) {
                try {
                    ShaderEffect shader = ShaderEffectLoader.load(mc.getFramebuffer(), "storageesp-shader",
                            String.format(
                                    Locale.ENGLISH,
                                    IOUtils.toString(getClass().getResource("/assets/hypnotic/shaders/mc_outline.ujson"), StandardCharsets.UTF_8),
                                    shaderWidth.getValueInt() / 2,
                                    shaderWidth.getValueInt() / 4));

                    shader.setupDimensions(mc.getWindow().getFramebufferWidth(), mc.getWindow().getFramebufferHeight());
                    lastWidth = mc.getWindow().getFramebufferWidth();
                    lastHeight = mc.getWindow().getFramebufferHeight();
                    lastShaderWidth = shaderWidth.getValueInt();
                    shaderUnloaded = false;

                    OutlineShaderManager.loadShader(shader);
                } catch (JsonSyntaxException | IOException e) {
                    e.printStackTrace();
                }
            }
        } else if (!shaderUnloaded) {
            OutlineShaderManager.loadDefaultShader();
            shaderUnloaded = true;
        }
    }

    @EventTarget
    public void onBlockEntityRender(EventBlockEntityRender.PreAll event) {
        if (mode.isMode("Shader")) {
            try {
                for (Map.Entry<BlockEntity, float[]> be: blockEntities.entrySet()) {
                    BlockEntityRenderer<BlockEntity> beRenderer = mc.getBlockEntityRenderDispatcher().get(be.getKey());

                    BlockPos pos = be.getKey().getPos();
                    MatrixStack matrices = RenderUtils.matrixFrom(pos.getX(), pos.getY(), pos.getZ());
                    if (beRenderer != null) {
                        beRenderer.render(
                                be.getKey(),
                                mc.getTickDelta(),
                                matrices,
                                OutlineVertexConsumers.outlineOnlyProvider(be.getValue()[0], be.getValue()[1], be.getValue()[2], 1f),
                                0xf000f0, OverlayTexture.DEFAULT_UV);
                    } else {
                        BlockState state = be.getKey().getCachedState();

                        mc.getBlockRenderManager().getModelRenderer().render(
                                mc.world,
                                mc.getBlockRenderManager().getModel(state),
                                state,
                                BlockPos.ORIGIN,
                                matrices,
                                OutlineVertexConsumers.outlineOnlyConsumer(be.getValue()[0], be.getValue()[1], be.getValue()[2], 1f),
                                false,
                                new Random(),
                                0L,
                                OverlayTexture.DEFAULT_UV);
                    }
                }
            } catch(Exception e) {

            }
        }
    }

    private float[] getColorForBlock(BlockEntity be) {
        if ((be instanceof ChestBlockEntity || be instanceof BarrelBlockEntity) && chest.isEnabled()) {
            return new float[] { 1F, 0.6F, 0.3F };
        } else if (be instanceof EnderChestBlockEntity && echest.isEnabled()) {
            return new float[] { 1F, 0.05F, 1F };
        } else if (be instanceof AbstractFurnaceBlockEntity && furnace.isEnabled()) {
            return new float[] { 0.5F, 0.5F, 0.5F };
        } else if (be instanceof DispenserBlockEntity && dispenser.isEnabled()) {
            return new float[] { 0.55F, 0.55F, 0.7F };
        } else if (be instanceof ShulkerBoxBlockEntity && shulker.isEnabled()) {
            return new float[] { 0.5F, 0.2F, 1F };
        }

        return null;
    }

    private Direction getChestDirection(BlockPos pos) {
        BlockState state = mc.world.getBlockState(pos);

        if (state.getBlock() instanceof ChestBlock && state.get(ChestBlock.CHEST_TYPE) != ChestType.SINGLE) {
            return ChestBlock.getFacing(state);
        }

        return null;
    }

    @Override
    public void onTickDisabled() {
        shaderWidth.setVisible(mode.isMode("Shader"));
        super.onTickDisabled();
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

}
