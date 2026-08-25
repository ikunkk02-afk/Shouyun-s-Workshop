package com.shouyun.workshop.network;

import com.shouyun.workshop.flight.WindFlightManager;
import com.shouyun.workshop.handler.HammerHandler;
import com.shouyun.workshop.item.ModItems;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public final class ModNetworking {
	public static void registerServer() {
		PayloadTypeRegistry.playC2S().register(WindFlightInputPayload.ID, WindFlightInputPayload.CODEC);
		PayloadTypeRegistry.playC2S().register(HammerBlastModePayload.ID, HammerBlastModePayload.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(WindFlightInputPayload.ID,
				(payload, context) -> {
					byte flags = (byte) (payload.flags() & WindFlightInputPayload.VALID_MASK);
					if ((flags & WindFlightInputPayload.TOGGLE) != 0) {
						WindFlightManager.toggle(context.player());
					}
					WindFlightManager.updateInput(context.player(), flags);
				});
		ServerPlayNetworking.registerGlobalReceiver(HammerBlastModePayload.ID, (payload, context) -> {
			if (context.player().getMainHandStack().isOf(ModItems.NETHERITE_HAMMER)) {
				HammerHandler.toggleBlockDestruction(context.player());
			}
		});
		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> HammerHandler.clearPlayer(handler.player));
	}

	private ModNetworking() {
	}
}
