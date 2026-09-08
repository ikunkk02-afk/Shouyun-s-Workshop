package com.shouyun.workshop.item;

import com.shouyun.workshop.ShouyunWorkshop;
import com.shouyun.workshop.util.ModConstants;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public final class ModItems {
	public static final java.util.Map<NecromancerTier, NecromancerStaffItem> NECROMANCER_STAVES = registerStaves();
	private static java.util.Map<NecromancerTier, NecromancerStaffItem> registerStaves() {
		var items = new java.util.EnumMap<NecromancerTier, NecromancerStaffItem>(NecromancerTier.class);
		for (NecromancerTier tier : NecromancerTier.values()) {
			Item.Settings settings = new Item.Settings().maxDamage(tier.durability);
			if (tier == NecromancerTier.NETHERITE) settings.fireproof();
			items.put(tier, (NecromancerStaffItem) register(tier.id(), new NecromancerStaffItem(tier, settings)));
		}
		return java.util.Collections.unmodifiableMap(items);
	}
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
			NECROMANCER_STAVES.values().forEach(entries::add);
		});
	}

	private ModItems() {
	}
}
