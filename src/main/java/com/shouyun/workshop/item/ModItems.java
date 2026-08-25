package com.shouyun.workshop.item;

import com.shouyun.workshop.ShouyunWorkshop;
import com.shouyun.workshop.util.ModConstants;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public final class ModItems {
	public static final Item GLASS_HAMMER = register("glass_hammer",
			new GlassHammerItem(new Item.Settings()
					.maxDamage(1)
					.attributeModifiers(HammerItem.createAttributeModifiers(
							ModConstants.GLASS_HAMMER_TOTAL_DAMAGE, ModConstants.HAMMER_ATTACK_SPEED))));

	public static final Item NETHERITE_HAMMER = register("netherite_hammer",
			new NetheriteHammerItem(new Item.Settings()
					.maxDamage(ModConstants.NETHERITE_HAMMER_DURABILITY)
					.fireproof()
					.attributeModifiers(HammerItem.createAttributeModifiers(
							ModConstants.NETHERITE_HAMMER_TOTAL_DAMAGE, ModConstants.HAMMER_ATTACK_SPEED))));

	private static Item register(String path, Item item) {
		return Registry.register(Registries.ITEM, ShouyunWorkshop.id(path), item);
	}

	public static void register() {
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(entries -> {
			entries.add(GLASS_HAMMER);
			entries.add(NETHERITE_HAMMER);
		});
	}

	private ModItems() {
	}
}
