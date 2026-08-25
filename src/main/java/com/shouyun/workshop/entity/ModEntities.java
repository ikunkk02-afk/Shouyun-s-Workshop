package com.shouyun.workshop.entity;

import com.shouyun.workshop.ShouyunWorkshop;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public final class ModEntities {
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
		// Class loading performs the registry registration.
	}

	private ModEntities() {
	}
}
