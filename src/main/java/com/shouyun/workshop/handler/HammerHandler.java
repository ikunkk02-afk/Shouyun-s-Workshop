package com.shouyun.workshop.handler;

import com.shouyun.workshop.util.CombatTargeting;
import com.shouyun.workshop.util.ModConstants;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class HammerHandler {
	private static final Set<UUID> BLOCK_DESTRUCTION_ENABLED = new HashSet<>();

	public static void shatterGlassHammer(ServerPlayerEntity attacker, LivingEntity primaryTarget, ItemStack stack) {
		ServerWorld world = attacker.getServerWorld();
		Vec3d center = primaryTarget.getPos().add(0.0, primaryTarget.getHeight() * 0.5, 0.0);

		world.playSound(null, center.x, center.y, center.z, SoundEvents.BLOCK_GLASS_BREAK,
				SoundCategory.PLAYERS, 1.25F, 0.85F);
		world.spawnParticles(new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.GLASS.getDefaultState()),
				center.x, center.y, center.z, ModConstants.GLASS_SHARD_PARTICLES, 0.9, 0.7, 0.9, 0.18);

		Set<Integer> affected = new HashSet<>();
		affected.add(primaryTarget.getId());
		affected.add(attacker.getId());
		attacker.damage(world.getDamageSources().generic(), ModConstants.GLASS_SHARD_SELF_DAMAGE);
		for (LivingEntity entity : livingEntitiesAround(world, center, ModConstants.GLASS_SHARD_RADIUS)) {
			if (!affected.add(entity.getId())) {
				continue;
			}
			if (CombatTargeting.canAffect(attacker, entity)) {
				entity.damage(world.getDamageSources().playerAttack(attacker), ModConstants.GLASS_SHARD_DAMAGE);
			}
		}

		attacker.sendEquipmentBreakStatus(stack.getItem(), EquipmentSlot.MAINHAND);
		stack.decrement(1);
	}

	public static boolean tryCreateNetheriteShockwave(ServerPlayerEntity attacker, LivingEntity primaryTarget,
			ItemStack stack) {
		if (attacker.getItemCooldownManager().isCoolingDown(stack.getItem())) {
			return false;
		}

		ServerWorld world = attacker.getServerWorld();
		Vec3d center = primaryTarget.getPos();
		world.playSound(null, center.x, center.y, center.z, SoundEvents.ITEM_MACE_SMASH_GROUND_HEAVY,
				SoundCategory.PLAYERS, 1.15F, 0.85F);
		world.playSound(null, center.x, center.y, center.z, SoundEvents.ENTITY_GENERIC_EXPLODE,
				SoundCategory.PLAYERS, 0.8F, 1.15F);
		world.spawnParticles(ParticleTypes.EXPLOSION, center.x, center.y + 0.4, center.z,
				ModConstants.NETHERITE_BLAST_PARTICLES, 0.55, 0.35, 0.55, 0.02);
		world.spawnParticles(ParticleTypes.GUST_EMITTER_SMALL, center.x, center.y + 0.15, center.z,
				3, 0.45, 0.05, 0.45, 0.0);
		world.spawnParticles(ParticleTypes.CLOUD, center.x, center.y + 0.15, center.z,
				ModConstants.NETHERITE_SHOCKWAVE_PARTICLES, 1.8, 0.2, 1.8, 0.12);
		world.spawnParticles(ParticleTypes.SMOKE, attacker.getX(), attacker.getBodyY(0.7), attacker.getZ(),
				6, 0.25, 0.2, 0.25, 0.02);
		world.spawnParticles(ParticleTypes.CRIT, attacker.getX(), attacker.getBodyY(0.7), attacker.getZ(),
				4, 0.2, 0.15, 0.2, 0.05);

		Set<Integer> affected = new HashSet<>();
		affected.add(primaryTarget.getId());
		affected.add(attacker.getId());
		for (LivingEntity entity : livingEntitiesAround(world, center, ModConstants.NETHERITE_SHOCKWAVE_RADIUS)) {
			if (!affected.add(entity.getId()) || !CombatTargeting.canAffect(attacker, entity)) {
				continue;
			}
			boolean inBlast = entity.squaredDistanceTo(center)
					<= ModConstants.NETHERITE_BLAST_RADIUS * ModConstants.NETHERITE_BLAST_RADIUS;
			float damage = ModConstants.NETHERITE_SHOCKWAVE_DAMAGE
					+ (inBlast ? ModConstants.NETHERITE_BLAST_BONUS_DAMAGE : 0.0F);
			entity.damage(inBlast
						? world.getDamageSources().explosion(attacker, attacker)
						: world.getDamageSources().playerAttack(attacker), damage);

			Vec3d horizontal = entity.getPos().subtract(center).multiply(1.0, 0.0, 1.0);
			if (horizontal.lengthSquared() < 1.0E-6) {
				horizontal = attacker.getRotationVec(1.0F).multiply(-1.0, 0.0, -1.0);
			}
			horizontal = horizontal.normalize();
			double distance = Math.min(Math.sqrt(entity.squaredDistanceTo(center)),
					ModConstants.NETHERITE_SHOCKWAVE_RADIUS);
			double falloff = ModConstants.NETHERITE_MIN_KNOCKBACK_MULTIPLIER
					+ (1.0 - ModConstants.NETHERITE_MIN_KNOCKBACK_MULTIPLIER)
					* (1.0 - distance / ModConstants.NETHERITE_SHOCKWAVE_RADIUS);
			entity.takeKnockback(ModConstants.NETHERITE_HORIZONTAL_KNOCKBACK * falloff,
					-horizontal.x, -horizontal.z);
			double resistance = Math.clamp(entity.getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE), 0.0, 1.0);
			entity.addVelocity(0.0,
					ModConstants.NETHERITE_VERTICAL_KNOCKBACK * falloff * (1.0 - resistance), 0.0);
			entity.velocityModified = true;
		}
		if (isBlockDestructionEnabled(attacker)) {
			breakBlocksAround(world, attacker, center);
		}

		attacker.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS,
				ModConstants.NETHERITE_HAMMER_NUMB_DURATION_TICKS,
				ModConstants.NETHERITE_HAMMER_WEAKNESS_AMPLIFIER));
		attacker.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE,
				ModConstants.NETHERITE_HAMMER_NUMB_DURATION_TICKS,
				ModConstants.NETHERITE_HAMMER_MINING_FATIGUE_AMPLIFIER));
		attacker.getItemCooldownManager().set(stack.getItem(), ModConstants.NETHERITE_HAMMER_COOLDOWN_TICKS);
		return true;
	}

	public static boolean toggleBlockDestruction(ServerPlayerEntity player) {
		boolean enabled;
		if (BLOCK_DESTRUCTION_ENABLED.remove(player.getUuid())) {
			enabled = false;
		} else {
			BLOCK_DESTRUCTION_ENABLED.add(player.getUuid());
			enabled = true;
		}
		player.sendMessage(Text.translatable(enabled
				? "message.shouyun_workshop.hammer_block_blast.enabled"
				: "message.shouyun_workshop.hammer_block_blast.disabled"), true);
		return enabled;
	}

	public static boolean isBlockDestructionEnabled(ServerPlayerEntity player) {
		return BLOCK_DESTRUCTION_ENABLED.contains(player.getUuid());
	}

	public static void clearPlayer(ServerPlayerEntity player) {
		BLOCK_DESTRUCTION_ENABLED.remove(player.getUuid());
	}

	private static void breakBlocksAround(ServerWorld world, ServerPlayerEntity attacker, Vec3d center) {
		double radius = ModConstants.NETHERITE_BLOCK_BLAST_RADIUS;
		int blockRadius = (int) Math.ceil(radius);
		BlockPos origin = BlockPos.ofFloored(center);
		for (BlockPos pos : BlockPos.iterate(origin.add(-blockRadius, -blockRadius, -blockRadius),
				origin.add(blockRadius, blockRadius, blockRadius))) {
			if (Vec3d.ofCenter(pos).squaredDistanceTo(center) > radius * radius
					|| !world.isInBuildLimit(pos)
					|| !attacker.canModifyAt(world, pos)) {
				continue;
			}
			var state = world.getBlockState(pos);
			if (!state.isAir() && state.getHardness(world, pos) >= 0.0F) {
				world.breakBlock(pos, true, attacker, 512);
			}
		}
	}

	private static Iterable<LivingEntity> livingEntitiesAround(ServerWorld world, Vec3d center, double radius) {
		Box box = new Box(center, center).expand(radius);
		return world.getEntitiesByClass(LivingEntity.class, box,
				entity -> entity.squaredDistanceTo(center) <= radius * radius);
	}

	private HammerHandler() {
	}
}
