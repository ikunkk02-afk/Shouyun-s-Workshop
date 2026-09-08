package com.shouyun.workshop.summon;

import com.shouyun.workshop.entity.ModEntities;
import com.shouyun.workshop.entity.SummonedZombieEntity;
import com.shouyun.workshop.item.ModItems;
import com.shouyun.workshop.item.NecromancerStaffItem;
import com.shouyun.workshop.item.NecromancerTier;
import com.shouyun.workshop.network.NecromancerStatePayload;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.util.ActionResult;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import java.util.*;

public final class NecromancerSummons {
	private static final Map<UUID, State> STATES = new HashMap<>();
	private static final class State {
		int cooldown;
		final List<SummonedZombieEntity> summons = new ArrayList<>();
		boolean dirty = true;
	}
	public static void register() {
		AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
			if (!world.isClient && player instanceof ServerPlayerEntity serverPlayer
					&& !player.isSpectator() && entity instanceof LivingEntity target) {
				orderAttack(serverPlayer, target);
			}
			return ActionResult.PASS;
		});
		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> clear(handler.player, true));
		ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((player, origin, destination) -> clear(player, false));
		ServerEntityEvents.ENTITY_UNLOAD.register((entity, world) -> {
			if (entity instanceof SummonedZombieEntity zombie) {
				State state = STATES.get(zombie.ownerUuid());
				if (state != null && state.summons.remove(zombie)) state.dirty = true;
			}
		});
		ServerLifecycleEvents.SERVER_STOPPED.register(server -> STATES.clear());
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
				State state = STATES.get(player.getUuid());
				if (state == null) continue;
				if (state.cooldown > 0 && --state.cooldown == 0) state.dirty = true;
				for (var zombie : List.copyOf(state.summons)) {
					if (!player.isAlive() || !zombie.isAlive() || zombie.isRemoved()
							|| zombie.getWorld() != player.getWorld() || zombie.remainingTicks() == 0) {
						state.summons.remove(zombie);
						if (!zombie.isRemoved()) zombie.discard();
						state.dirty = true;
					}
				}
				if (state.dirty || server.getTicks() % 20 == 0) sync(player, state);
				if (state.cooldown == 0 && state.summons.isEmpty()) STATES.remove(player.getUuid());
			}
		});
	}

	public static boolean summon(ServerPlayerEntity player, Hand hand, NecromancerTier tier) {
		var stack = player.getStackInHand(hand);
		if (!(stack.getItem() instanceof NecromancerStaffItem staff) || staff.tier() != tier || !player.isAlive() || player.isSpectator()) return false;
		State state = STATES.computeIfAbsent(player.getUuid(), ignored -> new State());
		state.summons.removeIf(zombie -> zombie.isRemoved() || !zombie.isAlive());
		if (state.cooldown > 0) return fail(player, "cooldown");
		if (state.summons.size() >= tier.limit) return fail(player, "limit");
		var world = player.getServerWorld();
		var zombie = new SummonedZombieEntity(ModEntities.SUMMONED_ZOMBIE, world);
		var point = SummonPlacement.find(world, player, zombie);
		if (point == null) return fail(player, "blocked");
		zombie.refreshPositionAndAngles(point.x, point.y, point.z, player.getYaw(), 0);
		zombie.bind(player, tier);
		if (!world.spawnEntity(zombie)) return fail(player, "blocked");
		state.summons.add(zombie);
		state.cooldown = tier.cooldownSeconds * 20;
		for (var item : ModItems.NECROMANCER_STAVES.values()) player.getItemCooldownManager().set(item, state.cooldown);
		// Vanilla damage handling preserves creative-mode immunity and break animations.
		stack.damage(1, player, LivingEntity.getSlotForHand(hand));
		world.spawnParticles(ParticleTypes.SOUL, point.x, point.y + 1, point.z, 12, .3, .5, .3, .02);
		world.playSound(null, point.x, point.y, point.z, SoundEvents.ENTITY_EVOKER_CAST_SPELL, SoundCategory.PLAYERS, .6F, .8F);
		sync(player, state);
		return true;
	}

	public static int count(ServerPlayerEntity player) {
		State state = STATES.get(player.getUuid());
		return state == null ? 0 : (int) state.summons.stream().filter(z -> z.isAlive() && !z.isRemoved()).count();
	}
	public static int orderAttack(ServerPlayerEntity player, LivingEntity target) {
		State state = STATES.get(player.getUuid());
		if (state == null) return 0;
		state.summons.removeIf(zombie -> zombie.isRemoved() || !zombie.isAlive());
		int ordered = 0;
		for (var zombie : state.summons) if (zombie.orderAttack(target)) ordered++;
		return ordered;
	}
	private static boolean fail(ServerPlayerEntity player, String reason) {
		player.sendMessage(Text.translatable("message.shouyun_workshop.staff." + reason), true);
		return false;
	}
	private static void clear(ServerPlayerEntity player, boolean forget) {
		State state = STATES.get(player.getUuid());
		if (state == null) return;
		for (var zombie : List.copyOf(state.summons)) zombie.discard();
		state.summons.clear();
		state.dirty = true;
		if (forget) STATES.remove(player.getUuid()); else sync(player, state);
	}
	private static void sync(ServerPlayerEntity player, State state) {
		if (ServerPlayNetworking.canSend(player, NecromancerStatePayload.ID)) {
			ServerPlayNetworking.send(player, new NecromancerStatePayload(state.cooldown, state.summons.stream()
					.map(z -> new NecromancerStatePayload.Entry(z.getUuid(), z.tier().ordinal(), z.remainingTicks())).toList()));
		}
		state.dirty = false;
	}
	private NecromancerSummons() {}
}
