package com.shouyun.workshop.datagen;

import com.shouyun.workshop.ShouyunWorkshop;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.registry.RegistryBuilder;
import net.minecraft.registry.RegistryKeys;

public final class ShouyunWorkshopDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator generator) {
		FabricDataGenerator.Pack pack = generator.createPack();
		pack.addProvider(ModRecipeProvider::new);
		pack.addProvider(ModEnglishLanguageProvider::new);
		pack.addProvider(ModChineseLanguageProvider::new);
		pack.addProvider(ModEnchantmentProvider::new);
		pack.addProvider(ModEnchantmentTagProvider::new);
	}

	@Override
	public void buildRegistry(RegistryBuilder registryBuilder) {
		registryBuilder.addRegistry(RegistryKeys.ENCHANTMENT, ModEnchantmentProvider::bootstrap);
	}

	@Override
	public String getEffectiveModId() {
		return ShouyunWorkshop.MOD_ID;
	}
}
