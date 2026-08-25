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
		builder.add("tooltip.shouyun_workshop.glass_hammer.1", "Powerful heavy strike");
		builder.add("tooltip.shouyun_workshop.glass_hammer.2", "Shatters after a successful hit");
		builder.add("tooltip.shouyun_workshop.glass_hammer.3", "Shards damage nearby creatures and the wielder");
		builder.add("tooltip.shouyun_workshop.netherite_hammer.1", "Heavy strikes create a small blast and shockwave");
		builder.add("tooltip.shouyun_workshop.netherite_hammer.2", "Launches nearby creatures");
		builder.add("tooltip.shouyun_workshop.netherite_hammer.3", "Its own shockwave cannot launch the wielder");
		builder.add("tooltip.shouyun_workshop.netherite_hammer.4", "Recoil briefly numbs the wielder's hand");
		builder.add("tooltip.shouyun_workshop.netherite_hammer.5", "Press N to toggle block destruction");
		builder.add("tooltip.shouyun_workshop.whirlwind_slash.1", "Area whirlwind slash");
		builder.add("tooltip.shouyun_workshop.whirlwind_slash.2", "Larger area and launches sword qi");
		builder.add("tooltip.shouyun_workshop.whirlwind_slash.3", "Maximum area with stronger sword qi");
		builder.add("tooltip.shouyun_workshop.wind_flight", "Press V to toggle wind flight");
		builder.add("key.shouyun_workshop.toggle_wind_flight", "Toggle Wind Flight");
		builder.add("key.shouyun_workshop.toggle_hammer_block_blast", "Toggle Hammer Block Destruction");
		builder.add("message.shouyun_workshop.hammer_block_blast.enabled", "Hammer block destruction: ON");
		builder.add("message.shouyun_workshop.hammer_block_blast.disabled", "Hammer block destruction: OFF");
		builder.add("key.categories.shouyun_workshop", "Shouyun's Workshop");
	}
}
