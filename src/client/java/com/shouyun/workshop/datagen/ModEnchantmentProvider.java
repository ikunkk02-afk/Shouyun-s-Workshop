package com.shouyun.workshop.datagen;

import com.shouyun.workshop.enchantment.ModEnchantments;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.ItemTags;

import java.util.concurrent.CompletableFuture;

public final class ModEnchantmentProvider extends FabricDynamicRegistryProvider {
	public ModEnchantmentProvider(FabricDataOutput output,
			CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
		super(output, registriesFuture);
	}

	public static void bootstrap(Registerable<Enchantment> registerable) {
		RegistryEntryList<Item> swords = registerable.getRegistryLookup(RegistryKeys.ITEM)
				.getOrThrow(ItemTags.SWORD_ENCHANTABLE);
		Enchantment.Definition definition = Enchantment.definition(
				swords,
				swords,
				5,
				3,
				Enchantment.leveledCost(5, 8),
				Enchantment.leveledCost(20, 8),
				4,
				AttributeModifierSlot.MAINHAND);
		registerable.register(ModEnchantments.WHIRLWIND_SLASH,
				Enchantment.builder(definition).build(ModEnchantments.WHIRLWIND_SLASH.getValue()));
	}

	@Override
	protected void configure(RegistryWrapper.WrapperLookup registries, Entries entries) {
		entries.addAll(registries.getWrapperOrThrow(RegistryKeys.ENCHANTMENT));
	}

	@Override
	public String getName() {
		return "Shouyun Workshop Enchantments";
	}
}
