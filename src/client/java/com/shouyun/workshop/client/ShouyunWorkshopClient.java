package com.shouyun.workshop.client;

import com.shouyun.workshop.enchantment.ModEnchantments;
import com.shouyun.workshop.entity.ModEntities;
import com.shouyun.workshop.network.WindFlightInputPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.entity.EmptyEntityRenderer;
import net.minecraft.client.util.InputUtil;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;

public final class ShouyunWorkshopClient implements ClientModInitializer {
	private static final KeyBinding TOGGLE_WIND_FLIGHT = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			"key.shouyun_workshop.toggle_wind_flight",
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_V,
			"key.categories.shouyun_workshop"));

	@Override
	public void onInitializeClient() {
		EntityRendererRegistry.register(ModEntities.SWORD_QI, EmptyEntityRenderer::new);
		ItemTooltipCallback.EVENT.register((stack, context, type, lines) -> {
			var client = net.minecraft.client.MinecraftClient.getInstance();
			if (client.world == null || !stack.isIn(ItemTags.SWORDS)) {
				return;
			}
			int level = ModEnchantments.getWhirlwindLevel(client.world, stack);
			if (level > 0) {
				lines.add(Text.translatable("tooltip.shouyun_workshop.whirlwind_slash." + Math.clamp(level, 1, 3))
						.formatted(Formatting.GRAY));
			}
		});
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			boolean toggleRequested = TOGGLE_WIND_FLIGHT.wasPressed();
			if (client.player == null || client.world == null || client.getNetworkHandler() == null) {
				return;
			}
			if (!client.player.getMainHandStack().isIn(ItemTags.SWORDS)
					|| ModEnchantments.getWhirlwindLevel(client.world, client.player.getMainHandStack()) <= 0
					|| !ClientPlayNetworking.canSend(WindFlightInputPayload.ID)) {
				return;
			}

			int flags = 0;
			if (client.options.forwardKey.isPressed()) flags |= WindFlightInputPayload.FORWARD;
			if (client.options.backKey.isPressed()) flags |= WindFlightInputPayload.BACKWARD;
			if (client.options.leftKey.isPressed()) flags |= WindFlightInputPayload.LEFT;
			if (client.options.rightKey.isPressed()) flags |= WindFlightInputPayload.RIGHT;
			if (client.options.jumpKey.isPressed()) flags |= WindFlightInputPayload.ASCEND;
			if (client.options.sneakKey.isPressed()) flags |= WindFlightInputPayload.DESCEND;
			if (toggleRequested) flags |= WindFlightInputPayload.TOGGLE;
			ClientPlayNetworking.send(new WindFlightInputPayload((byte) flags));
		});
	}
}
