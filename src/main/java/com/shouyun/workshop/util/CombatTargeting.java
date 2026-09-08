package com.shouyun.workshop.util;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

public final class CombatTargeting {
	public static boolean canAffect(ServerPlayerEntity attacker, LivingEntity target) {
		if (target instanceof com.shouyun.workshop.entity.SummonedZombieEntity summon
				&& (attacker.getUuid().equals(summon.ownerUuid())
						|| summon.owner() != null && attacker.isTeammate(summon.owner()))) return false;
		if (target == attacker || !target.isAlive() || !target.canTakeDamage() || target.isSpectator()) {
			return false;
		}
		if (target instanceof ArmorStandEntity armorStand && armorStand.isMarker()) {
			return false;
		}
		if (attacker.isTeammate(target)) {
			return false;
		}
		if (target instanceof TameableEntity tameable && tameable.isOwner(attacker)) {
			return false;
		}
		return !(target instanceof PlayerEntity player) || attacker.shouldDamagePlayer(player);
	}

	private CombatTargeting() {
	}
}
