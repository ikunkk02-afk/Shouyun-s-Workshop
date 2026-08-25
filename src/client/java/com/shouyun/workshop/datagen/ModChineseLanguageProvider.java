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
		builder.add("tooltip.shouyun_workshop.glass_hammer.1", "强力重击");
		builder.add("tooltip.shouyun_workshop.glass_hammer.2", "命中后破碎");
		builder.add("tooltip.shouyun_workshop.glass_hammer.3", "碎片会伤害周围生物与使用者");
		builder.add("tooltip.shouyun_workshop.netherite_hammer.1", "重击产生冲击波");
		builder.add("tooltip.shouyun_workshop.netherite_hammer.2", "击飞周围生物");
		builder.add("tooltip.shouyun_workshop.netherite_hammer.3", "使用者不会被自身冲击波震飞");
		builder.add("tooltip.shouyun_workshop.netherite_hammer.4", "反作用力会使手短暂发麻");
		builder.add("tooltip.shouyun_workshop.whirlwind_slash.1", "范围旋风斩");
		builder.add("tooltip.shouyun_workshop.whirlwind_slash.2", "扩大范围并释放剑气");
		builder.add("tooltip.shouyun_workshop.whirlwind_slash.3", "原地蓄力后可以御风飞行");
		builder.add("key.shouyun_workshop.toggle_wind_flight", "切换御风飞行");
		builder.add("key.categories.shouyun_workshop", "寿云工坊");
	}
}
