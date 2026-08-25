package com.shouyun.workshop.item;

import com.shouyun.workshop.handler.HammerHandler;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public final class NetheriteHammerItem extends HammerItem {
	public NetheriteHammerItem(Settings settings) {
		super(settings);
	}

	@Override
	public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		if (attacker instanceof ServerPlayerEntity player) {
			HammerHandler.createNetheriteShockwave(player, target);
		}
		stack.damage(1, attacker, EquipmentSlot.MAINHAND);
		return true;
	}

	@Override
	public boolean canRepair(ItemStack stack, ItemStack ingredient) {
		return ingredient.isOf(Items.NETHERITE_INGOT) || super.canRepair(stack, ingredient);
	}

	@Override
	public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
		tooltip.add(Text.translatable("tooltip.shouyun_workshop.netherite_hammer").formatted(Formatting.GRAY));
	}
}
