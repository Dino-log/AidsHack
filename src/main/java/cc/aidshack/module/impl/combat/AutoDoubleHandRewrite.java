// 
// Decompiled by Procyon v0.5.36
// 

package cc.aidshack.module.impl.combat;

import cc.aidshack.module.Module;
import cc.aidshack.module.settings.BooleanSetting;
import cc.aidshack.module.settings.DecimalSetting;
import cc.aidshack.utils.*;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static cc.aidshack.AidsHack.MC;

public class AutoDoubleHandRewrite extends Module
{
    private final BooleanSetting dhandafterpop;
    private final BooleanSetting dhandAtHealth;
    private final DecimalSetting dHandHealth;
    private final BooleanSetting checkPlayersAround;
    private final DecimalSetting distance;
    private final BooleanSetting predictCrystals;
    private final BooleanSetting checkEnemiesAim;
    private final BooleanSetting checkHoldingItems;
    private final DecimalSetting activatesAbove;
    private boolean BelowHearts;
    private boolean noOffhandTotem;
    
    public AutoDoubleHandRewrite() {
        super("AutoDHandReWrite", "Automatically double hand when you appear to be in a predicament", false, Category.COMBAT);
        this.dhandafterpop = new BooleanSetting("DHand after Pop",false);
        this.dhandAtHealth = new BooleanSetting("DHand at Health", false);
        this.dHandHealth =  new DecimalSetting("DHand Health",0, 20, 0, 1 );
        final BooleanSetting dhandAtHealth = this.dhandAtHealth;
        Objects.requireNonNull(dhandAtHealth);
        this.checkPlayersAround = new BooleanSetting("Check Around Players",false);
        this.distance = new DecimalSetting("Distance",1.0,10.0,1.0,0.1);
        final BooleanSetting checkPlayersAround = this.checkPlayersAround;
        Objects.requireNonNull(checkPlayersAround);
        this.predictCrystals = new BooleanSetting("Predict Crystals",false);
        this.checkEnemiesAim = new BooleanSetting("Check Aim", false);
        final BooleanSetting predictCrystals = this.predictCrystals;
        Objects.requireNonNull(predictCrystals);
        this.checkHoldingItems = new BooleanSetting("Check Items", false);
        this.activatesAbove = new DecimalSetting("Activation Hight", 0, 4, 0, 0.1);
        this.BelowHearts = false;
        this.noOffhandTotem = false;
        addSettings(dhandafterpop, dhandAtHealth, dHandHealth, checkPlayersAround, distance, predictCrystals, checkEnemiesAim, checkHoldingItems, activatesAbove);
    }

    private List<EndCrystalEntity> getNearByCrystals() {
        final Vec3d pos = MC.player.getPos();
        return (List<EndCrystalEntity>)MC.world.getEntitiesByClass((Class)EndCrystalEntity.class, new Box(pos.add(-6.0, -6.0, -6.0), pos.add(6.0, 6.0, 6.0)), a -> true);
    }
    
    @Override
    public void onTick() {
        final double distanceSq = this.distance.getValue() * this.distance.getValue();
        final PlayerInventory inv = AutoDoubleHandRewrite.mc.player.getInventory();
        if (((ItemStack)inv.offHand.get(0)).getItem() != Items.TOTEM_OF_UNDYING && this.dhandafterpop.isEnabled() && !this.noOffhandTotem) {
            this.noOffhandTotem = true;
            InventoryUtils.selectItemFromHotbar(Items.TOTEM_OF_UNDYING);
        }
        if (((ItemStack)inv.offHand.get(0)).getItem() == Items.TOTEM_OF_UNDYING) {
            this.noOffhandTotem = false;
        }
        if (MC.player.getHealth() <= this.dHandHealth.getValue() && this.dhandAtHealth.isEnabled() && !this.BelowHearts) {
            this.BelowHearts = true;
            InventoryUtils.selectItemFromHotbar(Items.TOTEM_OF_UNDYING);
        }
        if (MC.player.getHealth() > this.dHandHealth.getValue()) {
            this.BelowHearts = false;
        }
        if (MC.player.getHealth() > 19.0f) {
            return;
        }
        if (this.checkPlayersAround.isEnabled() && MC.world.getPlayers().parallelStream().filter(e -> e != MC.player).noneMatch(player -> MC.player.squaredDistanceTo(player) <= distanceSq)) {
            return;
        }
        final double activatesAboveV = this.activatesAbove.getValue();
        for (int f = (int)Math.floor(activatesAboveV), i = 1; i <= f; ++i) {
            if (BlockUtils.hasBlock(MC.player.getBlockPos().add(0, -i, 0))) {
                return;
            }
        }
        if (BlockUtils.hasBlock(new BlockPos(MC.player.getPos().add(0.0, -activatesAboveV, 0.0)))) {
            return;
        }
        final List<EndCrystalEntity> crystals = this.getNearByCrystals();
        final ArrayList<Vec3d> crystalsPos = new ArrayList<Vec3d>();
        crystals.forEach(e -> crystalsPos.add(e.getPos()));
        if (this.predictCrystals.isEnabled()) {
            Stream<BlockPos> stream = BlockUtils.getAllInBoxStream(MC.player.getBlockPos().add(-6, -8, -6), MC.player.getBlockPos().add(6, 2, 6)).filter(e -> BlockUtils.isBlock(Blocks.OBSIDIAN, e) || BlockUtils.isBlock(Blocks.BEDROCK, e)).filter(CrystalUtils::canPlaceCrystalClient);
            if (this.checkEnemiesAim.isEnabled()) {
                if (this.checkHoldingItems.isEnabled()) {
                    stream = stream.filter(this::arePeopleAimingAtBlockAndHoldingCrystals);
                }
                else {
                    stream = stream.filter(this::arePeopleAimingAtBlock);
                }
            }
            stream.forEachOrdered(e -> crystalsPos.add(Vec3d.ofBottomCenter(e).add(0.0, 1.0, 0.0)));
        }
        for (final Vec3d pos : crystalsPos) {
            final double damage = DamageUtils.crystalDamage((PlayerEntity) MC.player, pos, true, null, false);
            if (damage >= MC.player.getHealth() + MC.player.getAbsorptionAmount()) {
                InventoryUtils.selectItemFromHotbar(Items.TOTEM_OF_UNDYING);
                break;
            }
        }
    }
    
    private boolean arePeopleAimingAtBlock(final BlockPos block) {
        final Vec3d[] eyesPos = new Vec3d[1];
        final BlockHitResult[] hitResult = new BlockHitResult[1];
        return MC.world.getPlayers().parallelStream().filter(e -> e != MC.player).anyMatch(e -> {
            eyesPos[0] = RotationUtils.getEyesPos(e);
            hitResult[0] = MC.world.raycast(new RaycastContext(eyesPos[0], eyesPos[0].add(RotationUtils.getPlayerLookVec(e).multiply(4.5)), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, (Entity) e));
            return hitResult[0] != null && hitResult[0].getBlockPos().equals((Object)block);
        });
    }
    
    private boolean arePeopleAimingAtBlockAndHoldingCrystals(final BlockPos block) {
        final Vec3d[] eyesPos = new Vec3d[1];
        final BlockHitResult[] hitResult = new BlockHitResult[1];
        return MC.world.getPlayers().parallelStream().filter(e -> e != MC.player).filter(e -> e.isHolding(Items.END_CRYSTAL)).anyMatch(e -> {
            eyesPos[0] = RotationUtils.getEyesPos(e);
            hitResult[0] = MC.world.raycast(new RaycastContext(eyesPos[0], eyesPos[0].add(RotationUtils.getPlayerLookVec(e).multiply(4.5)), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, (Entity) e));
            return hitResult[0] != null && hitResult[0].getBlockPos().equals((Object)block);
        });
    }
}
