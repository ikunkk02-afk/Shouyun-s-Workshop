package com.shouyun.workshop.network;

import com.shouyun.workshop.flight.WindFlightManager;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public final class ModNetworking {
	public static void registerServer() {
		PayloadTypeRegistry.playC2S().register(WindFlightInputPayload.ID, WindFlightInputPayload.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(WindFlightInputPayload.ID,
				(payload, context) -> WindFlightManager.updateInput(context.player(), payload.flags()));
	}

	private ModNetworking() {
	}
}
