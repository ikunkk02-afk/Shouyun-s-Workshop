package com.shouyun.workshop.handler;

import com.shouyun.workshop.util.CombatTargeting;
import com.shouyun.workshop.util.ModConstants;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.HashSet;
import java.util.Set;

public final class HammerHandler {
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

	public static void createNetheriteShockwave(ServerPlayerEntity attacker, LivingEntity primaryTarget) {
		ServerWorld world = attacker.getServerWorld();
		Vec3d center = primaryTarget.getPos();
		world.playSound(null, center.x, center.y, center.z, SoundEvents.ITEM_MACE_SMASH_GROUND_HEAVY,
				SoundCategory.PLAYERS, 1.15F, 0.85F);
		world.spawnParticles(ParticleTypes.GUST_EMITTER_SMALL, center.x, center.y + 0.15, center.z,
				3, 0.45, 0.05, 0.45, 0.0);
		world.spawnParticles(ParticleTypes.CLOUD, center.x, center.y + 0.15, center.z,
				ModConstants.NETHERITE_SHOCKWAVE_PARTICLES, 1.8, 0.2, 1.8, 0.12);

		Set<Integer> affected = new HashSet<>();
		affected.add(primaryTarget.getId());
		affected.add(attacker.getId());
		for (LivingEntity entity : livingEntitiesAround(world, center, ModConstants.NETHERITE_SHOCKWAVE_RADIUS)) {
			if (!affected.add(entity.getId()) || !CombatTargeting.canAffect(attacker, entity)) {
				continue;
			}
			entity.damage(world.getDamageSources().playerAttack(attacker), ModConstants.NETHERITE_SHOCKWAVE_DAMAGE);

			Vec3d horizontal = entity.getPos().subtract(center).multiply(1.0, 0.0, 1.0);
			if (horizontal.lengthSquared() < 1.0E-6) {
				horizontal = attacker.getRotationVec(1.0F).multiply(-1.0, 0.0, -1.0);
			}
			horizontal = horizontal.normalize();
			entity.takeKnockback(ModConstants.NETHERITE_HORIZONTAL_KNOCKBACK, -horizontal.x, -horizontal.z);
			double resistance = Math.clamp(entity.getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE), 0.0, 1.0);
			entity.addVelocity(0.0, ModConstants.NETHERITE_VERTICAL_KNOCKBACK * (1.0 - resistance), 0.0);
			entity.velocityModified = true;
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
