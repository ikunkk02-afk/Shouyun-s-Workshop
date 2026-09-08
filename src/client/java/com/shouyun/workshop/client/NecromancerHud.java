package com.shouyun.workshop.client;

import com.shouyun.workshop.ShouyunWorkshop;
import com.shouyun.workshop.item.NecromancerStaffItem;
import com.shouyun.workshop.item.NecromancerTier;
import com.shouyun.workshop.network.NecromancerStatePayload;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import java.util.List;

public final class NecromancerHud {
	private static final Identifier PANEL = ShouyunWorkshop.id("textures/gui/necromancer_panel.png");
	private static final Identifier SOUL = ShouyunWorkshop.id("textures/gui/necromancer_soul.png");
	private static final Identifier CLOCK = ShouyunWorkshop.id("textures/gui/necromancer_cooldown.png");
	private static NecromancerStatePayload state = new NecromancerStatePayload(0, List.of());
	private static int elapsed;
	public static void register() {
		ClientPlayNetworking.registerGlobalReceiver(NecromancerStatePayload.ID, (payload, context) ->
				context.client().execute(() -> { state = payload; elapsed = 0; }));
		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> { state = new NecromancerStatePayload(0, List.of()); elapsed = 0; });
		ClientTickEvents.END_CLIENT_TICK.register(client -> { if (client.world != null && !client.isPaused()) elapsed++; });
		HudRenderCallback.EVENT.register((context, tickCounter) -> render(context));
	}
	private static void render(DrawContext context) {
		var client = MinecraftClient.getInstance();
		if (client.player == null || client.options.hudHidden || client.player.isSpectator()) return;
		var main = client.player.getMainHandStack().getItem();
		var off = client.player.getOffHandStack().getItem();
		NecromancerStaffItem held = main instanceof NecromancerStaffItem staff ? staff : off instanceof NecromancerStaffItem staff ? staff : null;
		int cooldown = Math.max(0, state.cooldownTicks() - elapsed);
		if (held == null && state.summons().isEmpty() && cooldown == 0) return;
		Text count = held == null ? Text.translatable("hud.shouyun_workshop.staff.count", state.summons().size())
				: Text.translatable("hud.shouyun_workshop.staff.limit", state.summons().size(), held.tier().limit);
		Text clock = cooldown == 0 ? Text.translatable("hud.shouyun_workshop.staff.ready")
				: Text.translatable("hud.shouyun_workshop.staff.cooldown", seconds(cooldown));
		var rows = state.summons().stream().map(entry -> Text.translatable("hud.shouyun_workshop.staff.summon",
				Text.translatable("tier.shouyun_workshop." + NecromancerTier.values()[entry.tier()].name().toLowerCase(java.util.Locale.ROOT)),
				seconds(Math.max(0, entry.remainingTicks() - elapsed)))).toList();
		int width = Math.max(160, Math.max(client.textRenderer.getWidth(count), client.textRenderer.getWidth(clock)) + 34);
		for (Text row : rows) width = Math.max(width, client.textRenderer.getWidth(row) + 18);
		int height = 40 + rows.size() * 11;
		float scale = Math.min(1F, (float) (client.getWindow().getScaledWidth() - 8) / width);
		context.getMatrices().push();
		context.getMatrices().translate(4, 4, 0);
		context.getMatrices().scale(scale, scale, 1);
		context.drawTexture(PANEL, 0, 0, width, height, 0, 0, 160, 80, 160, 80);
		context.drawTexture(SOUL, 6, 6, 0, 0, 16, 16, 16, 16);
		context.drawTexture(CLOCK, 6, 22, 0, 0, 16, 16, 16, 16);
		context.drawTextWithShadow(client.textRenderer, count, 26, 9, 0xE3E7DA);
		context.drawTextWithShadow(client.textRenderer, clock, 26, 25, 0x92D3BB);
		for (int i = 0; i < rows.size(); i++) context.drawTextWithShadow(client.textRenderer, rows.get(i), 8, 39 + i * 11, 0xD0D0C5);
		context.getMatrices().pop();
	}
	private static int seconds(int ticks) { return (ticks + 19) / 20; }
	private NecromancerHud() {}
}
