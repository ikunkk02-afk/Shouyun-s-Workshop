package com.shouyun.workshop.network;

import com.shouyun.workshop.ShouyunWorkshop;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record HammerBlastModePayload() implements CustomPayload {
	public static final HammerBlastModePayload INSTANCE = new HammerBlastModePayload();
	public static final Id<HammerBlastModePayload> ID = new Id<>(ShouyunWorkshop.id("hammer_blast_mode"));
	public static final PacketCodec<RegistryByteBuf, HammerBlastModePayload> CODEC = PacketCodec.unit(INSTANCE);

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}
}
