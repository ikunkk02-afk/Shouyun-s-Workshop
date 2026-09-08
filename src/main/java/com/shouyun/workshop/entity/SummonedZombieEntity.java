package com.shouyun.workshop.entity;

import com.shouyun.workshop.item.NecromancerTier;
import com.shouyun.workshop.summon.SummonPlacement;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import java.util.UUID;
import java.util.EnumSet;
import java.util.Comparator;

public final class SummonedZombieEntity extends ZombieEntity {
	private UUID ownerUuid;
	private NecromancerTier tier = NecromancerTier.WOODEN;
	private long summonedAt, expiresAt;
	private LivingEntity orderedTarget;
	private long attackOrderExpiresAt;

	public SummonedZombieEntity(EntityType<? extends ZombieEntity> type, World world) {
		super(type, world);
		setBaby(false);
		setCanPickUpLoot(false);
		setPersistent();
	}

	public void bind(ServerPlayerEntity owner, NecromancerTier tier) {
		this.ownerUuid = owner.getUuid();
		this.tier = tier;
		this.summonedAt = owner.getServerWorld().getTime();
		this.expiresAt = summonedAt + tier.lifetimeSeconds * 20L;
		tier.equip(this);
		addCommandTag("shouyun_workshop_summon");
	}
	public UUID ownerUuid() { return ownerUuid; }
	public NecromancerTier tier() { return tier; }
	public long summonedAt() { return summonedAt; }
	public int remainingTicks() { return (int) Math.max(0, expiresAt - getWorld().getTime()); }
	public ServerPlayerEntity owner() {
		return ownerUuid == null || getServer() == null ? null : getServer().getPlayerManager().getPlayer(ownerUuid);
	}

	public boolean canFight(LivingEntity target) {
		ServerPlayerEntity owner = owner();
		return canAttackForOwner(owner, target)
				&& (target instanceof Monster || target == orderedTarget && getWorld().getTime() < attackOrderExpiresAt)
				&& target.squaredDistanceTo(owner) <= (target == orderedTarget ? 32 * 32 : 16 * 16);
	}

	private boolean canAttackForOwner(ServerPlayerEntity owner, LivingEntity target) {
		return owner != null && target != null && target.isAlive() && !target.isSpectator() && target.canTakeDamage()
				&& target != owner && !(target instanceof SummonedZombieEntity)
				&& !(target instanceof PlayerEntity) && !(target instanceof TameableEntity)
				&& !owner.isTeammate(target) && target.getWorld() == owner.getWorld();
	}

	public boolean orderAttack(LivingEntity target) {
		ServerPlayerEntity owner = owner();
		if (!canAttackForOwner(owner, target) || target.squaredDistanceTo(owner) > 32 * 32) return false;
		orderedTarget = target;
		attackOrderExpiresAt = getWorld().getTime() + 200;
		setTarget(target);
		getNavigation().stop();
		return true;
	}

	@Override protected void initGoals() {
		goalSelector.add(0, new SwimGoal(this));
		goalSelector.add(2, new MeleeAttackGoal(this, 1.1, false));
		goalSelector.add(3, new FollowOwnerGoal());
		goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 8));
		goalSelector.add(8, new LookAroundGoal(this));
	}

	@Override public void tick() {
		if (!getWorld().isClient) {
			ServerPlayerEntity owner = owner();
			if (owner == null || !owner.isAlive() || owner.getWorld() != getWorld() || remainingTicks() <= 0) {
				discard();
				return;
			}
			if (orderedTarget != null && (!canFight(orderedTarget) || getWorld().getTime() >= attackOrderExpiresAt)) {
				orderedTarget = null;
				attackOrderExpiresAt = 0;
			}
			if (getTarget() != null && !canFight(getTarget())) setTarget(null);
			if (age % 20 == 0 && orderedTarget == null) {
				var targets = getWorld().getEntitiesByClass(LivingEntity.class, owner.getBoundingBox().expand(16),
						target -> canFight(target) && canSee(target));
				targets.sort(Comparator.<LivingEntity>comparingInt(target -> target instanceof MobEntity mob && mob.getTarget() == owner ? 0 : 1)
						.thenComparingDouble(this::squaredDistanceTo));
				setTarget(targets.isEmpty() ? null : targets.getFirst());
			}
		}
		super.tick();
	}

	@Override public boolean tryAttack(Entity target) {
		return target instanceof LivingEntity living && canFight(living) && super.tryAttack(target);
	}
	@Override protected boolean burnsInDaylight() { return false; }
	@Override protected boolean isDisallowedInPeaceful() { return false; }
	@Override public boolean shouldDropXp() { return false; }
	@Override protected boolean canConvertInWater() { return false; }
	@Override protected void dropLoot(DamageSource source, boolean causedByPlayer) {}
	@Override protected void dropEquipment(ServerWorld world, DamageSource source, boolean causedByPlayer) {}
	@Override protected int getXpToDrop() { return 0; }

	private final class FollowOwnerGoal extends Goal {
		FollowOwnerGoal() { setControls(EnumSet.of(Control.MOVE, Control.LOOK)); }
		@Override public boolean canStart() { return getTarget() == null && owner() != null && squaredDistanceTo(owner()) > 16; }
		@Override public boolean shouldContinue() { return getTarget() == null && owner() != null && squaredDistanceTo(owner()) > 6.25; }
		@Override public void start() { moveToOwner(); }
		@Override public void stop() { getNavigation().stop(); }
		@Override public void tick() {
			var owner = owner();
			if (owner == null) return;
			getLookControl().lookAt(owner, 10, getMaxLookPitchChange());
			if (age % 5 == 0) moveToOwner();
		}
		private void moveToOwner() {
			var owner = owner();
			if (owner == null) return;
			if (squaredDistanceTo(owner) > 24 * 24) {
				var point = SummonPlacement.find((ServerWorld) getWorld(), owner, SummonedZombieEntity.this);
				if (point != null) { refreshPositionAndAngles(point.x, point.y, point.z, getYaw(), getPitch()); getNavigation().stop(); return; }
			}
			getNavigation().startMovingTo(owner, 1.15);
		}
	}
}
