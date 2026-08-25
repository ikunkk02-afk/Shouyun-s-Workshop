package com.shouyun.workshop.datagen;

import com.shouyun.workshop.enchantment.ModEnchantments;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.EnchantmentTags;

import java.util.concurrent.CompletableFuture;

public final class ModEnchantmentTagProvider extends FabricTagProvider.EnchantmentTagProvider {
	public ModEnchantmentTagProvider(FabricDataOutput output,
			CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
		super(output, registriesFuture);
	}

	@Override
	protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
		getOrCreateTagBuilder(EnchantmentTags.NON_TREASURE).add(ModEnchantments.WHIRLWIND_SLASH);
	}
}
