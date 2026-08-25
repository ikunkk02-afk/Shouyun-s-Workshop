package com.shouyun.workshop.entity;

import com.shouyun.workshop.util.CombatTargeting;
import com.shouyun.workshop.util.ModConstants;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public final class SwordQiEntity extends ProjectileEntity {
	private float damage;
	private Vec3d origin;

	public SwordQiEntity(EntityType<? extends SwordQiEntity> type, World world) {
		super(type, world);
		this.origin = Vec3d.ZERO;
	}

	public SwordQiEntity(ServerWorld world, ServerPlayerEntity owner, float damage) {
		this(ModEntities.SWORD_QI, world);
		this.damage = damage;
		this.setOwner(owner);
		Vec3d direction = owner.getRotationVec(1.0F).normalize();
		Vec3d start = owner.getEyePos().add(direction.multiply(0.65));
		this.setPosition(start);
		this.origin = start;
		this.setVelocity(direction.multiply(ModConstants.SWORD_QI_SPEED));
	}

	@Override
	public void tick() {
		super.tick();
		if (this.origin.equals(Vec3d.ZERO)) {
			this.origin = this.getPos();
		}

		if (!this.getWorld().isClient()) {
			HitResult hitResult = ProjectileUtil.getCollision(this, this::canHit);
			if (hitResult.getType() != HitResult.Type.MISS) {
				this.hitOrDeflect(hitResult);
			}
		}

		if (this.isRemoved()) {
			return;
		}

		Vec3d velocity = this.getVelocity();
		this.move(MovementType.SELF, velocity);
		this.updateRotation();

		if (this.getWorld() instanceof ServerWorld serverWorld) {
			serverWorld.spawnParticles(ParticleTypes.SWEEP_ATTACK, this.getX(), this.getY(), this.getZ(),
					ModConstants.SWORD_QI_PARTICLES_PER_TICK, 0.12, 0.12, 0.12, 0.0);
			serverWorld.spawnParticles(ParticleTypes.CLOUD, this.getX(), this.getY(), this.getZ(),
					1, 0.05, 0.05, 0.05, 0.0);
		}

		if (this.age >= ModConstants.SWORD_QI_LIFETIME_TICKS
				|| this.squaredDistanceTo(this.origin) >= ModConstants.SWORD_QI_MAX_DISTANCE * ModConstants.SWORD_QI_MAX_DISTANCE) {
			this.discard();
		}
	}

	@Override
	protected boolean canHit(Entity entity) {
		if (!super.canHit(entity) || !(entity instanceof LivingEntity living)) {
			return false;
		}
		return !(this.getOwner() instanceof ServerPlayerEntity owner) || CombatTargeting.canAffect(owner, living);
	}

	@Override
	protected void onEntityHit(EntityHitResult hitResult) {
		if (this.getWorld() instanceof ServerWorld world
				&& this.getOwner() instanceof ServerPlayerEntity owner
				&& hitResult.getEntity() instanceof LivingEntity living
				&& CombatTargeting.canAffect(owner, living)) {
			living.damage(world.getDamageSources().playerAttack(owner), this.damage);
		}
		this.discard();
	}

	@Override
	protected void onCollision(HitResult hitResult) {
		super.onCollision(hitResult);
		if (hitResult.getType() != HitResult.Type.MISS) {
			this.discard();
		}
	}

	@Override
	protected void initDataTracker(DataTracker.Builder builder) {
	}

	@Override
	protected void readCustomDataFromNbt(NbtCompound nbt) {
	}

	@Override
	protected void writeCustomDataToNbt(NbtCompound nbt) {
	}

	@Override
	public boolean shouldSave() {
		return false;
	}
}
