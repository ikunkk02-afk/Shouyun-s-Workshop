package com.shouyun.workshop.entity;

import com.shouyun.workshop.ShouyunWorkshop;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public final class ModEntities {
	public static final EntityType<SummonedZombieEntity> SUMMONED_ZOMBIE = Registry.register(
			Registries.ENTITY_TYPE, ShouyunWorkshop.id("summoned_zombie"),
			EntityType.Builder.<SummonedZombieEntity>create(SummonedZombieEntity::new, SpawnGroup.CREATURE)
					.dimensions(.6F, 1.95F).maxTrackingRange(8).disableSaving().build("summoned_zombie"));
	public static final EntityType<SwordQiEntity> SWORD_QI = Registry.register(
			Registries.ENTITY_TYPE,
			ShouyunWorkshop.id("sword_qi"),
			EntityType.Builder.<SwordQiEntity>create(SwordQiEntity::new, SpawnGroup.MISC)
					.dimensions(0.35F, 0.35F)
					.maxTrackingRange(8)
					.trackingTickInterval(1)
					.disableSaving()
					.build("sword_qi"));

	public static void register() {
		net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry.register(
				SUMMONED_ZOMBIE, net.minecraft.entity.mob.ZombieEntity.createZombieAttributes());
		// Class loading performs the registry registration.
	}

	private ModEntities() {
	}
}
