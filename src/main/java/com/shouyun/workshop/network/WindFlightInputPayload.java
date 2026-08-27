package com.shouyun.workshop.network;

import com.shouyun.workshop.ShouyunWorkshop;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record WindFlightInputPayload(byte flags) implements CustomPayload {
	public static final int FORWARD = 1;
	public static final int BACKWARD = 1 << 1;
	public static final int LEFT = 1 << 2;
	public static final int RIGHT = 1 << 3;
	public static final int MOVEMENT_MASK = FORWARD | BACKWARD | LEFT | RIGHT;
	public static final int VALID_MASK = MOVEMENT_MASK;

	public static final Id<WindFlightInputPayload> ID = new Id<>(ShouyunWorkshop.id("wind_flight_input"));
	public static final PacketCodec<RegistryByteBuf, WindFlightInputPayload> CODEC = PacketCodec.tuple(
			PacketCodecs.BYTE, WindFlightInputPayload::flags, WindFlightInputPayload::new);

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}
}
