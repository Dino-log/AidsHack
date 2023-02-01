package cc.aidshack.module.impl.combat;

import cc.aidshack.event.*;
import cc.aidshack.event.events.EventTick;
import cc.aidshack.module.Module;
import cc.aidshack.module.settings.BooleanSetting;
import cc.aidshack.module.settings.DecimalSetting;
import cc.aidshack.utils.*;
import net.minecraft.block.Blocks;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.item.Items;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static cc.aidshack.AidsHack.MC;

public class AutoDoubleHand extends Module   {

    public BooleanSetting checkPlayersAround = new BooleanSetting("Check players around", false);
    public DecimalSetting distance = new DecimalSetting("Distance", 2, 10, 6, 0.1);
    public BooleanSetting predictCrystals = new BooleanSetting("Predict crystals", false);

    public BooleanSetting checkEnemiesAim = new BooleanSetting("Check enemy aim", false);
    public BooleanSetting checkHoldingItems = new BooleanSetting("Check held items", false);

    public DecimalSetting activatesAbove = new DecimalSetting("Activates above", 0, 4, 0.5, 0.1);

    public AutoDoubleHand() {
        super("AutoDoubleHand", "Automatically double hand when you appear to be in a predicament", false,Category.COMBAT);
        addSettings(checkPlayersAround, distance, predictCrystals, checkEnemiesAim, checkHoldingItems, activatesAbove);
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }



    private List<EndCrystalEntity> getNearByCrystals() {
        Vec3d pos = MC.player.getPos();
        return MC.world.getEntitiesByClass(EndCrystalEntity.class, new Box(pos.add(-6, -6, -6), pos.add(6, 6, 6)), a -> true);
    }

    @Override
    public void onTick() {
        double distanceSq = distance.getValueInt() * distance.getValueInt();
        if (checkPlayersAround.isEnabled() && MC.world.getPlayers().parallelStream()
                .filter(e -> e != MC.player)
                .noneMatch(player -> MC.player.squaredDistanceTo(player) <= distanceSq))
            return;

        double activatesAboveV = activatesAbove.getValueInt();
        int f = (int) Math.floor(activatesAboveV);
        for (int i = 1; i <= f; i++)
            if (BlockUtils.hasBlock(MC.player.getBlockPos().add(0, -i, 0)))
                return;
        if (BlockUtils.hasBlock(new BlockPos(MC.player.getPos().add(0, -activatesAboveV, 0))))
            return;

        List<EndCrystalEntity> crystals = getNearByCrystals();
        ArrayList<Vec3d> crystalsPos = new ArrayList<>();
        crystals.forEach(e -> crystalsPos.add(e.getPos()));

        if (predictCrystals.isEnabled()) {
            Stream<BlockPos> stream =
                    BlockUtils.getAllInBoxStream(MC.player.getBlockPos().add(-6, -8, -6), MC.player.getBlockPos().add(6, 2, 6))
                            .filter(e -> BlockUtils.isBlock(Blocks.OBSIDIAN, e) || BlockUtils.isBlock(Blocks.BEDROCK, e))
                            .filter(CrystalUtils::canPlaceCrystalClient);
            if (checkEnemiesAim.isEnabled()) {
                if (checkHoldingItems.isEnabled())
                    stream = stream.filter(this::arePeopleAimingAtBlockAndHoldingCrystals);
                else
                    stream = stream.filter(this::arePeopleAimingAtBlock);
            }
            stream.forEachOrdered(e -> crystalsPos.add(Vec3d.ofBottomCenter(e).add(0, 1, 0)));
        }

        for (Vec3d pos : crystalsPos) {
            double damage =
                    DamageUtils.crystalDamage(MC.player, pos, true, null, false);
            if (damage >= MC.player.getHealth() + MC.player.getAbsorptionAmount()) {
                InventoryUtils.selectItemFromHotbar(Items.TOTEM_OF_UNDYING);
                break;
            }
        }
    }

    private boolean arePeopleAimingAtBlock(BlockPos block) {
        return MC.world.getPlayers().parallelStream()
                .filter(e -> e != MC.player)
                .anyMatch(e ->
                {
                    Vec3d eyesPos = RotationUtils.getEyesPos(e);
                    BlockHitResult hitResult = MC.world.raycast(new RaycastContext(eyesPos, eyesPos.add(RotationUtils.getPlayerLookVec(e).multiply(4.5)), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, e));
                    return hitResult != null && hitResult.getBlockPos().equals(block);
                });
    }

    private boolean arePeopleAimingAtBlockAndHoldingCrystals(BlockPos block) {
        return MC.world.getPlayers().parallelStream()
                .filter(e -> e != MC.player)
                .filter(e -> e.isHolding(Items.END_CRYSTAL))
                .anyMatch(e ->
                {
                    Vec3d eyesPos = RotationUtils.getEyesPos(e);
                    BlockHitResult hitResult = MC.world.raycast(new RaycastContext(eyesPos, eyesPos.add(RotationUtils.getPlayerLookVec(e).multiply(4.5)), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, e));
                    return hitResult != null && hitResult.getBlockPos().equals(block);
                });
    }

    public static String kjads9 = "tps:";
}