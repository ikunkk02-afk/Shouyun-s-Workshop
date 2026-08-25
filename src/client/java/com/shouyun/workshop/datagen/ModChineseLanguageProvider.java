package com.shouyun.workshop.datagen;

import com.shouyun.workshop.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public final class ModChineseLanguageProvider extends FabricLanguageProvider {
	public ModChineseLanguageProvider(FabricDataOutput output,
			CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
		super(output, "zh_cn", registryLookup);
	}

	@Override
	public void generateTranslations(RegistryWrapper.WrapperLookup registryLookup, TranslationBuilder builder) {
		builder.add(ModItems.GLASS_HAMMER, "玻璃重锤");
		builder.add(ModItems.NETHERITE_HAMMER, "下界合金重锤");
		builder.add("enchantment.shouyun_workshop.whirlwind_slash", "旋风斩");
		builder.add("tooltip.shouyun_workshop.glass_hammer", "命中后破碎，并向周围散射伤人的玻璃碎片");
		builder.add("tooltip.shouyun_workshop.netherite_hammer", "命中后释放强力冲击波");
	}
}
