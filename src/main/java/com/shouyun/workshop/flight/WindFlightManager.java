package com.shouyun.workshop.flight;

import com.shouyun.workshop.enchantment.ModEnchantments;
import com.shouyun.workshop.network.WindFlightInputPayload;
import com.shouyun.workshop.util.ModConstants;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class WindFlightManager {
	private static final Map<UUID, WindState> STATES = new HashMap<>();

	public static void register() {
		ServerTickEvents.END_SERVER_TICK.register(WindFlightManager::tickServer);
		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> exit(handler.player));
		ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((player, origin, destination) -> exit(player));
	}

	public static void updateInput(ServerPlayerEntity player, byte rawFlags) {
		WindState state = STATES.get(player.getUuid());
		if (state != null && state.active) {
			state.inputFlags = (byte) (rawFlags & WindFlightInputPayload.MOVEMENT_MASK);
		}
	}

	public static void toggle(ServerPlayerEntity player) {
		WindState current = STATES.get(player.getUuid());
		if (current != null && current.active) {
			exit(player);
			return;
		}
		if (!canStart(player)) {
			return;
		}

		WindState state = new WindState();
		state.active = true;
		state.previousNoGravity = player.hasNoGravity();
		STATES.put(player.getUuid(), state);
		player.setNoGravity(true);
		Vec3d velocity = player.getVelocity();
		player.setVelocity(velocity.x, ModConstants.WIND_HOVER_VERTICAL_SPEED, velocity.z);
		player.velocityModified = true;
		player.fallDistance = 0.0F;
	}

	public static boolean isFlying(ServerPlayerEntity player) {
		WindState state = STATES.get(player.getUuid());
		return state != null && state.active;
	}

	private static void tickServer(MinecraftServer server) {
		for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
			WindState state = STATES.get(player.getUuid());
			if (state != null && state.active) {
				tickPlayer(player, state);
			}
		}
	}

	private static void tickPlayer(ServerPlayerEntity player, WindState state) {
		if (!canContinue(player)) {
			exit(player);
			return;
		}
		tickActive(player, state);
	}

	private static void tickActive(ServerPlayerEntity player, WindState state) {
		if (++state.hungerTicks >= ModConstants.WIND_HUNGER_INTERVAL_TICKS) {
			if (player.getHungerManager().getFoodLevel() < ModConstants.WIND_HUNGER_COST) {
				exit(player);
				return;
			}
			player.getHungerManager().setFoodLevel(
					player.getHungerManager().getFoodLevel() - ModConstants.WIND_HUNGER_COST);
			state.hungerTicks = 0;
		}

		double yaw = Math.toRadians(player.getYaw());
		Vec3d forward = new Vec3d(-Math.sin(yaw), 0.0, Math.cos(yaw));
		Vec3d right = new Vec3d(-Math.cos(yaw), 0.0, -Math.sin(yaw));
		double forwardInput = flag(state.inputFlags, WindFlightInputPayload.FORWARD)
				- flag(state.inputFlags, WindFlightInputPayload.BACKWARD);
		double sideInput = flag(state.inputFlags, WindFlightInputPayload.RIGHT)
				- flag(state.inputFlags, WindFlightInputPayload.LEFT);
		double verticalInput = flag(state.inputFlags, WindFlightInputPayload.ASCEND)
				- flag(state.inputFlags, WindFlightInputPayload.DESCEND);
		Vec3d horizontal = forward.multiply(forwardInput).add(right.multiply(sideInput));
		if (horizontal.lengthSquared() > 1.0) {
			horizontal = horizontal.normalize();
		}
		Vec3d target = new Vec3d(horizontal.x * ModConstants.WIND_HORIZONTAL_SPEED,
				verticalInput * ModConstants.WIND_VERTICAL_SPEED,
				horizontal.z * ModConstants.WIND_HORIZONTAL_SPEED);
		Vec3d smoothed = player.getVelocity().lerp(target, ModConstants.WIND_VELOCITY_SMOOTHING);
		smoothed = new Vec3d(smoothed.x, target.y, smoothed.z);
		player.setVelocity(smoothed);
		player.velocityModified = true;
		player.fallDistance = 0.0F;

		if (player.age % ModConstants.WIND_PARTICLE_INTERVAL_TICKS == 0) {
			ServerWorld world = player.getServerWorld();
			world.spawnParticles(ParticleTypes.CLOUD, player.getX(), player.getBodyY(0.35), player.getZ(),
					5, 0.45, 0.25, 0.45, 0.02);
			world.spawnParticles(ParticleTypes.SWEEP_ATTACK, player.getX(), player.getBodyY(0.5), player.getZ(),
					1, 0.35, 0.15, 0.35, 0.0);
		}
	}

	private static boolean canStart(ServerPlayerEntity player) {
		return hasWhirlwind(player)
				&& player.getHungerManager().getFoodLevel() >= ModConstants.WIND_HUNGER_COST
				&& baseStateValid(player);
	}

	private static boolean canContinue(ServerPlayerEntity player) {
		return hasWhirlwind(player) && baseStateValid(player);
	}

	private static boolean baseStateValid(ServerPlayerEntity player) {
		return player.isAlive()
				&& !player.isSpectator()
				&& !player.hasVehicle()
				&& !player.isSleeping()
				&& !player.isSwimming()
				&& !player.isFallFlying()
				&& !player.isTouchingWater()
				&& !player.isInLava();
	}

	private static boolean hasWhirlwind(ServerPlayerEntity player) {
		ItemStack stack = player.getMainHandStack();
		return stack.isIn(ItemTags.SWORDS)
				&& ModEnchantments.getWhirlwindLevel(player.getServerWorld(), stack) > 0;
	}

	public static void exit(ServerPlayerEntity player) {
		WindState state = STATES.remove(player.getUuid());
		if (state == null || !state.active) {
			return;
		}
		player.setNoGravity(state.previousNoGravity);
		player.fallDistance = 0.0F;
		Vec3d velocity = player.getVelocity();
		player.setVelocity(velocity.x * 0.5, 0.0, velocity.z * 0.5);
		player.velocityModified = true;
	}

	private static int flag(byte flags, int mask) {
		return (flags & mask) != 0 ? 1 : 0;
	}

	private static final class WindState {
		private int hungerTicks;
		private byte inputFlags;
		private boolean active;
		private boolean previousNoGravity;
	}

	private WindFlightManager() {
	}
}
