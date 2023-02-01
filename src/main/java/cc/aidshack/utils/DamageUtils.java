package cc.aidshack.utils;

import cc.aidshack.event.EventManager;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.DamageUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameMode;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.explosion.Explosion;

import java.util.Objects;

import static cc.aidshack.AidsHack.MC;

public class DamageUtils {

	private static double crystalDamageThisTick = 0;

	private static DamageUtils instance = new DamageUtils();

	public DamageUtils() {
		EventManager.INSTANCE.register(this);
	}

	public static DamageUtils getInstance() {
		return instance;
	}
	// Crystal damage

	public static double crystalDamage(PlayerEntity player, Vec3d playerPos, Vec3d crystal, BlockPos obsidianPos, boolean ignoreTerrain) {
		if (player == null)
			return 0;
		if (PlayerUtils.getGameMode(player) == GameMode.CREATIVE)
			return 0;

		final double modDistance = Math.sqrt(playerPos.squaredDistanceTo(crystal));
		if (modDistance > 12)
			return 0;

		final double exposure = getExposure(crystal, player, playerPos, obsidianPos, ignoreTerrain);
		final double impact = (1 - modDistance / 12) * exposure;
		double damage = (impact * impact + impact) / 2 * 7 * (6 * 2) + 1;

		damage = getDamageForDifficulty(damage);
		damage = DamageUtil.getDamageLeft((float) damage, player.getArmor(), (float) player.getAttributeInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS).getValue());
		damage = resistanceReduction(player, damage);

		final Explosion explosion = new Explosion(MC.world, null, crystal.x, crystal.y, crystal.z, 6);
		damage = blastProtReduction(player, damage, explosion);

		if (player.hurtTime > 0) {
			if (damage > crystalDamageThisTick) {
				damage = damage - crystalDamageThisTick;
			} else {
				damage = 0;
			}
		}

		return damage < 0 ? 0 : damage;
	}

	public static double crystalDamage(PlayerEntity player, Vec3d crystal, boolean predictMovement, BlockPos obsidianPos, boolean ignoreTerrain) {
		if (player == null)
			return 0;
		if (PlayerUtils.getGameMode(player) == GameMode.CREATIVE)
			return 0;

		Vec3d playerPos = new Vec3d(player.getPos().x, player.getPos().y, player.getPos().z);

		if (predictMovement)
			playerPos = new Vec3d(playerPos.x + player.getVelocity().x, playerPos.y + player.getVelocity().y, playerPos.z + player.getVelocity().z);

		final double modDistance = Math.sqrt(playerPos.squaredDistanceTo(crystal));
		if (modDistance > 12)
			return 0;

		final double exposure = getExposure(crystal, player, predictMovement, obsidianPos, ignoreTerrain);
		final double impact = (1 - modDistance / 12) * exposure;
		double damage = (impact * impact + impact) / 2 * 7 * (6 * 2) + 1;

		damage = getDamageForDifficulty(damage);
		damage = DamageUtil.getDamageLeft((float) damage, player.getArmor(), (float) player.getAttributeInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS).getValue());
		damage = resistanceReduction(player, damage);

		final Explosion explosion = new Explosion(MC.world, null, crystal.x, crystal.y, crystal.z, 6);
		damage = blastProtReduction(player, damage, explosion);

		return damage < 0 ? 0 : damage;
	}

	public static double crystalDamage(PlayerEntity player, Vec3d crystal) {
		return crystalDamage(player, crystal, false, null, false);
	}

	public static double crystalDamage(PlayerEntity player, Vec3d playerPos, Vec3d crystal) {
		return crystalDamage(player, playerPos, crystal, null, false);
	}

	// Sword damage

	public static double getSwordDamage(PlayerEntity entity, boolean charged) {
		// Get sword damage
		double damage = 0;
		if (charged) {
			if (entity.getActiveItem().getItem() == Items.DIAMOND_SWORD) {
				damage += 7;
			} else if (entity.getActiveItem().getItem() == Items.GOLDEN_SWORD) {
				damage += 4;
			} else if (entity.getActiveItem().getItem() == Items.IRON_SWORD) {
				damage += 6;
			} else if (entity.getActiveItem().getItem() == Items.STONE_SWORD) {
				damage += 5;
			} else if (entity.getActiveItem().getItem() == Items.WOODEN_SWORD) {
				damage += 4;
			}
			damage *= 1.5;
		}

		if (entity.getActiveItem().getEnchantments() != null) {
			if (EnchantmentHelper.get(entity.getActiveItem()).containsKey(Enchantments.SHARPNESS)) {
				final int level = EnchantmentHelper.getLevel(Enchantments.SHARPNESS, entity.getActiveItem());
				damage += 0.5 * level + 0.5;
			}
		}

		if (entity.getActiveStatusEffects().containsKey(StatusEffects.STRENGTH)) {
			final int strength = Objects.requireNonNull(entity.getStatusEffect(StatusEffects.STRENGTH)).getAmplifier() + 1;
			damage += 3 * strength;
		}

		// Reduce by resistance
		damage = resistanceReduction(entity, damage);

		// Reduce by armour
		damage = DamageUtil.getDamageLeft((float) damage, entity.getArmor(), (float) entity.getAttributeInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS).getValue());

		// Reduce by enchants
		damage = normalProtReduction(entity, damage);

		return damage < 0 ? 0 : damage;
	}

	// Bed damage

	public static double bedDamage(LivingEntity player, Vec3d bed) {
		if (player instanceof PlayerEntity && ((PlayerEntity) player).getAbilities().creativeMode)
			return 0;

		final double modDistance = Math.sqrt(player.squaredDistanceTo(bed));
		if (modDistance > 10)
			return 0;

		final double exposure = Explosion.getExposure(bed, player);
		final double impact = (1.0 - modDistance / 10.0) * exposure;
		double damage = (impact * impact + impact) / 2 * 7 * (5 * 2) + 1;

		// Multiply damage by difficulty
		damage = getDamageForDifficulty(damage);

		// Reduce by resistance
		damage = resistanceReduction(player, damage);

		// Reduce by armour
		damage = DamageUtil.getDamageLeft((float) damage, player.getArmor(), (float) player.getAttributeInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS).getValue());

		// Reduce by enchants
		final Explosion explosion = new Explosion(MC.world, null, bed.x, bed.y, bed.z, 5);
		damage = blastProtReduction(player, damage, explosion);

		if (damage < 0)
			damage = 0;
		return damage;
	}

	// Anchor damage

	public static double anchorDamage(LivingEntity player, Vec3d anchor) {
		final BlockState state = BlockUtils.getBlockState(new BlockPos(anchor));
		MC.world.removeBlock(new BlockPos(anchor), false);
		final double damage = bedDamage(player, anchor);
		MC.world.setBlockState(new BlockPos(anchor), state);
		return damage;
	}

	// Utils

	private static double getDamageForDifficulty(double damage) {
		return switch (MC.world.getDifficulty()) {
			case PEACEFUL -> 0;
			case EASY -> Math.min(damage / 2 + 1, damage);
			case HARD -> damage * 3 / 2;
			default -> damage;
		};
	}

	private static double normalProtReduction(Entity player, double damage) {
		int protLevel = EnchantmentHelper.getProtectionAmount(player.getArmorItems(), DamageSource.GENERIC);
		if (protLevel > 20)
			protLevel = 20;

		damage *= 1 - protLevel / 25.0;
		return damage < 0 ? 0 : damage;
	}

	private static double blastProtReduction(Entity player, double damage, Explosion explosion) {
		int protLevel = EnchantmentHelper.getProtectionAmount(player.getArmorItems(), DamageSource.explosion(explosion));
		if (protLevel > 20)
			protLevel = 20;

		damage *= 1 - protLevel / 25.0;
		return damage < 0 ? 0 : damage;
	}

	private static double resistanceReduction(LivingEntity player, double damage) {
		if (player.hasStatusEffect(StatusEffects.RESISTANCE)) {
			final int lvl = player.getStatusEffect(StatusEffects.RESISTANCE).getAmplifier() + 1;
			damage *= 1 - lvl * 0.2;
		}

		return damage < 0 ? 0 : damage;
	}

	private static double getExposure(Vec3d source, Entity entity, boolean predictMovement, BlockPos obsidianPos, boolean ignoreTerrain) {
		final Box box = entity.getBoundingBox();
		if (predictMovement) {
			final Vec3d v = entity.getVelocity();
			box.offset(v.x, v.y, v.z);
		}

		final double d = 1 / ((box.maxX - box.minX) * 2 + 1);
		final double e = 1 / ((box.maxY - box.minY) * 2 + 1);
		final double f = 1 / ((box.maxZ - box.minZ) * 2 + 1);
		final double g = (1 - Math.floor(1 / d) * d) / 2;
		final double h = (1 - Math.floor(1 / f) * f) / 2;

		if (!(d < 0) && !(e < 0) && !(f < 0)) {
			int i = 0;
			int j = 0;

			for (double k = 0; k <= 1; k += d) {
				for (double l = 0; l <= 1; l += e) {
					for (double m = 0; m <= 1; m += f) {
						final double n = MathHelper.lerp(k, box.minX, box.maxX);
						final double o = MathHelper.lerp(l, box.minY, box.maxY);
						final double p = MathHelper.lerp(m, box.minZ, box.maxZ);

						final Vec3d vec3d = new Vec3d(n + g, o, p + h);

						final RaycastContext raycastContext = new RaycastContext(vec3d, source, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, entity);

						if (raycast(raycastContext, obsidianPos, ignoreTerrain).getType() == HitResult.Type.MISS)
							i++;

						j++;
					}
				}
			}

			return (double) i / j;
		}

		return 0;
	}

	private static double getExposure(Vec3d source, Entity entity, Vec3d playerPos, BlockPos obsidianPos, boolean ignoreTerrain) {
		final Box box = entity.getBoundingBox();
		final Vec3d v = playerPos.subtract(entity.getPos());
		box.offset(v.x, v.y, v.z);

		final double d = 1 / ((box.maxX - box.minX) * 2 + 1);
		final double e = 1 / ((box.maxY - box.minY) * 2 + 1);
		final double f = 1 / ((box.maxZ - box.minZ) * 2 + 1);
		final double g = (1 - Math.floor(1 / d) * d) / 2;
		final double h = (1 - Math.floor(1 / f) * f) / 2;

		if (!(d < 0) && !(e < 0) && !(f < 0)) {
			int i = 0;
			int j = 0;

			for (double k = 0; k <= 1; k += d) {
				for (double l = 0; l <= 1; l += e) {
					for (double m = 0; m <= 1; m += f) {
						final double n = MathHelper.lerp(k, box.minX, box.maxX);
						final double o = MathHelper.lerp(l, box.minY, box.maxY);
						final double p = MathHelper.lerp(m, box.minZ, box.maxZ);

						final Vec3d vec3d = new Vec3d(n + g, o, p + h);

						final RaycastContext raycastContext = new RaycastContext(vec3d, source, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, entity);

						if (raycast(raycastContext, obsidianPos, ignoreTerrain).getType() == HitResult.Type.MISS)
							i++;

						j++;
					}
				}
			}

			return (double) i / j;
		}

		return 0;
	}

	private static BlockHitResult raycast(RaycastContext context, BlockPos obsidianPos, boolean ignoreTerrain) {
		return BlockView.raycast(context.getStart(), context.getEnd(), context, (raycastContext, blockPos) -> {
			BlockState blockState;
			if (blockPos.equals(obsidianPos))
				blockState = Blocks.OBSIDIAN.getDefaultState();
			else {
				blockState = MC.world.getBlockState(blockPos);
				if (blockState.getBlock().getBlastResistance() < 600 && ignoreTerrain)
					blockState = Blocks.AIR.getDefaultState();
			}

			final Vec3d vec3d = raycastContext.getStart();
			final Vec3d vec3d2 = raycastContext.getEnd();

			final VoxelShape voxelShape = raycastContext.getBlockShape(blockState, MC.world, blockPos);
			final BlockHitResult blockHitResult = MC.world.raycastBlock(vec3d, vec3d2, blockPos, voxelShape, blockState);
			final VoxelShape voxelShape2 = VoxelShapes.empty();
			final BlockHitResult blockHitResult2 = voxelShape2.raycast(vec3d, vec3d2, blockPos);

			final double d = blockHitResult == null ? Double.MAX_VALUE : raycastContext.getStart().squaredDistanceTo(blockHitResult.getPos());
			final double e = blockHitResult2 == null ? Double.MAX_VALUE : raycastContext.getStart().squaredDistanceTo(blockHitResult2.getPos());

			return d <= e ? blockHitResult : blockHitResult2;
		}, raycastContext -> {
			final Vec3d vec3d = raycastContext.getStart().subtract(raycastContext.getEnd());
			return BlockHitResult.createMissed(raycastContext.getEnd(), Direction.getFacing(vec3d.x, vec3d.y, vec3d.z), new BlockPos(raycastContext.getEnd()));
		});
	}
}
