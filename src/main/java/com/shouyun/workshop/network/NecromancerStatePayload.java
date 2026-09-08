package com.shouyun.workshop.network;

import com.shouyun.workshop.ShouyunWorkshop;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import java.util.List;
import java.util.ArrayList;
import java.util.UUID;

public record NecromancerStatePayload(int cooldownTicks, List<Entry> summons) implements CustomPayload {
	public record Entry(UUID uuid, int tier, int remainingTicks) {}
	public static final Id<NecromancerStatePayload> ID = new Id<>(ShouyunWorkshop.id("necromancer_state"));
	public static final PacketCodec<RegistryByteBuf, NecromancerStatePayload> CODEC = new PacketCodec<>() {
		@Override public NecromancerStatePayload decode(RegistryByteBuf buf) {
			int cooldown = Math.clamp(buf.readVarInt(), 0, 160);
			int count = buf.readVarInt();
			if (count < 0 || count > 4) throw new IllegalArgumentException("Invalid summon count");
			List<Entry> entries = new ArrayList<>(count);
			for (int i = 0; i < count; i++) entries.add(new Entry(buf.readUuid(), Math.clamp(buf.readVarInt(), 0, 5), Math.clamp(buf.readVarInt(), 0, 2400)));
			return new NecromancerStatePayload(cooldown, List.copyOf(entries));
		}
		@Override public void encode(RegistryByteBuf buf, NecromancerStatePayload value) {
			buf.writeVarInt(value.cooldownTicks());
			buf.writeVarInt(value.summons().size());
			for (Entry entry : value.summons()) { buf.writeUuid(entry.uuid()); buf.writeVarInt(entry.tier()); buf.writeVarInt(entry.remainingTicks()); }
		}
	};
	@Override public Id<? extends CustomPayload> getId() { return ID; }
}
