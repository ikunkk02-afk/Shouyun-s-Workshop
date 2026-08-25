package com.shouyun.workshop.network;

import com.shouyun.workshop.flight.WindFlightManager;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public final class ModNetworking {
	public static void registerServer() {
		PayloadTypeRegistry.playC2S().register(WindFlightInputPayload.ID, WindFlightInputPayload.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(WindFlightInputPayload.ID,
				(payload, context) -> {
					byte flags = (byte) (payload.flags() & WindFlightInputPayload.VALID_MASK);
					if ((flags & WindFlightInputPayload.TOGGLE) != 0) {
						WindFlightManager.toggle(context.player());
					}
					WindFlightManager.updateInput(context.player(), flags);
				});
	}

	private ModNetworking() {
	}
}
