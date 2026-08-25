package com.shouyun.workshop.handler;

import com.shouyun.workshop.enchantment.ModEnchantments;
import com.shouyun.workshop.entity.SwordQiEntity;
import com.shouyun.workshop.util.CombatTargeting;
import com.shouyun.workshop.util.ModConstants;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public final class WhirlwindHandler {
	public static void register() {
		UseItemCallback.EVENT.register((player, world, hand) -> {
			ItemStack stack = player.getStackInHand(hand);
			int level = ModEnchantments.getWhirlwindLevel(world, stack);
			if (level <= 0) {
				return TypedActionResult.pass(stack);
			}
			if (!(player instanceof ServerPlayerEntity serverPlayer)) {
				return TypedActionResult.success(stack, true);
			}
			if (serverPlayer.getItemCooldownManager().isCoolingDown(stack.getItem())) {
				return TypedActionResult.fail(stack);
			}

			activate(serverPlayer, level);
			serverPlayer.getItemCooldownManager().set(stack.getItem(), ModConstants.WHIRLWIND_COOLDOWN_TICKS);
			serverPlayer.swingHand(hand, true);
			return TypedActionResult.success(stack, false);
		});
	}

	public static void activate(ServerPlayerEntity player, int rawLevel) {
		int level = Math.clamp(rawLevel, 1, 3);
		ServerWorld world = player.getServerWorld();
		Vec3d center = player.getPos().add(0.0, player.getHeight() * 0.5, 0.0);
		double radius = ModConstants.WHIRLWIND_RADII[level];

		for (LivingEntity target : world.getEntitiesByClass(LivingEntity.class,
				new Box(center, center).expand(radius),
				entity -> entity.squaredDistanceTo(center) <= radius * radius)) {
			if (CombatTargeting.canAffect(player, target)) {
				target.damage(world.getDamageSources().playerAttack(player), ModConstants.WHIRLWIND_DAMAGE[level]);
			}
		}

		spawnRing(world, center, radius, ModConstants.WHIRLWIND_RING_PARTICLES[level]);
		world.playSound(null, center.x, center.y, center.z, SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP,
				SoundCategory.PLAYERS, 1.15F, 0.85F + level * 0.08F);

		if (level >= 2) {
			SwordQiEntity swordQi = new SwordQiEntity(world, player, ModConstants.SWORD_QI_DAMAGE[level]);
			world.spawnEntity(swordQi);
		}
	}

	private static void spawnRing(ServerWorld world, Vec3d center, double radius, int count) {
		for (int i = 0; i < count; i++) {
			double angle = Math.PI * 2.0 * i / count;
			double x = center.x + Math.cos(angle) * radius;
			double z = center.z + Math.sin(angle) * radius;
			world.spawnParticles(ParticleTypes.SWEEP_ATTACK, x, center.y, z, 1, 0.0, 0.08, 0.0, 0.0);
		}
	}

	private WhirlwindHandler() {
	}
}
