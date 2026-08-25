package com.shouyun.workshop.client;

import com.shouyun.workshop.enchantment.ModEnchantments;
import com.shouyun.workshop.entity.ModEntities;
import com.shouyun.workshop.network.WindFlightInputPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.EmptyEntityRenderer;
import net.minecraft.registry.tag.ItemTags;

public final class ShouyunWorkshopClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		EntityRendererRegistry.register(ModEntities.SWORD_QI, EmptyEntityRenderer::new);
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (client.player == null || client.world == null || client.getNetworkHandler() == null) {
				return;
			}
			if (!client.player.getMainHandStack().isIn(ItemTags.SWORDS)
					|| ModEnchantments.getWhirlwindLevel(client.world, client.player.getMainHandStack()) < 3
					|| !ClientPlayNetworking.canSend(WindFlightInputPayload.ID)) {
				return;
			}

			int flags = 0;
			if (client.options.forwardKey.isPressed()) flags |= WindFlightInputPayload.FORWARD;
			if (client.options.backKey.isPressed()) flags |= WindFlightInputPayload.BACKWARD;
			if (client.options.leftKey.isPressed()) flags |= WindFlightInputPayload.LEFT;
			if (client.options.rightKey.isPressed()) flags |= WindFlightInputPayload.RIGHT;
			ClientPlayNetworking.send(new WindFlightInputPayload((byte) flags));
		});
	}
}
