package com.shouyun.workshop.item;

import com.shouyun.workshop.summon.NecromancerSummons;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import java.util.List;

public final class NecromancerStaffItem extends Item {
	private final NecromancerTier tier;
	public NecromancerStaffItem(NecromancerTier tier, Settings settings) { super(settings); this.tier = tier; }
	public NecromancerTier tier() { return tier; }

	@Override public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack stack = user.getStackInHand(hand);
		if (world.isClient) return TypedActionResult.success(stack);
		return NecromancerSummons.summon((ServerPlayerEntity) user, hand, tier)
				? TypedActionResult.success(stack, false) : TypedActionResult.fail(stack);
	}

	@Override public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> lines, TooltipType type) {
		lines.add(Text.translatable("tooltip.shouyun_workshop.staff", tier.limit, tier.cooldownSeconds, tier.lifetimeSeconds).formatted(Formatting.GRAY));
		lines.add(Text.translatable("tooltip.shouyun_workshop.staff." + tier.name().toLowerCase(java.util.Locale.ROOT)).formatted(Formatting.DARK_GREEN));
	}
}
