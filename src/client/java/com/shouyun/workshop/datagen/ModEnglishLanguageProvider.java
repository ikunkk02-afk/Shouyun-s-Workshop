package com.shouyun.workshop.datagen;

import com.shouyun.workshop.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public final class ModEnglishLanguageProvider extends FabricLanguageProvider {
	public ModEnglishLanguageProvider(FabricDataOutput output,
			CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
		super(output, registryLookup);
	}

	@Override
	public void generateTranslations(RegistryWrapper.WrapperLookup registryLookup, TranslationBuilder builder) {
		builder.add(ModItems.GLASS_HAMMER, "Glass Hammer");
		builder.add(ModItems.NETHERITE_HAMMER, "Netherite Hammer");
		builder.add("enchantment.shouyun_workshop.whirlwind_slash", "Whirlwind Slash");
		builder.add("tooltip.shouyun_workshop.glass_hammer", "Shatters on hit and scatters damaging glass shards");
		builder.add("tooltip.shouyun_workshop.netherite_hammer", "Releases a powerful shockwave on hit");
	}
}
