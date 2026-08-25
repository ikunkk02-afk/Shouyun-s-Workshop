package com.shouyun.workshop.enchantment;

import com.shouyun.workshop.ShouyunWorkshop;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.World;

public final class ModEnchantments {
	public static final RegistryKey<Enchantment> WHIRLWIND_SLASH = RegistryKey.of(
			RegistryKeys.ENCHANTMENT, ShouyunWorkshop.id("whirlwind_slash"));

	public static int getWhirlwindLevel(World world, ItemStack stack) {
		return world.getRegistryManager().get(RegistryKeys.ENCHANTMENT)
				.getEntry(WHIRLWIND_SLASH)
				.map(entry -> EnchantmentHelper.getLevel(entry, stack))
				.orElse(0);
	}

	private ModEnchantments() {
	}
}
